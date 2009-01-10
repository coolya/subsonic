#!/bin/sh

# The directory where Subsonic will create files. Make sure it is writable.
SUBSONIC_HOME=/var/subsonic

# The port on which Subsonic will listen for incoming HTTP traffic.
SUBSONIC_PORT=8080

# The context path (i.e., the last part of the Subsonic URL).  Typically "/" or "/subsonic".
SUBSONIC_CONTEXT_PATH=/

# The memory limit (max Java heap size) in megabytes.
MAX_MEMORY=64


echo Starting Subsonic...
cd `dirname $0`

mkdir -p ${SUBSONIC_HOME}
LOG=${SUBSONIC_HOME}/subsonic_sh.log
rm -f ${LOG}

java -Xmx${MAX_MEMORY}m  -Dsubsonic.home=${SUBSONIC_HOME} -Dsubsonic.port=${SUBSONIC_PORT} \
-Dsubsonic.contextPath=${SUBSONIC_CONTEXT_PATH} -jar subsonic-booter-jar-with-dependencies.jar > ${LOG} 2>&1 &

echo Started Subsonic. See log file ${LOG}
