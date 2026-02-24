// pwr_region_hint.hpp
// Power API - Region Hint Implementation
//
// Implements PWR_AppHintRegion(), an inline C++ function that translates a
// PWR_RegionHint into a cpufreq governor profile via libcpupower.
//
// Call PWR_Init() once at program start to discover hardware frequency bounds.
// Then call PWR_AppHintRegion() at each region boundary.
//
// Requires CAP_SYS_ADMIN or root privileges.
// Link with: -lcpupower

#pragma once

extern "C" {
#include <cpupower.h>   // cpupower_is_cpu_online
#include <cpufreq.h>    // cpufreq_get_policy, cpufreq_put_policy,
                        // cpufreq_modify_policy_min, cpufreq_modify_policy_max,
                        // cpufreq_get_hardware_limits
#include <cpuidle.h>    // cpuidle_state_count, cpuidle_state_disable
}
#include <unistd.h>     // sysconf
#include <climits>      // ULONG_MAX

// ---------------------------------------------------------------------------
// PWR_RegionHint - from Power API specification (§ PWR_RegionHint)
// ---------------------------------------------------------------------------
typedef enum {
    PWR_REGION_DEFAULT       =  0,  // Restore default; hint withdrawn
    PWR_REGION_SERIAL        =  1,  // Single-threaded serial execution
    PWR_REGION_PARALLEL      =  2,  // Multi-core parallel execution
    PWR_REGION_COMPUTE       =  3,  // Compute-intensive workload
    PWR_REGION_COMMUNICATE   =  4,  // Network / inter-process communication
    PWR_REGION_IO            =  5,  // I/O bound (disk / storage)
    PWR_REGION_MEM_BOUND     =  6,  // Memory bandwidth bound
    PWR_REGION_GLOBAL_LOOP   =  7,  // Outer loop spanning mixed regions
    PWR_NUM_REGION_HINTS     =  8,
    PWR_REGION_INVALID       = -1,
    PWR_REGION_NOT_SPECIFIED = -2
} PWR_RegionHint;

typedef enum {
    PWR_RET_SUCCESS         =  0,
    PWR_RET_FAILURE         = -1,
    PWR_RET_NOT_IMPLEMENTED = -2
} PWR_Status;

// ---------------------------------------------------------------------------
// Hardware frequency bounds — populated once by PWR_Init().
// All hint profiles are derived from these values rather than hardcoded kHz.
// ---------------------------------------------------------------------------
static unsigned long g_hw_min_khz = 0;
static unsigned long g_hw_max_khz = 0;

// Discover and cache the hardware frequency limits from CPU 0.
// Must be called once before PWR_AppHintRegion().
//
// cpufreq_get_hardware_limits() returns cpuinfo_min/max_freq, which on AMD
// systems using acpi-cpufreq reports the base clock rather than the boost.
// cpufreq_get_available_frequencies() enumerates all P-states including boost,
// so we use it to find the true maximum.  Falls back to hardware limits if the
// available-frequencies list is not exposed (e.g. amd-pstate active mode).
inline PWR_Status PWR_Init() {
    struct cpufreq_available_frequencies *freqs =
        cpufreq_get_available_frequencies(0);

    if (freqs) {
        g_hw_max_khz = 0;
        g_hw_min_khz = ULONG_MAX;
        for (auto *p = freqs; p; p = p->next) {
            if (p->frequency > g_hw_max_khz) g_hw_max_khz = p->frequency;
            if (p->frequency < g_hw_min_khz) g_hw_min_khz = p->frequency;
        }
        cpufreq_put_available_frequencies(freqs);
        if (g_hw_max_khz > 0 && g_hw_min_khz != ULONG_MAX)
            return PWR_RET_SUCCESS;
    }

    // Fallback: hardware limits (may miss boost on some AMD drivers)
    return (cpufreq_get_hardware_limits(0, &g_hw_min_khz, &g_hw_max_khz) == 0)
        ? PWR_RET_SUCCESS : PWR_RET_FAILURE;
}

