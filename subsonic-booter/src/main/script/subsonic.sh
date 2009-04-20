#!/bin/sh

###################################################################################
# Shell script for starting Subsonic.  See http://subsonic.sourceforge.net.
#
# Author: Sindre Mehus
###################################################################################

SUBSONIC_HOME=/var/subsonic
SUBSONIC_HOST=0.0.0.0
SUBSONIC_PORT=8080
SUBSONIC_CONTEXT_PATH=/
SUBSONIC_MAX_MEMORY=64
SUBSONIC_PIDFILE=

usage() {
    echo "Usage: subsonic.sh [options]"
    echo "  --help               This small usage guide."
    echo "  --home=DIR           The directory where Subsonic will create files."
    echo "                       Make sure it is writable. Default: /var/subsonic"
    echo "  --host=HOST          The host name or IP address on which to bind Subsonic."
    echo "                       Only relevant if you have multiple network interfaces and want"
    echo "                       to make Subsonic available on only one of them. The default value"
    echo "                       will bind Subsonic to all available network interfaces. Default: 0.0.0.0"
    echo "  --port=PORT          The port on which Subsonic will listen for"
    echo "                       incoming HTTP traffic. Default: 8080"
    echo "  --context-path=PATH  The context path, i.e., the last part of the Subsonic"
    echo "                       URL. Typically '/' or '/subsonic'. Default '/'"
    echo "  --max-memory=MB      The memory limit (max Java heap size) in megabytes."
    echo "                       Default: 64"
    echo "  --pidfile=PIDFILE    Write PID to this file. Default not created."
    exit 1
}

# Parse arguments.
while [ $# -ge 1 ]; do
    case $1 in
        --help)
            usage
            ;;
        --home=?*)
            SUBSONIC_HOME=${1#--home=}
            ;;
        --host=?*)
            SUBSONIC_HOST=${1#--host=}
            ;;
        --port=?*)
            SUBSONIC_PORT=${1#--port=}
            ;;
        --context-path=?*)
            SUBSONIC_CONTEXT_PATH=${1#--context-path=}
            ;;
        --max-memory=?*)
            SUBSONIC_MAX_MEMORY=${1#--max-memory=}
            ;;
        --pidfile=?*)
            SUBSONIC_PIDFILE=${1#--pidfile=}
            ;;
        *)
            usage
            ;;
    esac
    shift
done

# Use JAVA_HOME if set, otherwise assume java is in the path.
JAVA=java
if [ -e "${JAVA_HOME}" ]
    then
    JAVA=${JAVA_HOME}/bin/java
fi

# Create Subsonic home directory.
mkdir -p ${SUBSONIC_HOME}
LOG=${SUBSONIC_HOME}/subsonic_sh.log
rm -f ${LOG}

cd `dirname $0`

${JAVA} -Xmx${SUBSONIC_MAX_MEMORY}m -Dsubsonic.home=${SUBSONIC_HOME} -Dsubsonic.host=${SUBSONIC_HOST} -Dsubsonic.port=${SUBSONIC_PORT} \
-Dsubsonic.contextPath=${SUBSONIC_CONTEXT_PATH} -jar subsonic-booter-jar-with-dependencies.jar > ${LOG} 2>&1 &

# Write pid to pidfile if it is defined.
if [ $SUBSONIC_PIDFILE ]; then
    echo $! > ${SUBSONIC_PIDFILE}
fi

