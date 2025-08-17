#!/bin/bash

# Use Java 8 for CORBA support
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Start FileShareServer instance with the postgres JDBC driver. and connected to the ORB service.
java -cp ".:postgresql-42.7.6.jar" FileShareServer -ORBInitialPort 1050