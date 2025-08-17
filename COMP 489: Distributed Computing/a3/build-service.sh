#!/bin/bash

# Simple build script for JAX-WS FileShare Web Service
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH

# Create WEB-INF structure in root
mkdir -p WEB-INF/classes WEB-INF/lib

# Compile service in root directory
javac -cp "postgresql-42.7.6.jar" FileShareService.java

# Copy files to WEB-INF structure
cp FileShareService.class WEB-INF/classes/
cp postgresql-42.7.6.jar WEB-INF/lib/
cp web.xml WEB-INF/
cp sun-jaxws.xml WEB-INF/

# Create WAR
jar cf FileShareService.war WEB-INF/

# Deploy the WAR
cp FileShareService.war wildfly-10.1.0.Final/standalone/deployments/