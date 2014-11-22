#!/bin/sh

#mvn clean install -Dmaven.test.skip=true


TOMCAT_HOME="/opt/tomcat7/"

rm -rf ${TOMCAT_HOME}/webapps/web ${TOMCAT_HOME}/webapps/web.war

ln -s /Users/zhiwenmizw/Public/dev/workcontent/resources/deploy/deploy.web/target/deploy.web-1.0-SNAPSHOT.war ${TOMCAT_HOME}/webapps/web.war

CATALINA_OPTS="-server -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8899"

sh ${TOMCAT_HOME}/bin/startup.sh
