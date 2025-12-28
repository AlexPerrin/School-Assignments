#!/bin/bash
echo $1
if [ "$1" == "s" ]; then
	echo serial
	for i in {1..10}
	do 
		./serialjob.sh
	done
fi
	
if [ "$1" == "p" ]; then
	echo parallel
	for i in {1..10}
	do 
		./paralleljob.sh
	done
fi


if [ "$1" == "dp" ]; then
	echo defaultparallel
	for i in {1..10}
	do 
		./defaultparalleljob.sh
	done
fi

if [ "$1" == "ds" ]; then
	echo defaultserial
	for i in {1..10}
	do 
		./defaultjob.sh
	done

fi

if [ "$1" == "ipds" ]; then
	echo ipdefaultjob
	for i in {1..10}
	do 
		./ipdefaultjob.sh
	done
fi

if [ "$1" == "ips" ]; then
	echo ipserial
	for i in {1..10}
	do
		./ipserialjob.sh
	done
fi

if [ "$1" == "ipdp" ]; then
	echo ipdefaultparalleljob
	for i in {1..10}
	do 
		./ipdefaultparalleljob.sh
	done
fi

if [ "$1" == "ipp" ]; then
	echo ipparallel
	for i in {1..10}
	do
		./ipparalleljob.sh
	done
fi

if [ "$1" == "ipdm" ]; then
	echo ipdefaultmem
	for i in {1..10}
	do 
		./ipdefaultmemjob.sh
	done
fi

if [ "$1" == "ipm" ]; then
	echo ipmem
	for i in {1..10}
	do
		./ipmemjob.sh
	done
fi

if [ "$1" == "ipdio" ]; then
	echo ipdefaultio
	for i in {1..10}
	do 
		./ipdefaultiojob.sh
	done
fi

if [ "$1" == "ipio" ]; then
	echo ipio
	for i in {1..10}
	do
		./ipiojob.sh
	done
fi

if [ "$1" == "ipdcomm" ]; then
	echo ipdefaultcomm
	for i in {1..10}
	do 
		./ipdefaultcommjob.sh
	done
fi

if [ "$1" == "ipcomm" ]; then
	echo ipcomm
	for i in {1..10}
	do
		./ipcommjob.sh
		./reset.sh
	done
fi
