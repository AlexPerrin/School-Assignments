#!/bin/bash

# Simple build script for JAX-WS FileShare Client
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Create directory for stubs
mkdir -p client-stubs

# Generate stubs
wsimport -keep -d client-stubs http://localhost:8080/FileShareService/FileShareService?wsdl

# Compile client with generated stubs in classpath
javac -cp client-stubs FileShareClient.java

# Create executable JAR with all necessary classes
jar cfe FileShareClient.jar FileShareClient \
    FileShareClient*.class \
    -C client-stubs .