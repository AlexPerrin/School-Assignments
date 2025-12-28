
--------------Overview--------------

/benchmarks/ for benchmarks
/general/ for utility files, general notes, etc
/jobs/ for batch file to run everything

--------------Setup--------------

Install:

- clang (C compiler which worked better for openmp, might not need)

- libomp-dev (openmp)

- linux-tools-* (cpupower, just run cpupower and itll probably say you need a specific version, install that)



If your using Intel do:

1) Disable intel pstate, acpi-cpufreq will activate by default

GRUB_CMDLINE_LINUX_DEFAULT="intel_pstate=disable"

2) Update grub 

sudo update-grub

3) Restart computer

If your not using Intel, it will likely work similarly but you may have to disable a different driver. 

