#!/bin/sh

cd /Users/zhiwenmizw/work/resources

mvn clean install -Dmaven.test.skip eclipse:eclipse -U

export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"


#cd web 
mvn tomcat7:run
