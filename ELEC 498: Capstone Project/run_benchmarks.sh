#!/bin/bash
#
# run_benchmarks.sh
# Run each synthetic workload twice — first under the default CPU frequency
# profile, then with the region-appropriate cpupower profile — and report the
# runtime and energy delta between the two runs.
#
# Requires: cpupower, bc, CAP_SYS_ADMIN (root) for frequency writes
# RAPL energy counters must be readable at /sys/class/powercap/intel-rapl:0/
#
# Build workloads before running:
#   gcc -O2 -mfma -mavx2 -o serial_workload serial_workload.c
#   gcc -O3 -fopenmp -mfma -mavx2 -o parallel_workload parallel_workload.c

set -euo pipefail

LOG=results.csv
# Enumerate only CPUs that actually expose a cpufreq interface.
# nproc --all includes placeholder entries that have no scaling files.
FREQ_CPUS=($(ls -d /sys/devices/system/cpu/cpu*/cpufreq 2>/dev/null \
    | grep -oE 'cpu[0-9]+' | grep -oE '[0-9]+' | sort -n))
NUM_CPUS=${#FREQ_CPUS[@]}
LAST_CPU=${FREQ_CPUS[-1]}

# ---------------------------------------------------------------------------
# Environment check — cpufreq sysfs is only present on bare-metal Linux.
# WSL2 and most VMs do not expose these interfaces.
# ---------------------------------------------------------------------------
ok=1
command -v cpupower > /dev/null 2>&1 \
    || { echo "ERROR: cpupower not found (install linux-tools-$(uname -r))"; ok=0; }
[ -f /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq ] \
    || { echo "ERROR: cpufreq sysfs not available (bare-metal Linux required)"; ok=0; }
[ "$ok" -eq 1 ] || { echo "This script must run on a bare-metal Linux system."; exit 1; }

# ---------------------------------------------------------------------------
# Energy counter detection — probed in order of preference.
#   1. Intel RAPL (powercap)      — Intel CPUs
#   2. AMD RAPL  (powercap)       — AMD Zen 3+ on kernel 5.17+
#   3. amd_energy hwmon           — AMD Zen 2+ via kernel module
# All three expose the counter in µJ.  If none are found, energy columns
# are omitted from the output but timing still runs.
# ---------------------------------------------------------------------------
find_energy_path() {
    [ -f /sys/class/powercap/intel-rapl:0/energy_uj ] \
        && { echo /sys/class/powercap/intel-rapl:0/energy_uj; return; }
    [ -f /sys/class/powercap/amd-rapl:0/energy_uj ] \
        && { echo /sys/class/powercap/amd-rapl:0/energy_uj; return; }
    # amd_energy hwmon: energy1_input is the package energy counter
    local p
    for p in /sys/class/hwmon/hwmon*/energy1_input; do
        [ -f "$p" ] && { echo "$p"; return; }
    done
    echo ""
}

RAPL=$(find_energy_path)
if [ -z "$RAPL" ]; then
    echo "WARNING: no energy counter found — energy columns will be skipped."
    HAVE_ENERGY=0
else
    echo "Energy counter: $RAPL"
    HAVE_ENERGY=1
fi

# Hardware frequency bounds in kHz.
# HW_MIN: absolute hardware minimum from cpuinfo.
# HW_MAX: highest available P-state from scaling_available_frequencies, which
#         includes boost clocks that cpuinfo_max_freq omits on AMD acpi-cpufreq.
#         Falls back to cpuinfo_max_freq if the file is absent (amd-pstate active).
HW_MIN=$(cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq)
AVAIL=/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies
if [ -f "$AVAIL" ]; then
    HW_MAX=$(tr ' ' '\n' < "$AVAIL" | grep -v '^$' | sort -rn | head -1)
else
    HW_MAX=$(cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq)
fi
RANGE=$((HW_MAX - HW_MIN))

# Derived targets as percentages of [HW_MIN, HW_MAX], matching the profiles
# defined in pwr_region_hint.hpp.
FREQ_75=$(( HW_MIN + 3 * RANGE / 4 ))

# ---------------------------------------------------------------------------
# apply_hint <hint>
#   Set cpufreq scaling limits via cpupower to match the named hint profile.
#   Frequencies are passed to cpupower in kHz using the KHz suffix.
#   A brief sleep follows to let the governor settle at the new limits.
# ---------------------------------------------------------------------------
apply_hint() {
    case $1 in
        DEFAULT)
            # Restore the full hardware range — governor has unrestricted control.
            cpupower frequency-set -d "${HW_MIN}KHz" -u "${HW_MAX}KHz" > /dev/null
            # Re-enable all C-states on every core (undo any changes made by SERIAL).
            cpupower idle-set -E > /dev/null 2>&1 || true
            sleep 0.15
            ;;
        SERIAL)
            # Pin core 0 to the max P-state for single-thread speed.
            cpupower -c 0             frequency-set -d "${HW_MAX}KHz" -u "${HW_MAX}KHz" > /dev/null
            # Pin idle cores to the lowest P-state.
            cpupower -c "1-$LAST_CPU" frequency-set -d "${HW_MIN}KHz" -u "${HW_MIN}KHz" > /dev/null
            # Drive idle cores into the deepest available C-state.
            # Count cpuidle states on cpu1 (representative idle core), then
            # enable all states and disable every one except the last (deepest).
            # This works regardless of how many states the hardware exposes:
            #   3 states (POLL/C1/C2)       → disables 0,1   → leaves C2
            #   4 states (POLL/C1/C2/C6)    → disables 0,1,2 → leaves C6
            CSTATE_COUNT=$(ls -d /sys/devices/system/cpu/cpu1/cpuidle/state* \
                           2>/dev/null | wc -l)
            cpupower -c "1-$LAST_CPU" idle-set -E > /dev/null 2>&1 || true
            for (( _s=0; _s<CSTATE_COUNT-1; _s++ )); do
                cpupower -c "1-$LAST_CPU" idle-set -d "$_s" > /dev/null 2>&1 || true
            done
            # Longer settle: idle cores need time to drain their run-queues and
            # descend through the C-state hierarchy into C6/CC6.
            sleep 1
            ;;
        PARALLEL)
            # All cores at high frequency to sustain parallel throughput.
            cpupower frequency-set -d "${FREQ_75}KHz" -u "${HW_MAX}KHz" > /dev/null
            sleep 0.15
            ;;
    esac
}

