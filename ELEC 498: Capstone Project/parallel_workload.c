/*
 * parallel_workload.c
 * Synthetic parallel-bound compute workload.
 *
 * Runs the structural opposite of serial_workload.c.  Instead of a single
 * accumulator forced through every iteration, each thread maintains four
 * completely independent FMA chains:
 *
 *     s0 = s0 * mul + add
 *     s1 = s1 * mul + add        (all four execute simultaneously)
 *     s2 = s2 * mul + add
 *     s3 = s3 * mul + add
 *
 * Two levels of parallelism are exposed:
 *
 *   1. SIMD (within a core): the four independent streams have no data
 *      dependencies between them, so the compiler packs all four into a
 *      single 256-bit AVX instruction (VFMADD132PD ymm) that computes all
 *      four doubles in one cycle — delivering the full 4× SIMD throughput
 *      that was impossible in the serial case.
 *
 *   2. Thread-level (across cores): OpenMP spawns one thread per logical
 *      CPU.  Every thread runs its own independent copy of the four-stream
 *      loop with a distinct seed, so there are zero shared data structures
 *      and zero synchronisation points inside the hot path.
 *
 * Adding more cores (or wider SIMD) directly and linearly improves runtime —
 * the opposite behaviour to the serial workload.
 *
 * Compile:
 *   gcc -O3 -fopenmp -mfma -mavx2 -o parallel_workload parallel_workload.c
 */

#include <stdio.h>
#include <time.h>
#include <omp.h>

#define ITERATIONS 500000000L   /* 500 million iterations per thread */

int main(void)
{
    struct timespec t0, t1;
    double result = 0.0;

    const double MUL = 1.0000001;
    const double ADD = 0.0000001;

    clock_gettime(CLOCK_MONOTONIC, &t0);

    /*
     * Each thread gets a unique base seed so the compiler cannot prove the
     * four streams are identical and collapse them into one.  The distinct
     * starting values are what allow the compiler to treat s0–s3 as four
     * separate dependency chains and emit a single packed VFMADD132PD
     * across all four rather than four serialised scalar VFMADD132SD.
     */
#pragma omp parallel reduction(+:result)
    {
        double base = (double)(omp_get_thread_num() + 1);

        double s0 = base;
        double s1 = base + 0.1;
        double s2 = base + 0.2;
        double s3 = base + 0.3;

        for (long i = 0; i < ITERATIONS; i++) {
            s0 = s0 * MUL + ADD;
            s1 = s1 * MUL + ADD;
            s2 = s2 * MUL + ADD;
            s3 = s3 * MUL + ADD;
        }

        result += s0 + s1 + s2 + s3;
    }

    clock_gettime(CLOCK_MONOTONIC, &t1);

    double elapsed = (double)(t1.tv_sec  - t0.tv_sec)
                   + (double)(t1.tv_nsec - t0.tv_nsec) * 1e-9;

    /* Print result to prevent dead-code elimination of the parallel region. */
    printf("parallel: %.3f s   result = %.6e   threads = %d\n",
           elapsed, result, omp_get_max_threads());
    return 0;
}
