#!/bin/bash

# Use Java 8 for CORBA support
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Start FileShareServer instance connected to the ORB service.
java FileShareClient -ORBInitialPort 1050