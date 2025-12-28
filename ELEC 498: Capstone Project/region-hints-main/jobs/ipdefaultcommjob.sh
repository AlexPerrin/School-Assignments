#!/bin/bash

#Run benchmark
gcc -o ../benchmarks/benchmarktest.o -fopenmp ../benchmarks/benchmarktest.c -D comm=1 -lcurl

cat /sys/class/powercap/intel-rapl:0/energy_uj > startjoules
../benchmarks/benchmarktest.o
cat /sys/class/powercap/intel-rapl:0/energy_uj > stopjoules

echo -n , ipdefaultcomm >> ../log.csv

for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq
do 
	echo -n , $(cat $i) >> ../log.csv
done

echo , $(cat startjoules), $(cat stopjoules) >> ../log.csv

