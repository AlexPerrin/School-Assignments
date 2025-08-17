#!/bin/bash

# Use Java 8 for CORBA support
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Recreate the Database
sudo -u postgres psql -U postgres -c "DROP DATABASE IF EXISTS fileshare;"

# Setup database schema
sudo -u postgres psql -U postgres -f database-schema.sql

# Kill any existing orbd processes
pkill -f orbd

# Start CORBA naming service
orbd -ORBInitialPort 1050 &