#!/bin/bash
#
# diagnose.sh
# Verify the SERIAL hint is actually taking effect by sampling CPU state
# while the serial workload runs:
#   1. Process is pinned to core 0
#   2. Core 0 is running at its maximum (boost) frequency
#   3. Idle cores are in deep C-states
#
# Run as root alongside run_benchmarks.sh, or standalone:
#   sudo ./diagnose.sh

set -euo pipefail

FREQ_CPUS=($(ls -d /sys/devices/system/cpu/cpu*/cpufreq 2>/dev/null \
    | grep -oE 'cpu[0-9]+' | grep -oE '[0-9]+' | sort -n))
LAST_CPU=${FREQ_CPUS[-1]}
HW_MIN=$(cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq)
AVAIL=/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies
if [ -f "$AVAIL" ]; then
    HW_MAX=$(tr ' ' '\n' < "$AVAIL" | grep -v '^$' | sort -rn | head -1)
else
    HW_MAX=$(cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq)
fi

# ---------------------------------------------------------------------------
# 1. Show what frequency limits are actually set after apply_hint SERIAL
# ---------------------------------------------------------------------------
echo "=== Applying SERIAL hint ==="
cpupower -c 0             frequency-set -d "${HW_MAX}KHz" -u "${HW_MAX}KHz" > /dev/null
cpupower -c "1-$LAST_CPU" frequency-set -d "${HW_MIN}KHz" -u "${HW_MIN}KHz" > /dev/null
CSTATE_COUNT=$(ls -d /sys/devices/system/cpu/cpu1/cpuidle/state* 2>/dev/null | wc -l)
cpupower -c "1-$LAST_CPU" idle-set -E > /dev/null 2>&1 || true
for (( _s=0; _s<CSTATE_COUNT-1; _s++ )); do
    cpupower -c "1-$LAST_CPU" idle-set -d "$_s" > /dev/null 2>&1 || true
done
sleep 1   # allow idle cores to settle into deepest C-state

echo ""
echo "=== Available P-states on cpu0 (highest = boost target) ==="
[ -f "$AVAIL" ] && cat "$AVAIL" | tr ' ' '\n' | grep -v '^$' | sort -rn \
    | awk '{printf "  %d MHz\n", $1/1000}' | head -6

echo ""
echo "=== Scaling limits after hint (min / max) ==="
for cpu in "${FREQ_CPUS[@]}"; do
    min=$(cat /sys/devices/system/cpu/cpu${cpu}/cpufreq/scaling_min_freq)
    max=$(cat /sys/devices/system/cpu/cpu${cpu}/cpufreq/scaling_max_freq)
    printf "  cpu%-3d  min: %4d MHz   max: %4d MHz\n" \
        "$cpu" "$((min/1000))" "$((max/1000))"
done

# ---------------------------------------------------------------------------
# 2. Launch workload pinned to core 0, sample state mid-run
# ---------------------------------------------------------------------------
echo ""
echo "=== Launching serial workload (taskset -c 0) ==="
taskset -c 0 ./serial_workload > /dev/null &
BENCH_PID=$!
sleep 0.3   # let it reach steady state

echo ""
echo "=== Process CPU affinity ==="
taskset -p "$BENCH_PID"
# Field 39 of /proc/PID/stat is the last CPU the process was scheduled on
LAST_CPU_RAN=$(awk '{print $39}' /proc/"$BENCH_PID"/stat)
echo "  Last scheduled on CPU: $LAST_CPU_RAN"

# ---------------------------------------------------------------------------
# 3. Sample actual running frequencies
# ---------------------------------------------------------------------------
echo ""
echo "=== Actual running frequencies (scaling_cur_freq) ==="
for cpu in "${FREQ_CPUS[@]}"; do
    cur=$(cat /sys/devices/system/cpu/cpu${cpu}/cpufreq/scaling_cur_freq)
    printf "  cpu%-3d  %4d MHz\n" "$cpu" "$((cur/1000))"
done

# ---------------------------------------------------------------------------
# 4. C-state residency snapshot — compare before/after a 1s window
# ---------------------------------------------------------------------------
echo ""
echo "=== C-state residency over 1 s window (idle cores 1–4 sample) ==="

declare -A time_before
for cpu in 1 2 3 4; do
    for state_dir in /sys/devices/system/cpu/cpu${cpu}/cpuidle/state*; do
        [ -d "$state_dir" ] || continue
        key="cpu${cpu}_$(basename "$state_dir")"
        time_before[$key]=$(cat "$state_dir/time")
    done
done

sleep 1

for cpu in 1 2 3 4; do
    printf "  cpu%d:\n" "$cpu"
    for state_dir in /sys/devices/system/cpu/cpu${cpu}/cpuidle/state*; do
        [ -d "$state_dir" ] || continue
        name=$(cat "$state_dir/name")
        key="cpu${cpu}_$(basename "$state_dir")"
        time_after=$(cat "$state_dir/time")
        delta_us=$(( time_after - ${time_before[$key]} ))
        printf "    %-8s  %6d ms in state\n" "$name" "$((delta_us / 1000))"
    done
done

wait "$BENCH_PID" 2>/dev/null || true

# ---------------------------------------------------------------------------
# 5. Restore
# ---------------------------------------------------------------------------
echo ""
echo "=== Restoring DEFAULT ==="
cpupower frequency-set -d "${HW_MIN}KHz" -u "${HW_MAX}KHz" > /dev/null
cpupower idle-set -E > /dev/null 2>&1 || true
echo "Done."
