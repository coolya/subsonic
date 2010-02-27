Name:           subsonic
Version:        @VERSION@
Release:        1%{?dist}
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
