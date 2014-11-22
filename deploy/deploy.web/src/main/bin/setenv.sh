# app
APP_NAME=/Users/zhiwenmizw/work/resources/deploy/deploy.web/target/deploy.web-1.0-SNAPSHOT
STATUSROOT_HOME="${APP_HOME}/target/${APP_NAME}.war"

# os env
export LANG=zh_CN.GB18030
export NLS_LANG=AMERICAN_AMERICA.ZHS16GBK
#export LD_LIBRARY_PATH=/opt/taobao/oracle/lib:/opt/taobao/lib:$LD_LIBRARY_PATH
export JAVA_HOME=${JAVA_HOME}
ulimit -c unlimited

# nginx
NGINX_HOME=${NGINX_HOME}
NGINXCTL=$NGINX_HOME/bin/nginxctl

# jpda options
JPDA_ENABLE=1
export JPDA_ADDRESS=8000
export JPDA_SUSPEND=n

export LOGS_BASE=$APP_HOME/logs
export CATALINA_HOME=/opt/tomcat7
export CATALINA_BASE=$APP_HOME/.default
export CATALINA_LOGS=$APP_HOME/logs/catalina
export CATALINA_OUT=$APP_HOME/logs/tomcat_stdout.log
export CATALINA_PID=$APP_HOME/logs/catalina.pid

# tomcat jvm options
CATALINA_OPTS="-server"
CATALINA_OPTS="${CATALINA_OPTS} -Xms1536m -Xmx1536m"
CATALINA_OPTS="${CATALINA_OPTS} -XX:PermSize=256m -XX:MaxPermSize=256m"
CATALINA_OPTS="${CATALINA_OPTS} -Xmn768m"
CATALINA_OPTS="${CATALINA_OPTS} -XX:SurvivorRatio=10"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+DisableExplicitGC"
#CATALINA_OPTS="${CATALINA_OPTS} -verbose:gc -Xloggc:/home/admin/logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseCompressedOops"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+AggressiveOpts"
CATALINA_OPTS="${CATALINA_OPTS} -Djava.awt.headless=true"
CATALINA_OPTS="${CATALINA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
CATALINA_OPTS="${CATALINA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"

# project.name for hsf
CATALINA_OPTS="$CATALINA_OPTS -Dproject.name=$APP_NAME"
export CATALINA_OPTS
