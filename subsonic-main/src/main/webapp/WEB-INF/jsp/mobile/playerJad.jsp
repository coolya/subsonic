<%@ page language="java" contentType="text/vnd.sun.j2me.app-descriptor; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="../include.jsp" %>
MIDlet-1: Subsonic, /icons/subsonic.png, net.sourceforge.subsonic.jmeplayer.SubsonicMIDlet
MIDlet-Jar-Size: ${model.jarSize}
MIDlet-Jar-URL: playerJar.view
MIDlet-Name: Subsonic
MIDlet-Vendor: Sindre Mehus
MIDlet-Version: ${model.version}
MicroEdition-Configuration: CLDC-1.0
MicroEdition-Profile: MIDP-2.0
Subsonic-Base-URL: ${model.baseUrl}
