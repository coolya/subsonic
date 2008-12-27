@echo off

REM  The directory where Subsonic will create files. Make sure it is writable.
set SUBSONIC_HOME=/var/subsonic

REM  The port on which Subsonic will listen for incoming HTTP traffic.
set SUBSONIC_PORT=8080

REM  The context path (i.e., the last part of the Subsonic URL).  Typically "/" or "/subsonic".
set SUBSONIC_CONTEXT_PATH=/

REM  The memory limit (max Java heap size) in megabytes.
set MAX_MEMORY=64

java -Xmx%MAX_MEMORY%m  -Dsubsonic.home=%SUBSONIC_HOME% -Dsubsonic.port=%SUBSONIC_PORT% -Dsubsonic.contextPath=%SUBSONIC_CONTEXT_PATH% -jar subsonic-booter-jar-with-dependencies.jar

