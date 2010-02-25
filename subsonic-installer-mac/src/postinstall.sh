#!/bin/bash

SUBSONIC_HOME="/Library/Application Support/Subsonic"

mkdir -p "$SUBSONIC_HOME/transcode"
chmod oug+rwx "$SUBSONIC_HOME"
chown root:admin "$SUBSONIC_HOME"
rm -rf "$SUBSONIC_HOME/jetty"

# Create symlinks to macports transcoders.
[ ! -e "$SUBSONIC_HOME/transcode/lame" ] && ln -sf /opt/local/bin/lame "$SUBSONIC_HOME/transcode/"
[ ! -e "$SUBSONIC_HOME/transcode/ffmpeg" ] && ln -sf /opt/local/bin/ffmpeg "$SUBSONIC_HOME/transcode/"
