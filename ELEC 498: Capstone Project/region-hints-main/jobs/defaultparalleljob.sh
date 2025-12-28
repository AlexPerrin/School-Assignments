#!/bin/bash

#Set governor
sudo cpupower frequency-set --governor powersave

#Determine max, min, and inital freqs
maxfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
minfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
initalfreqfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed"
max=$(cat "$maxfile")
min=$(cat "$minfile")
initialfreq=$(cat "$initalfreqfile")

export OMP_NUM_THREADS=8
#export OMP_DYNAMIC=FALSE

#Run benchmark
clang -o ../benchmarks/benchmarktest.o -pedantic -fopenmp=libomp ../benchmarks/benchmarktest.c -D par=1

sudo cat /sys/class/powercap/intel-rapl:0/energy_uj > startjoules
../benchmarks/benchmarktest.o
sudo cat /sys/class/powercap/intel-rapl:0/energy_uj > stopjoules

echo -n , parallel >> ../log.csv

for i in /sys/devices/system/cpu/cpu*/cpufreq/cpuinfo_cur_freq
do 
	echo -n , $(sudo cat $i) >> ../log.csv
done

echo , $(cat startjoules), $(cat stopjoules) >> ../log.csv

#Reset governor
sudo cpupower frequency-set --governor ondemand
