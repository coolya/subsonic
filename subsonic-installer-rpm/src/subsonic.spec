Name:           subsonic
Version:        @VERSION@
Release:        @BUILD_NUMBER@
Summary:        A web-based music streamer, jukebox and Podcast receiver

Group:          Applications/Multimedia
License:        GPLv3
URL:            http://subsonic.org

%description
Subsonic is a web-based music streamer, jukebox and Podcast receiver,
providing access to your music collection wherever you are. Use it
to share your music with friends, or to listen to your music while away
from home.

A Subsonic client for Android phones is also available.

Java 1.6 or higher is required to run Subsonic.

Subsonic can be found at http://subsonic.org

%files
%defattr(644,root,root,755)
/usr/share/subsonic/subsonic-booter-jar-with-dependencies.jar
/usr/share/subsonic/subsonic.war
%attr(755,root,root) /usr/share/subsonic/subsonic.sh
%attr(755,root,root) /etc/init.d/subsonic
%config(noreplace) /etc/sysconfig/subsonic

%pre
# Stop Subsonic service.
if [ -e /etc/init.d/subsonic ]; then
  service subsonic stop
fi

exit 0

%post
ln -sf /usr/share/subsonic/subsonic.sh /usr/bin/subsonic

# Create transcoder symlinks.
mkdir -p /var/subsonic/transcode
chmod oug+rwx /var/subsonic

[ ! -e /var/subsonic/transcode/lame ]   && ln -sf /usr/bin/lame   /var/subsonic/transcode/
[ ! -e /var/subsonic/transcode/ffmpeg ] && ln -sf /usr/bin/ffmpeg /var/subsonic/transcode/
[ ! -e /var/subsonic/transcode/flac ]   && ln -sf /usr/bin/flac   /var/subsonic/transcode/
[ ! -e /var/subsonic/transcode/faad ]   && ln -sf /usr/bin/faad   /var/subsonic/transcode/
[ ! -e /var/subsonic/transcode/oggdec ] && ln -sf /usr/bin/oggdec /var/subsonic/transcode/
[ ! -e /var/subsonic/transcode/oggenc ] && ln -sf /usr/bin/oggenc /var/subsonic/transcode/

# Clear jetty cache.
rm -rf /var/subsonic/jetty

# For SELinux: Set security context
chcon -t java_exec_t /etc/init.d/subsonic 2>/dev/null

# Configure and start Subsonic service.
chkconfig --add subsonic
service subsonic start

exit 0

%preun
# Only do it if uninstalling, not upgrading.
if [ $1 = 0 ] ; then

  # Stop the service.
  [ -e /etc/init.d/subsonic ] && service subsonic stop

  # Remove symlink.
  rm -f /usr/bin/subsonic

  # Remove startup scripts.
  chkconfig --del subsonic

fi

exit 0

