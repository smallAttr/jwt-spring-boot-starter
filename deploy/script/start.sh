#!/bin/bash

if [ 'prod' = "$SPRING_PROFILES_ACTIVE" ]
then
    java -Dfile.encoding=UTF8 -Xms1024m -Xmx1024m -XX:NewRatio=4 -XX:SurvivorRatio=4 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -Xss2048k -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:ConcGCThreads=4 -XX:ParallelGCThreads=4 -XX:+CMSScavengeBeforeRemark -XX:PretenureSizeThreshold=64m -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=50 -XX:CMSMaxAbortablePrecleanTime=6000 -XX:+CMSParallelRemarkEnabled -XX:+ParallelRefProcEnabled  -server -jar /apps/jwt-spring-boot-starter-0.0.1-SNAPSHOT.jar > /dev/null 2>&1
else
    java -Dfile.encoding=UTF8 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:ConcGCThreads=4 -XX:ParallelGCThreads=4 -XX:+CMSScavengeBeforeRemark -XX:PretenureSizeThreshold=64m -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=50 -XX:CMSMaxAbortablePrecleanTime=6000 -XX:+CMSParallelRemarkEnabled -jar /apps/jwt-spring-boot-starter-0.0.1-SNAPSHOT.jar > /dev/null 2>&1
fi
