#!/bin/bash

current_path=`pwd`
base="$(cd "$(dirname "$0")" && pwd -P)"

if [ -f $base/env/find_java_home.sh ]; then
  . $base/env/find_java_home.sh
fi

if [ -z $JAVA ]; then
  echo java not find!
  exit 1
fi


AUTOCONFIG=$base/autoconfig/autoconfig
LIBS=$base/../lib
JAR=$LIBS/`ls $LIBS/ | grep tesla.manager.*.jar`

if [ -z $JAVA_HOME ]; then
  export JAVA_HOME=/opt/taobao/java
fi

echo start to exec: $AUTOCONFIG $RESOURCES -c "UTF-8" $@
$AUTOCONFIG $JAR -c "UTF-8" $@
