#!/bin/bash

current_path=`pwd`

base="$(cd "$(dirname "$0")" && pwd -P)"

JAVA_HOME="`echo $JAVA_HOME`"

if [ -z $JAVA_HOME ]; then
  echo java not find!
  exit 1
fi

JAVA="$JAVA_HOME/bin/java"

case "$#"
  in
  0 )
  ;;
  1 )
  ;;
  2 )
  var1=$1
  var2=$2
  if [ "$1" = "debug" ]; then
    DEBUG_PORT=$2
    DEBUG_SUSPEND="n"
    JAVA_DEBUG_OPT="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=$DEBUG_SUSPEND"
  fi;;
* )
  echo "THE PARAMETERS MUST BE TWO OR LESS.PLEASE CHECK AGAIN."
  exit 1
  ;;
esac

JAVA_OPTS="-server -Xms2048m -Xmx3072m -Xmn1024m -XX:SurvivorRatio=2 -XX:PermSize=96m -XX:MaxPermSize=256m -Xss256k -XX:-UseAdaptiveSizePolicy -XX:MaxTenuringThreshold=15 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dnetworkaddress.cache.ttl=15 -Dfile.encoding=UTF-8 -DappName=spider"

CLASSPATH="$base/../config/:$CLASSPATH";

for i in $base/../lib/*;
do
  CLASSPATH=$i:"$CLASSPATH";
done

echo CLASSPATH :$CLASSPATH

cd $base

$JAVA $JAVA_OPTS $JAVA_DEBUG_OPT -classpath .:$CLASSPATH com.resource.spider.TestMain 1>>$base/nohup.out 2>&1 &

echo "cd to current path"
cd $current_path

echo $! > $base/spider.pid

