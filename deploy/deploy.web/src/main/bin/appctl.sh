#!/bin/bash

PROG_NAME=$0
ACTION=$1
TOMCAT_PORT="7001"
usage() {
    echo "Usage: $PROG_NAME {start|stop|restart}"
    exit 1;
}

if [ $# -lt 1 ]; then
    usage
fi

APP_HOME=$(cd $(dirname $0)/..; pwd)
source "$APP_HOME/bin/setenv.sh"

# check and clear $CATALINA_PID file.
# (copy from catalina.sh)
check_catalina_pid() {
  if [ ! -z "$CATALINA_PID" ]; then
    if [ -f "$CATALINA_PID" ]; then
      if [ -s "$CATALINA_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$CATALINA_PID" ]; then
          PID=`cat "$CATALINA_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "Tomcat appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$CATALINA_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$CATALINA_PID" ]; then
                cat /dev/null > "$CATALINA_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$CATALINA_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$CATALINA_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi
}

prepare_catalina_base() {
	rm -rf "$CATALINA_BASE" || exit
	mkdir -p "$CATALINA_BASE" "$CATALINA_BASE/"{deploy,temp,work} || exit
	cp -rf "$CATALINA_HOME/conf" "$CATALINA_BASE/" || exit
	sed -i -e "s/\"8080\"/\"$TOMCAT_PORT\"/g" $CATALINA_BASE/conf/server.xml
}

create_log_dir(){
    mkdir -p "$LOGS_BASE"
}

start() {
    create_log_dir
	check_catalina_pid
	prepare_catalina_base

    rm -rf "$CATALINA_BASE/deploy/${war_name}"
    ln -s "${APP_HOME}/${war_name}" "$CATALINA_BASE/deploy/${war_name}"

	# prepare_catalina_out
	if [ -e "$CATALINA_OUT" ]; then
        rm -f "$CATALINA_OUT.*"
		mv "$CATALINA_OUT" "$CATALINA_OUT.$(date '+%Y%m%d%H%M%S')" || exit
	fi
	touch "$CATALINA_OUT" || exit

	echo "start tomcat"

	"$CATALINA_HOME"/bin/catalina.sh "$JPDA_ARG" start >"$CATALINA_OUT" 2>&1
}

stop() {
	echo "stop nginx"
	"$NGINXCTL" stop
	echo "stop tomcat"
	"$CATALINA_HOME"/bin/catalina.sh stop -force
}

start_http() {
    exptime=0
    while true
    do
      ###CHECK_TOMCAT_STATUS
      ret=`egrep "(startup failed due to previous errors|Cannot start server)" $CATALINA_OUT`
      if [ ! -z "$ret" ]; then
          echo -e "\nTomcat startup failed."
          return
      fi
      ret=`fgrep "Server startup in" $CATALINA_OUT`
      if [ -z "$ret" ]; then
          sleep 1
          ((exptime++))
          if [ -t 1 ]; then
            echo -n -e "\r"
          else
            echo
          fi
          echo -n -e "Wait Tomcat Start: $exptime..."
      else
        echo
        "$APP_HOME/bin/preload.sh"
        if [ "$?" -eq 0 ]; then
          touch -m $STATUSROOT_HOME/status.taobao
          if [ ! -f $STATUSROOT_HOME/status.taobao ];then
            echo "can't create status file, please check directory and disk space, not online"
          else
            echo "app start success, created status file, online"
          fi
        else
          echo "app start failed, not online, please fix the problem then restart"
        fi
        update_conf "nginx" "$NGINX_HOME/conf"
		echo "start nginx"
        "$NGINXCTL" start
        return
      fi
    done
}

case "$ACTION" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start
        start_http
    ;;
    *)
        usage
    ;;
esac

