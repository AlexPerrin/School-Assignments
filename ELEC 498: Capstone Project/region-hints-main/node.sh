#!/bin/bash

#SBATCH --nodes=1
#SBATCH --ntasks=4 # --cpus-per-task=4
#SBATCH --time=0:60:00
#SBATCH --mem-per-cpu=4g
#SBATCH --res=grantr
#SBATCH --account=def-hpcg1971
#SBATCH --qos=privileged
#SBATCH --output=jobname-%j.out
#SBATCH --error=jobname-%j.out
sleep 3600