// ---------------------------------------------------------------------------
// Internal helpers (not part of the public API)
// ---------------------------------------------------------------------------
namespace pwr_internal {

// Number of configured logical CPUs.
static inline int numCPUs() {
    return static_cast<int>(sysconf(_SC_NPROCESSORS_CONF));
}

// Interpolate a frequency at pct% of the hardware range.
//   freqAtPct(0)   == g_hw_min_khz
//   freqAtPct(100) == g_hw_max_khz
static inline unsigned long freqAtPct(unsigned int pct) {
    return g_hw_min_khz + (unsigned long)pct * (g_hw_max_khz - g_hw_min_khz) / 100;
}

// Set scaling_min_freq and/or scaling_max_freq for one CPU via libcpupower.
// Pass 0 for min_khz or max_khz to leave that limit unchanged.
// Offline CPUs are silently skipped.
//
// Write ordering:
//   cpufreq_modify_policy_min/max each do an independent sysfs write.
//   The kernel clamps a new min to [hw_min, current_max] and a new max to
//   [current_min, hw_max], so the order matters when the new range does not
//   overlap with the current range.  The safe rule is:
//     - If new_min > current_max  →  raise max first, then min
//     - Otherwise                 →  lower/set min first, then max
static inline bool setCoreFreq(unsigned int cpu,
                                unsigned long min_khz,
                                unsigned long max_khz) {
    if (!cpupower_is_cpu_online(cpu))
        return true;

    // Single-limit cases need no ordering consideration.
    if (min_khz == 0 && max_khz == 0) return true;
    if (min_khz == 0) return (cpufreq_modify_policy_max(cpu, max_khz) == 0);
    if (max_khz == 0) return (cpufreq_modify_policy_min(cpu, min_khz) == 0);

    // Both limits: read current max to determine safe write order.
    struct cpufreq_policy *cur = cpufreq_get_policy(cpu);
    if (!cur) return false;
    unsigned long cur_max = cur->max;
    cpufreq_put_policy(cur);

    bool ok = true;
    if (min_khz > cur_max) {
        // New min exceeds current max: raise max first to make room,
        // then raise min.
        ok &= (cpufreq_modify_policy_max(cpu, max_khz) == 0);
        ok &= (cpufreq_modify_policy_min(cpu, min_khz) == 0);
    } else {
        // New min fits within current max: lower/set min first,
        // then adjust max in either direction.
        ok &= (cpufreq_modify_policy_min(cpu, min_khz) == 0);
        ok &= (cpufreq_modify_policy_max(cpu, max_khz) == 0);
    }
    return ok;
}

// Enable all C-states on a CPU so the hardware can choose the deepest sleep
// (C6/CC6 on AMD Zen, C6 on Intel) when the core is idle.
// cpuidle_state_disable(cpu, state, 0) means enable (disable=false).
static inline void enableAllCStates(unsigned int cpu) {
    int count = cpuidle_state_count(cpu);
    for (int s = 0; s < count; s++)
        cpuidle_state_disable(cpu, s, 0);
}

// Force the deepest available C-state (C6/CC6) on an idle CPU by disabling
// shallow states (POLL=0, C1=1, C2=2).  The last state is always kept enabled.
// Call enableAllCStates() first so that any previously disabled states are
// cleared before we selectively re-disable the shallow ones.
static inline void forceDeepCState(unsigned int cpu) {
    int count = cpuidle_state_count(cpu);
    // Disable all states except the last (deepest) one.
    for (int s = 0; s < count - 1; s++)
        cpuidle_state_disable(cpu, s, 1);   // 1 = disable
}

} // namespace pwr_internal

