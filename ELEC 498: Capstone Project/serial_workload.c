/*
 * serial_workload.c
 * Synthetic serial-bound compute workload.
 *
 * Performs a long chain of dependent Fused Multiply-Add (FMA) operations
 * where every iteration reads and overwrites the same accumulator register:
 *
 *     acc = acc * mul + add       (repeated ITERATIONS times)
 *
 * The read-after-write (RAW) hazard on `acc` means each iteration cannot
 * begin until the previous FMA retires.  On Intel Broadwell / Skylake the
 * VFMADD132SD instruction has a 4-cycle latency, so throughput is bounded
 * to one operation per 4 cycles — regardless of how many execution units or
 * cores are available.  This is the defining characteristic of a serial
 * bottleneck: adding more hardware resources cannot improve runtime.
 *
 * The hot loop is written in x86-64 inline assembly so the dependency chain
 * is preserved exactly regardless of compiler optimisation level.  A pure C
 * loop over a non-volatile variable would allow the compiler to break the
 * chain via renaming or unrolling.
 *
 * Compile:
 *   gcc -O2 -mfma -mavx2 -o serial_workload serial_workload.c
 *
 * Typical runtime on Intel Xeon E5-2650 v4 @ 2.7 GHz:
 *   ~3 s  (≈ 2e9 iterations × 4-cycle latency / 2.7e9 Hz)
 */

#include <stdio.h>
#include <time.h>
#include <stdint.h>

#define ITERATIONS 2000000000L   /* 2 billion dependent FMA operations */

/*
 * serial_kernel - execute a RAW-dependent FMA chain in assembly.
 *
 * Arguments (System V AMD64 ABI):
 *   n    (rdi)  - iteration count
 *   acc  (xmm0) - initial accumulator value; returned in xmm0
 *   mul  (xmm1) - multiplier  (loaded into xmm1 before the loop)
 *   add  (xmm2) - addend      (loaded into xmm2 before the loop)
 *
 * Loop body (AT&T syntax):
 *   vfmadd132sd %%xmm1, %%xmm2, %[acc]
 *     => acc = acc * xmm1 + xmm2
 *     => acc = acc * mul  + add
 *
 * Because %[acc] is both an input and an output of every instruction,
 * the processor cannot start iteration i+1 until iteration i writes its
 * result — serialising the entire loop to FMA latency.
 */
static double serial_kernel(long n, double acc, double mul, double add)
{
    __asm__ volatile (
        "vmovsd  %[mul], %%xmm1             \n\t"   /* mul  -> xmm1 (loop-invariant) */
        "vmovsd  %[add], %%xmm2             \n\t"   /* add  -> xmm2 (loop-invariant) */
        "1:                                 \n\t"
        "vfmadd132sd %%xmm1, %%xmm2, %[acc]\n\t"   /* acc = acc * mul + add          */
        "dec     %[n]                       \n\t"
        "jnz     1b                         \n\t"
        : [acc] "+x" (acc),                         /* read-write XMM accumulator     */
          [n]   "+r" (n)                            /* read-write loop counter        */
        : [mul] "m"  (mul),                         /* memory operand — no reg needed */
          [add] "m"  (add)
        : "xmm1", "xmm2"                            /* clobbered by vmovsd above      */
    );
    return acc;
}

int main(void)
{
    struct timespec t0, t1;

    /* Values chosen so the result stays finite over 2 billion iterations. */
    double acc = 1.0000001;
    double mul = 1.0000001;
    double add = 0.0000001;

    clock_gettime(CLOCK_MONOTONIC, &t0);
    acc = serial_kernel(ITERATIONS, acc, mul, add);
    clock_gettime(CLOCK_MONOTONIC, &t1);

    double elapsed = (double)(t1.tv_sec  - t0.tv_sec)
                   + (double)(t1.tv_nsec - t0.tv_nsec) * 1e-9;

    /* Print result to prevent the compiler treating the call as dead code. */
    printf("serial:  %.3f s   result = %.6e\n", elapsed, acc);
    return 0;
}
