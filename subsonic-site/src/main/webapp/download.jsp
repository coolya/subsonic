<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%!
    String current = "download";
    String stable = "4.0.1";
    String beta = null; // Set to null if no beta is available.
%>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Download Subsonic</h1>

            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="featuretable bottomspace">
                <tr class="table-heading">
                    <th></th>
                    <th class="featurename">Latest stable release &ndash; Subsonic <%=stable%></th>
                    <th><a href="changelog.jsp#<%=stable%>">What's new?</a></th>
                    <th><a href="http://www.activeobjects.no/subsonic/forum/viewtopic.php?t=3284">Discuss</a></th>
                </tr>
                <tr class="table-altrow">
                    <td><img src="inc/img/download-windows.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Windows installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>-setup.exe"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#windows"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td><img src="inc/img/download-mac.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Mac OS X 10.5+ installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>.pkg"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#mac"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td><img src="inc/img/download-ubuntu.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Debian/Ubuntu installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>.deb"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#debian"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td><img src="inc/img/download-fedora.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Red Hat/Fedora installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>.rpm"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#rpm"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td></td>
                    <td class="featurename" style="padding-left:0">Stand-alone version (all platforms)</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>-standalone.tar.gz"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#standalone"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td></td>
                    <td class="featurename" style="padding-left:0"> WAR version (all platforms)</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=stable%>-war.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#war"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
            </table>

            <% if (beta != null) { %>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="featuretable bottomspace">
                <tr class="table-heading">
                    <th></th>
                    <th class="featurename">Latest beta release &ndash; Subsonic <%=beta%></th>
                    <th><a href="changelog.jsp#<%=beta%>">What's new?</a></th>
                    <th><a href="http://www.activeobjects.no/subsonic/forum/viewtopic.php?t=3246">Discuss</a></th>
                </tr>
                <tr class="table-altrow">
                    <td><img src="inc/img/download-windows.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Windows installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>-setup.exe"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#windows"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td><img src="inc/img/download-mac.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Mac OS X 10.5+ installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>.pkg"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#mac"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td><img src="inc/img/download-ubuntu.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Debian/Ubuntu installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>.deb"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#debian"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td><img src="inc/img/download-fedora.png" alt="" height="16" width="16"/></td>
                    <td class="featurename" style="padding-left:0">Red Hat/Fedora installer</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>.rpm"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#rpm"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td></td>
                    <td class="featurename" style="padding-left:0">Stand-alone version (all platforms)</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>-standalone.tar.gz"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#standalone"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td></td>
                    <td class="featurename" style="padding-left:0"> WAR version (all platforms)</td>
                    <td style="white-space:nowrap;"><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-<%=beta%>-war.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/>&nbsp;Download</a></td>
                    <td style="white-space:nowrap;"><a href="installation.jsp#war"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
            </table>
            <% } %>

            <p class="margin10-t">
                <a href="http://www.gnu.org/copyleft/gpl.html"><img class="img-left" alt="GPL" src="inc/img/gpl.png"/></a>
                Subsonic is open-source software licensed under the <a href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a>.
            </p>
        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
            <div class="sidebox">
                <h2>Archive</h2>
                <p>
                    Older versions, as well as source code, can be downloaded from
                    <a href="http://sourceforge.net/projects/subsonic/">SourceForge</a>.
                </p>
            </div>

            <%@ include file="merchandise.jsp" %>

        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
