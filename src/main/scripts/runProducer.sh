#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
# Set CLASSPATH for the JVM
CLASSPATH="$SCRIPT_PATH/../lib/*"
# Config is here
confDir="$SCRIPT_PATH/../config"

hibernate_log_config="-Dcom.mchange.v2.log.MLog=com.mchange.v2.log.FallbackMLog -Dcom.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL=WARNING"

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
 -cp "$CLASSPATH" \
  dk.statsbiblioteket.larm_doms_exporter.producer.ProducerApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/lde.infrastructure.properties \
 --behavioural_configfile=$confDir/lde.behaviour.properties