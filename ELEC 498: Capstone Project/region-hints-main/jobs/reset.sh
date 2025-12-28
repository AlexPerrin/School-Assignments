#!/bin/bash


#Determine max, min, and inital freqs
maxfile="/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
minfile="/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
#initalfreqfile="/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed"
max=$(cat "$maxfile")
min=$(cat "$minfile")
#initialfreq=$(cat "$initalfreqfile")

#Reset inital freqs
for i in /sys/devices/system/cpu/cpu*/cpufreq
do 
	echo $max | tee $i/scaling_max_freq
	echo $min | tee $i/scaling_min_freq
done



