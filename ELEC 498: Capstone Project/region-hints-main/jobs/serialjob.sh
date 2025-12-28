#!/bin/bash
export OMP_NUM_THREADS=8
#Set governor
date +%s%N
sudo cpupower frequency-set --governor performance
date +%s%N
#Determine max, min, and inital freqs
maxfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
minfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
initalfreqfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed"
max=$(cat "$maxfile")
min=$(cat "$minfile")
initialfreq=$(cat "$initalfreqfile")

#Compile benchmark
gcc -o ../benchmarks/benchmarktest.o -fopenmp ../benchmarks/benchmarktest.c -D serial=1

#Hint behaviour
date +%s%N
sudo cpupower --cpu all frequency-set --freq $min
date +%s%N
sudo cpupower --cpu 0 frequency-set --freq 2000000
date +%s%N


#sudo cpupower --cpu 2,3,4,5,6,7 idle-set -d 0

#sudo turbostat --Summary --quiet --show Pkg_J -interval 10 --Joules --out pwrdata
sudo cat /sys/class/powercap/intel-rapl:0/energy_uj > startjoules
taskset -c 0 ../benchmarks/benchmarktest.o
sudo cat /sys/class/powercap/intel-rapl:0/energy_uj > stopjoules

echo -n , serial >> ../log.csv

for i in /sys/devices/system/cpu/cpu*/cpufreq/cpuinfo_cur_freq
do 
	echo -n , $(sudo cat $i) >> ../log.csv
done

echo , $(cat startjoules), $(cat stopjoules) >> ../log.csv


#Reset inital freq
sudo cpupower --cpu all frequency-set --freq $initialfreq

#Reset governor
sudo cpupower frequency-set --governor ondemand


