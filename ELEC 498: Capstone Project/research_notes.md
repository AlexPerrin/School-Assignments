#### https://www.kernel.org/doc/html/v4.14/admin-guide/pm/working-state.html

#### https://docs.kernel.org/admin-guide/pm/cpufreq.html

#### https://www.kernel.org/doc/html/v5.0/admin-guide/pm/intel_pstate.html

#

## https://www.kernel.org/doc/Documentation/cpu-freq/

###  https://www.kernel.org/doc/html/latest/admin-guide/pm/cpufreq.html#policy-interface-in-sysfs

That directory contains a policyX subdirectory (where X represents an integer number) for every policy object maintained by the CPUFreq core. Each policyX directory is pointed to by cpufreq symbolic links under 
/sys/devices/system/cpu/cpuY/ (where Y represents an integer that may be different from the one represented by X) for all of the CPUs associated with (or belonging to) the given policy. The policyX directories in 
/sys/devices/system/cpu/cpufreq each contain policy-specific attributes (files) to control CPUFreq behavior for the corresponding policy objects (that is, for all of the CPUs associated with them).

##  https://www.kernel.org/doc/Documentation/cpu-freq/intel-pstate.txt

The Intel P-State driver falls under the latter category, which implements the
setpolicy() callback. This driver decides what P-State to use based on the
requested policy from the cpufreq core. If the processor is capable of
selecting its next P-State internally, then the driver will offload this
responsibility to the processor (aka HWP: Hardware P-States). If not, the
driver implements algorithms to select the next P-State.

For contemporary Intel processors, the frequency is controlled by the
processor itself and the P-State exposed to software is related to
performance levels.  The idea that frequency can be set to a single
frequency is fictional for Intel Core processors. Even if the scaling
driver selects a single P-State, the actual frequency the processor
will run at is selected by the processor itself.

There is one more sysfs attribute in /sys/devices/system/cpu/intel_pstate/
that can be used for controlling the operation mode of the driver:

      status: Three settings are possible:
      "off"     - The driver is not in use at this time.
      "active"  - The driver works as a P-state governor (default).
      "passive" - The driver works as a regular cpufreq one and collaborates
                  with the generic cpufreq governors (it sets P-states as
                  requested by those governors).
      The current setting is returned by reads from this attribute.  Writing one
      of the above strings to it changes the operation mode as indicated by that
      string, if possible.  If HW-managed P-states (HWP) are enabled, it is not
      possible to change the driver's operation mode and attempts to write to
      this attribute will fail.

The kernel command line option "intel_pstate=per_cpu_perf_limits" forces
the intel_pstate driver to use per-CPU performance limits.  When it is set,
the sysfs control interface described above is subject to limitations.
The following controls can be used to set performance limits, as far as the
architecture of the processor permits:
	/sys/devices/system/cpu/cpu*/cpufreq/scaling_max_freq
	/sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq
	/sys/devices/system/cpu/cpu*/cpufreq/scaling_governor



### https://metebalci.com/blog/a-minimum-complete-tutorial-of-cpu-power-management-c-states-and-p-states
OS-controlled P-states
In this method, OS is aware of the P-states and a specific P-state is requested by the OS. This basically means selecting an operating frequency, whereas the voltage is automatically selected by the processor depending on the frequency and other factors. After a P-state is requested by writing it to a model specific register (meaning a 16-bit value is written to IA32_PERF_CTL), the voltage is transitioned to an automatically calculated value and the clock generator (PLL) locks to the frequency requested. All cores share the same P-state, so it is not possible to set this individually for a core. The current P-state / operating point can be read from another model specific register (IA32_PERF_STATUS).

The P-state transition is rapid, so many transitions per second can be performed. This is very different than C-state transitions which have higher latency and power costs.

### https://wiki.archlinux.org/title/CPU_frequency_scaling


