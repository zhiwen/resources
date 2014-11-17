#!/bin/bash

current_path=`pwd`
base="$(cd "$(dirname "$0")" && pwd -P)"
logback_file=$base/../conf/logback.xml
NID=`cat $base/../conf/nid`

if [ -z "$NID" ] ; then
  echo "no NID in $base/../conf/nid"
  exit 3
fi

## set java path
if [ -f $base/env/find_java_home.sh ]; then
  . $base/env/find_java_home.sh
fi

if [ -z $JAVA ]; then
  echo java not find!
  exit 1
fi


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

JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
TESLA_OPTS="-DappName=tesla-node -Dnid=$NID -Dlogback.configurationFile=$logback_file"

if [ -e $logback_file ]
then
  for i in $base/../lib/*;
  do CLASSPATH=$i:"$CLASSPATH";
  done

  echo LOG CONFIGURATION : $logback_file
  echo CLASSPATH :$CLASSPATH

  echo "cd to $base for workaround relative path for logback"
  cd $base

  $JAVA $JAVA_OPTS $JAVA_DEBUG_OPT $TESLA_OPTS -classpath .:$CLASSPATH com.taobao.tesla.node.NodeLauncher 1>>$base/nohup.out 2>&1 &

  echo "cd to current path"
  cd $current_path

  echo $! > $base/tesla.node.pid

else
  echo there is no logback file, please check!
fi
