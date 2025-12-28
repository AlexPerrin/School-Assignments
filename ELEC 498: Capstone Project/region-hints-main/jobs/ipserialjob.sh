#!/bin/bash


#Determine max, min, and inital freqs
maxfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
minfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
#initalfreqfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed"
max=$(cat "$maxfile")
min=$(cat "$minfile")
#initialfreq=$(cat "$initalfreqfile")

#Compile benchmark
gcc -o ../benchmarks/benchmarktest.o -fopenmp ../benchmarks/benchmarktest.c -D serial=1 -lcurl

#Hint behaviour
#Increase core 0 minimum
echo 2500000 | tee /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
#Decrease the remaining cores maxes 
for i in /sys/devices/system/cpu/cpu*/cpufreq
do 
	echo 1300000 | tee $i/scaling_max_freq
done

core="/sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq"

#Set core 0 to a high value
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq

cat /sys/class/powercap/intel-rapl:0/energy_uj > startjoules
taskset -c 0 ../benchmarks/benchmarktest.o
cat /sys/class/powercap/intel-rapl:0/energy_uj > stopjoules

#Write to log
echo -n , serial >> ../log.csv
for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq
do 
	echo -n , $(cat $i) >> ../log.csv
done
echo , $(cat startjoules), $(cat stopjoules) >> ../log.csv

#Reset inital freqs
for i in /sys/devices/system/cpu/cpu*/cpufreq
do 
	echo $max | tee $i/scaling_max_freq
	echo $min | tee $i/scaling_min_freq
done



