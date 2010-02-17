#!/bin/bash

mkdir -p  "/Library/Application Support/Subsonic"
chmod oug+rwx "/Library/Application Support/Subsonic"
chown root:admin "/Library/Application Support/Subsonic"
rm -rf "/Library/Application Support/Subsonic/jetty"
