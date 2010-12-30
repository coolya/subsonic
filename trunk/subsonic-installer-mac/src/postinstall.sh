#!/bin/bash

SUBSONIC_HOME="/Library/Application Support/Subsonic"

chmod oug+rwx "$SUBSONIC_HOME"
chown root:admin "$SUBSONIC_HOME"
rm -rf "$SUBSONIC_HOME/jetty"

echo Subsonic installation done
