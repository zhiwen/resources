#!/bin/sh

mvn clean install -Dmaven.test.skip=true

ln -s /Users/zhiwenmizw/Public/dev/workcontent/resources/deploy/deploy.web/target/deploy.web-1.0-SNAPSHOT.war /opt/tomcat7/deploy/web.war
