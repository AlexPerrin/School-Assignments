# sysfs
```
$ ls /sys/devices/system/cpu/cpu0/cpufreq
affected_cpus     cpuinfo_min_freq            related_cpus                 scaling_cur_freq  scaling_governor  scaling_min_freq
cpuinfo_max_freq  cpuinfo_transition_latency  scaling_available_governors  scaling_driver    scaling_max_freq  scaling_setspeed
```
```
$ cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors
conservative ondemand userspace powersave performance schedutil
```
```
$ cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_driver
intel_cpufreq [intel_pstate]
```
```
$ cat /sys/devices/system/cpu/intel_pstate/status
passive [active]
```
```
get scaling_governor for all cpus
$ tail /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor

get scaling_setspeed for all cpus
$ tail /sys/devices/system/cpu/cpu*/cpufreq/scaling_setspeed

set scaling_governor to userspace for all cpus
# for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo "userspace" > $i; done
# for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo "schedutil" > $i; done

set scaling_setspeed to 800000 for all cpus
# for i in /sys/devices/system/cpu/cpu*/cpufreq/scaling_setspeed; do echo "800000" > $i; done

set scaling_setspeed per cpu
# echo "800000" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed

watch cpu frequencies
$ watch -n 0.5 cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_cur_freq
```

# https://wiki.archlinux.org/title/CPU_frequency_scaling#Scaling_drivers

### list of available kernal modules (for scaling driver)
`$ ls /usr/lib/modules/$(uname -r)/kernel/drivers/cpufreq/`

### current scaling driver
`$ cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_driver`

# Other Stuff
### https://wiki.archlinux.org/title/CPU_frequency_scaling#cpupower
### install cpupower
`$ sudo apt install linux-tools-common linux-tools-5.15.0-58-generic`
### using cpupower
`$ cpupower frequency-info`

# https://vstinner.github.io/intel-cpus.html
`$ cat /proc/cpuinfo`

`$ lscpu -a -e`

# Changing lernal parameters
## https://wiki.archlinux.org/title/kernel_parameters
### Get kernal parameters
`$ cat /proc/cmdline`
### change defualt paramters in grub cfg
### can also just set parameters at boot in the grub boot screen
`$ vim /etc/default/grub`
```
GRUB_CMDLINE_LINUX_DEFAULT="intel_pstate=per_cpu_perf_limits"
```
`# grub-mkconfig -o /boot/grub/grub.cfg`