// ---------------------------------------------------------------------------
// PWR_AppHintRegion
//
// Apply the cpufreq profile that best matches the given region hint.
// Called inline at the boundary of each execution region.
//
// All frequency targets are derived from the hardware limits discovered by
// PWR_Init() rather than hardcoded values, making the implementation
// portable across CPU models with different frequency ranges.
//
// Profiles (expressed as % of the [hw_min, hw_max] hardware range):
//
//   DEFAULT      : all cores → [0%, 100%]   restore full governor range
//   SERIAL       : core 0   → pinned to 100%  (max P-state)
//                  cores 1+ → pinned to   0%  (lowest P-state; min=max=hw_min)
//   PARALLEL     : all cores → [75%, 100%]  sustain high parallel throughput
//   COMPUTE      : all cores → [75%, 100%]  same as PARALLEL
//   COMMUNICATE  : core 0   → [67%, 100%]  dedicated network core
//                  cores 1+ → [ 0%,  17%]  parked
//   IO           : all cores → [  -,  67%]  cap max; CPU idles on I/O
//   MEM_BOUND    : all cores → [ 0%,  33%]  reduce freq; memory is bottleneck
//   GLOBAL_LOOP  : all cores → [33%,  75%]  balanced moderate profile
// ---------------------------------------------------------------------------
inline PWR_Status PWR_AppHintRegion(PWR_RegionHint hint) {
    using namespace pwr_internal;

    if (g_hw_min_khz == 0 || g_hw_max_khz == 0)
        return PWR_RET_FAILURE;  // PWR_Init() not called

    const int n = numCPUs();
    if (n <= 0) return PWR_RET_FAILURE;

    bool ok = true;

    switch (hint) {

    case PWR_REGION_DEFAULT:
        // Restore the full hardware frequency range so the governor regains
        // unrestricted control, and re-enable all C-states on every core.
        for (int i = 0; i < n; i++) {
            ok &= setCoreFreq(i, g_hw_min_khz, g_hw_max_khz);
            enableAllCStates(i);
        }
        break;

    case PWR_REGION_SERIAL:
        // Pin core 0 to the highest P-state for maximum single-thread speed.
        // Pin idle cores to the lowest P-state, then force the deepest C-state
        // (C6/CC6) by disabling shallower states (POLL, C1, C2).  Combined,
        // the P-state and C-state changes maximise power savings on every
        // non-active core while keeping core 0 at peak performance.
        ok &= setCoreFreq(0, g_hw_max_khz, g_hw_max_khz);
        for (int i = 1; i < n; i++) {
            ok &= setCoreFreq(i, g_hw_min_khz, g_hw_min_khz);
            enableAllCStates(i);   // reset first so previous overrides are cleared
            forceDeepCState(i);    // then disable shallow states to steer to C6/CC6
        }
        break;

    case PWR_REGION_PARALLEL:
    case PWR_REGION_COMPUTE:
        // All cores at high frequency to sustain parallel / compute throughput.
        // COMPUTE is treated identically since both are CPU-frequency limited.
        for (int i = 0; i < n; i++)
            ok &= setCoreFreq(i, freqAtPct(75), g_hw_max_khz);
        break;

    case PWR_REGION_COMMUNICATE:
        // Dedicate one fast core to network handling; park the rest.
        ok &= setCoreFreq(0, freqAtPct(67), g_hw_max_khz);
        for (int i = 1; i < n; i++)
            ok &= setCoreFreq(i, g_hw_min_khz, freqAtPct(17));
        break;

    case PWR_REGION_IO:
        // CPU is stalled on storage I/O. Cap the maximum frequency to save
        // energy; min is left unchanged (0 = skip) to avoid OS stalls.
        for (int i = 0; i < n; i++)
            ok &= setCoreFreq(i, /*min=*/0, /*max=*/freqAtPct(67));
        break;

    case PWR_REGION_MEM_BOUND:
        // Memory bandwidth is the bottleneck. Reduce CPU frequency to save
        // energy with minimal runtime penalty.
        for (int i = 0; i < n; i++)
            ok &= setCoreFreq(i, g_hw_min_khz, freqAtPct(33));
        break;

    case PWR_REGION_GLOBAL_LOOP:
        // Outer loop spanning mixed sub-regions. Balanced moderate profile
        // avoids peak-power overhead and deep-sleep wake latency.
        for (int i = 0; i < n; i++)
            ok &= setCoreFreq(i, freqAtPct(33), freqAtPct(75));
        break;

    case PWR_REGION_INVALID:
    case PWR_REGION_NOT_SPECIFIED:
    default:
        return PWR_RET_FAILURE;
    }

    return ok ? PWR_RET_SUCCESS : PWR_RET_FAILURE;
}