# ---------------------------------------------------------------------------
# measure_run <binary> [cpu]
#   Run <binary>, bracketed by energy and wall-clock reads.
#   Optional [cpu] pins the process to that core via taskset.
#   Prints two values on one line: elapsed_ms energy_uj
#   energy_uj is 0 when no energy counter is available.
# ---------------------------------------------------------------------------
measure_run() {
    local binary=$1 cpu=${2:-""}
    local t_start t_end elapsed_ms
    local e_start=0 e_end=0 energy_uj=0

    [ "$HAVE_ENERGY" -eq 1 ] && e_start=$(cat "$RAPL")
    t_start=$(date '+%s%N')

    if [ -n "$cpu" ]; then
        taskset -c "$cpu" "$binary" > /dev/null
    else
        "$binary" > /dev/null
    fi

    t_end=$(date '+%s%N')
    [ "$HAVE_ENERGY" -eq 1 ] && e_end=$(cat "$RAPL")

    elapsed_ms=$(( (t_end - t_start) / 1000000 ))

    if [ "$HAVE_ENERGY" -eq 1 ]; then
        energy_uj=$(( e_end - e_start ))
        # Handle counter wraparound (max_energy_range_uj if present, else 2^32 µJ)
        if (( energy_uj < 0 )); then
            local max_range
            max_range=$(cat "$(dirname "$RAPL")/max_energy_range_uj" 2>/dev/null || echo 4294967296)
            energy_uj=$(( max_range - e_start + e_end ))
        fi
    fi

    echo "$elapsed_ms $energy_uj"
}

