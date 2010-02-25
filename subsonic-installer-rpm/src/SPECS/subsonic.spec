Name:           subsonic
Version:        3.9
Release:        1%{?dist}
Summary:        A web-based music streamer, jukebox and Podcast receiver

Group:          Applications/Multimedia
License:        GPLv3
URL:            http://subsonic.org
Source0:
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:
Requires:

%description
Subsonic is a web-based music streamer, jukebox and Podcast receiver,
providing access to your music collection wherever you are. Use it
to share your music with friends, or to listen to your music while away
from home.

A Subsonic client for Android phones is also available.

Java 1.6 or higher is required to run Subsonic.

Subsonic can be found at http://subsonic.org

%prep
%setup -q

%build
%configure
#make %{?_smp_mflags}

%install
#rm -rf $RPM_BUILD_ROOT
#make install DESTDIR=$RPM_BUILD_ROOT

%clean
#rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%doc

%changelog
