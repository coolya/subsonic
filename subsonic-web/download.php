<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'download';
    include("header.php");
?>

<body>

<a name="top"/>

<div id="container">
    <?php include("menu.php");?>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Download Subsonic</h1>

            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="featuretable bottomspace"
                   id="comparisontable">
                <tr class="table-heading">
                    <th class="featurename">Latest stable release &ndash; Subsonic 3.5</th>
                    <th>Download</th>
                    <th>Instructions</th>
                </tr>
                <tr class="table-altrow">
                    <td class="featurename">Windows installer</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.5-setup.exe"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#windows"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td class="featurename">Stand-alone version (all platforms)</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.5-standalone.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#standalone"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td class="featurename">WAR version (all platforms)</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.5-war.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#war"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
            </table>

            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="featuretable bottomspace"
                   id="comparisontable2">
                <tr class="table-heading">
                    <th class="featurename">Latest beta release &ndash; Subsonic 3.6.beta1</th>
                    <th>Download</th>
                    <th>Instructions</th>
                </tr>
                <tr class="table-altrow">
                    <td class="featurename">Windows installer</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.6.beta1-setup.exe"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#windows"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr>
                    <td class="featurename">Stand-alone version (all platforms)</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.6.beta1-standalone.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#standalone"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
                <tr class="table-altrow">
                    <td class="featurename">WAR version (all platforms)</td>
                    <td><a href="http://prdownloads.sourceforge.net/subsonic/subsonic-3.6.beta1-war.zip"><img
                            src="inc/img/download_small.gif" alt="Download" height="11" width="11"/> Download</a></td>
                    <td><a href="installation.php#war"><img src="inc/img/star.gif" alt="Instructions" height="14" width="14"/> Instructions</a></td>
                </tr>
            </table>

            <p class="margin10-t">
                <a href="http://www.gnu.org/copyleft/gpl.html"><img class="img-left" alt="GPL" src="inc/img/gpl.png"/></a>
                Subsonic is open-source software licensed under the <a href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a>.
            </p>
        </div>

        <div id="side-col">
            <div class="sidebox">
                <h2>Archive</h2>
                <p>
                    Older versions, as well as source code, can be downloaded from
                    <a href="http://sourceforge.net/projects/subsonic/">SourceForge</a>.
                </p>
            </div>

            <?php include("merchandise.php"); ?>

        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <?php include("footer.php"); ?>
</div>


</body>
</html>
