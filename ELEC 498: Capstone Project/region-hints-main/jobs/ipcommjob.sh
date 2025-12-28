#!/bin/bash


#Determine max, min, and inital freqs
maxfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
minfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
#initalfreqfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed"
max=$(cat "$maxfile")
min=$(cat "$minfile")
#initialfreq=$(cat "$initalfreqfile")

#Compile benchmark
gcc -o ../benchmarks/benchmarktest.o ../benchmarks/benchmarktest.c -D comm=1 -lcurl

#Hint behaviour
echo 2400000 | tee /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
#Set all cores max to a low value
for i in /sys/devices/system/cpu/cpu*/cpufreq
	do 
		echo 1300000 | tee $i/scaling_max_freq
done

echo 2900000 | tee /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq

cat /sys/class/powercap/intel-rapl:0/energy_uj > startjoules
taskset -c 0 ../benchmarks/benchmarktest.o
cat /sys/class/powercap/intel-rapl:0/energy_uj > stopjoules

#Write to log
echo -n , ipcomm >> ../log.csv
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