# ---------------------------------------------------------------------------
# compare <name> <binary> <hint> [cpu]
#   Run <binary> under DEFAULT then <hint>, print the side-by-side result,
#   and append both rows to results.csv.
#   Optional [cpu] pins the workload to that core via taskset.
# ---------------------------------------------------------------------------
compare() {
    local name=$1 binary=$2 hint=$3 cpu=${4:-""}
    local def_ms def_uj hint_ms hint_uj
    local ts
    ts=$(date '+%Y-%m-%d %H:%M:%S')

    printf '\n── %s ─────────────────────────────────────\n' "$name"

    printf '  DEFAULT  ... '
    apply_hint DEFAULT
    IFS=' ' read -r def_ms  def_uj  <<< "$(measure_run "$binary" "$cpu")"
    if [ "$HAVE_ENERGY" -eq 1 ]; then
        printf '%d ms   %.3f J\n' "$def_ms" "$(echo "scale=3; $def_uj / 1000000" | bc)"
    else
        printf '%d ms\n' "$def_ms"
    fi

    printf '  %-9s... ' "$hint"
    apply_hint "$hint"
    IFS=' ' read -r hint_ms hint_uj <<< "$(measure_run "$binary" "$cpu")"
    if [ "$HAVE_ENERGY" -eq 1 ]; then
        printf '%d ms   %.3f J\n' "$hint_ms" "$(echo "scale=3; $hint_uj / 1000000" | bc)"
    else
        printf '%d ms\n' "$hint_ms"
    fi

    # Always restore before moving to the next benchmark.
    apply_hint DEFAULT

    # Percentage deltas — positive means the hint improved over default.
    local dt dj
    dt=$(echo "scale=1; 100 * ($def_ms - $hint_ms) / $def_ms" | bc)
    if [ "$HAVE_ENERGY" -eq 1 ] && (( def_uj > 0 )); then
        dj=$(echo "scale=1; 100 * ($def_uj - $hint_uj) / $def_uj" | bc)
        printf '  Δ time: %+.1f%%   Δ energy: %+.1f%%\n' "$dt" "$dj"
    else
        printf '  Δ time: %+.1f%%\n' "$dt"
        dj="N/A"
    fi

    # Append to CSV log.
    printf '%s, %s, DEFAULT, %d, %s\n' \
        "$ts" "$name" "$def_ms" \
        "$([ "$HAVE_ENERGY" -eq 1 ] && echo "scale=3; $def_uj / 1000000" | bc || echo 'N/A')" >> "$LOG"
    printf '%s, %s, %s, %d, %s\n' \
        "$ts" "$name" "$hint" "$hint_ms" \
        "$([ "$HAVE_ENERGY" -eq 1 ] && echo "scale=3; $hint_uj / 1000000" | bc || echo 'N/A')" >> "$LOG"
}

# ---------------------------------------------------------------------------
# Build workloads from source if the binaries are missing or stale.
# ---------------------------------------------------------------------------
build_if_needed() {
    local src=$1 bin=$2 flags=$3
    if [ ! -f "$bin" ] || [ "$src" -nt "$bin" ]; then
        printf 'Building %s... ' "$bin"
        gcc $flags -o "$bin" "$src"
        echo "done"
    fi
}

build_if_needed serial_workload.c   serial_workload   "-O2 -mfma -mavx2"
build_if_needed parallel_workload.c parallel_workload "-O3 -fopenmp -mfma -mavx2"

# ---------------------------------------------------------------------------

[ ! -f "$LOG" ] && echo 'timestamp, benchmark, hint, elapsed_ms, energy_J' > "$LOG"

echo "CPUs: $NUM_CPUS   HW range: ${HW_MIN}–${HW_MAX} kHz   energy: ${RAPL:-none}"

compare serial   ./serial_workload   SERIAL   0   # pin to core 0 — the boosted core
compare parallel ./parallel_workload PARALLEL      # no affinity — uses all cores

printf '\nResults appended to %s\n' "$LOG"
