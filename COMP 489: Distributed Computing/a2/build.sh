#!/bin/bash

# Use Java 8 for CORBA support
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Compile IDL, generates FileShare, Stub, Helper, Holder, Operations, POA in current directory.
idlj -fall FileShare.idl

# Compile Java with PostgreSQL driver
javac -cp ".:postgresql-42.7.6.jar" FileShareServer.java FileShareClient.java