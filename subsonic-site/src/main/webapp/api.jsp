<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%! String current = "api"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1>Subsonic API</h1>
            <p>
                The Subsonic API allows anyone to build their own programs using Subsonic as the server, whether they're
                on the web, the desktop or on mobile devices. As an example, the Subsonic Android and iPhone <a href="apps.jsp">apps</a> are built using the
                Subsonic API.
            </p>

            <h2 class="div">Introduction</h2>
            <p>
                The Subsonic API allows you to call methods that respond in <a href="http://en.wikipedia.org/wiki/Representational_State_Transfer">REST</a> style xml.
                Individual methods are detailed below.
            </p>
            <p>
                Please note that all methods take the following parameters:
            </p>

            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>u</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The username.</td>
                </tr>
                <tr>
                    <td><code>p</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The password, either in clear text or hex-encoded with a "enc:" prefix.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>v</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The protocol version implemented by the client, i.e., the version of the
                        <code>subsonic-rest-api.xsd</code> schema used (see below).</td>
                </tr>
                <tr>
                    <td><code>c</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>A unique string identifying the client application.</td>
                </tr>
            </table>

            <p>
                For example:
            </p>
            <p>
                <code>http://your-server/rest/getIndexes.view?u=joe&amp;p=sesame&amp;v=1.1.0&amp;c=myapp</code>, or<br/>
                <code>http://your-server/rest/getIndexes.view?u=joe&amp;p=enc:736573616d65&amp;v=1.1.0&amp;c=myapp</code>
            </p>
            <p>
                Also note that UTF-8 should be used when sending parameters to API methods. The XML returned
                will also be encoded with UTF-8.
            </p>

            <p>
                All methods (except those that return binary data) returns XML documents conforming to the
                <code>subsonic-rest-api.xsd</code> schema. This schema (as well as example XML documents) can be found
                at <code>http://your-server/xsd/</code>
            </p>

            <h2 class="div">Error handling</h2>
            <p>
                If a method fails it will return an error code and message in an <code>&lt;error&gt;</code> element.
                In addition, the <code>status</code> attribute of the <code>&lt;subsonic-response&gt;</code> root element
                will be set to <code>failed</code> instead of <code>ok</code>. For example:
            </p>

            <pre>
   &lt;?xml version="1.0" encoding="UTF-8"?&gt;
   &lt;subsonic-response xmlns="http://subsonic.org/restapi"
                      status="failed" version="1.1.0"&gt;
       &lt;error code="40" message="Wrong username or password"/&gt;
   &lt;/subsonic-response&gt;
            </pre>

            <p>
                The following error codes are defined:

            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Code</th>
                    <th class="param-heading">Description</th>
                </tr>
                <tr class="table-altrow"><td><code>0</code></td><td>A generic error.</td></tr>
                <tr>                     <td><code>10</code></td><td>Required parameter is missing.</td></tr>
                <tr class="table-altrow"><td><code>20</code></td><td>Incompatible Subsonic REST protocol version. Client must upgrade.</td></tr>
                <tr>                     <td><code>30</code></td><td>Incompatible Subsonic REST protocol version. Server must upgrade.</td></tr>
                <tr class="table-altrow"><td><code>40</code></td><td>Wrong username or password.</td></tr>
                <tr>                     <td><code>50</code></td><td>User is not authorized for the given operation.</td></tr>
                <tr class="table-altrow"><td><code>60</code></td><td>The trial period is over. Please donate to get a license key.</td></tr>
            </table>

            <h2 class="div">ping</h2>
            <p>
                <code>http://your-server/rest/ping.view</code>
            </p>
            <p>
                Used to test connectivity with the server.  Takes no extra parameters.
            </p>
            <p>
                Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
            </p>

            <h2 class="div">getLicense</h2>
            <p>
                <code>http://your-server/rest/getLicense.view</code>
            </p>
            <p>
                Get details about the software license.  Takes no extra parameters.  Please note that access to the
                REST API requires that the server has a valid license. To get a license key you can give a donation
                to the Subsonic project.
            </p>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;license&gt;</code>
                element on success.
            </p>

            <h2 class="div">getMusicFolders</h2>
            <p>
                <code>http://your-server/rest/getMusicFolders.view</code>
            </p>
            <p>
                Returns all configured music folders. Takes no extra parameters.
            </p>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;musicFolders&gt;</code>
                element on success.
            </p>

            <h2 class="div">getNowPlaying</h2>
            <p>
                <code>http://your-server/rest/getNowPlaying.view</code>
            </p>
            <p>
                Returns what is currently being played by all users. Takes no extra parameters.
            </p>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;nowPlaying&gt;</code>
                element on success.
            </p>

            <h2 class="div">getIndexes</h2>
            <p>
                <code>http://your-server/rest/getIndexes.view</code>
            </p>
            <p>
                Returns an indexed structure of all artists.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>musicFolderId</code></td>
                    <td>No</td>
                    <td></td>
                    <td>If specified, only return artists in the music folder with the given ID.</td>
                </tr>
                <tr>
                    <td><code>ifModifiedSince</code></td>
                    <td>No</td>
                    <td></td>
                    <td>If specified, only return a result if the artist collection has changed since the given time.</td>
                </tr>
            </table>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;indexes&gt;</code>
                element on success.
            </p>

            <h2 class="div">getMusicDirectory</h2>
            <p>
                <code>http://your-server/rest/getMusicDirectory.view</code>
            </p>
            <p>
                Returns a listing of all files in a music directory. Typically used to get list of albums for an artist,
                or list of songs for an album.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>id</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>A string which uniquely identifies the music folder. Obtained by calls to getIndexes or getMusicDirectory.</td>
                </tr>
            </table>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;musicFolder&gt;</code>
                element on success.
            </p>

            <h2 class="div">search</h2>
            <p>
                <code>http://your-server/rest/search.view</code>
            </p>
            <p>
                Returns a listing of files matching the given search criteria. Supports paging through the result.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>artist</code></td>
                    <td>No</td>
                    <td></td>
                    <td>Artist to search for.</td>
                </tr>
                <tr>
                    <td><code>album</code></td>
                    <td>No</td>
                    <td></td>
                    <td>Album to searh for.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>title</code></td>
                    <td>No</td>
                    <td></td>
                    <td>Song title to search for.</td>
                </tr>
                <tr>
                    <td><code>any</code></td>
                    <td>No</td>
                    <td></td>
                    <td>Searches all fields.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>count</code></td>
                    <td>No</td>
                    <td>20</td>
                    <td>Maximum number of results to return.</td>
                </tr>
                <tr>
                    <td><code>offset</code></td>
                    <td>No</td>
                    <td>0</td>
                    <td>Search result offset. Used for paging.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>newerThan</code></td>
                    <td>No</td>
                    <td></td>
                    <td>Only return matches that are newer than this. Given as milliseconds since 1970.</td>
                </tr>
            </table>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;searchResult&gt;</code>
                element on success.
            </p>

            <h2 class="div">getPlaylists</h2>
            <p>
                <code>http://your-server/rest/getPlaylists.view</code>
            </p>
            <p>
                Returns the ID and name of all saved playlists.
            </p>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;playlists&gt;</code>
                element on success.
            </p>


            <h2 class="div">getPlaylist</h2>
            <p>
                <code>http://your-server/rest/getPlaylist.view</code>
            </p>
            <p>
                Returns a listing of files in a saved playlist.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>id</code></td>
                    <td>yes</td>
                    <td></td>
                    <td>ID of the playlist to return, as obtained by <code>getPlaylists</code>.</td>
                </tr>
            </table>
            <p>
                Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;playlist&gt;</code>
                element on success.
            </p>

            <h2 class="div">download</h2>
            <p>
                <code>http://your-server/rest/download.view</code>
            </p>
            <p>
                Downloads a given music file.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>id</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>A string which uniquely identifies the file to download. Obtained by calls to getMusicDirectory.</td>
                </tr>
            </table>
            <p>
                Returns binary data on success.
            </p>

            <h2 class="div">stream</h2>
            <p>
                <code>http://your-server/rest/stream.view</code>
            </p>
            <p>
                Streams a given music file.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>id</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>A string which uniquely identifies the file to stream. Obtained by calls to getMusicDirectory.</td>
                </tr>
            </table>
            <p>
                Returns binary data on success.
            </p>

            <h2 class="div">getCoverArt</h2>
            <p>
                <code>http://your-server/rest/getCoverArt.view</code>
            </p>
            <p>
                Returns a cover art image.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>id</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>A string which uniquely identifies the cover art file to download. Obtained by calls to getMusicDirectory.</td>
                </tr>
                <tr>
                    <td><code>size</code></td>
                    <td>No</td>
                    <td></td>
                    <td>If specified, scale image to this size.</td>
                </tr>
            </table>
            <p>
                Returns binary data on success.
            </p>

            <h2 class="div">changePassword</h2>
            <p>
                <code>http://your-server/rest/changePassword.view</code>
            </p>
            <p>
                Changes the password of an existing Subsonic user, using the following parameters.
                You can only change your own password unless you are the admin user.
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>username</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The name of the user which should change its password.</td>
                </tr>
                <tr>
                    <td><code>password</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The new password of the new user, either in clear text of hex-encoded (see above).</td>
                </tr>
            </table>

            <p>
                Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
            </p>

            <h2 class="div">createUser</h2>
            <p>
                <code>http://your-server/rest/createUser.view</code>
            </p>
            <p>
                Creates a new Subsonic user, using the following parameters:
            </p>
            <table width="100%" class="bottomspace">
                <tr>
                    <th class="param-heading">Parameter</th>
                    <th class="param-heading">Required</th>
                    <th class="param-heading">Default</th>
                    <th class="param-heading">Comment</th>
                </tr>
                <tr class="table-altrow">
                    <td><code>username</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The name of the new user.</td>
                </tr>
                <tr>
                    <td><code>password</code></td>
                    <td>Yes</td>
                    <td></td>
                    <td>The password of the new user, either in clear text of hex-encoded (see above).</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>ldapAuthenticated</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is authenicated in LDAP.</td>
                </tr>
                <tr>
                    <td><code>adminRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is administrator.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>settingsRole</code></td>
                    <td>No</td>
                    <td>true</td>
                    <td>Whether the user is allowed to change settings and password.</td>
                </tr>
                <tr>
                    <td><code>streamRole</code></td>
                    <td>No</td>
                    <td>true</td>
                    <td>Whether the user is allowed to play files.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>jukeboxRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to play files in jukebox mode.</td>
                </tr>
                <tr>
                    <td><code>downloadRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to download files.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>uploadRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to upload files.</td>
                </tr>
                <tr>
                    <td><code>playlistRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to create and delete playlists.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>coverArtRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to change cover art and tags.</td>
                </tr>
                <tr>
                    <td><code>commentRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to create and edit comments and ratings.</td>
                </tr>
                <tr class="table-altrow">
                    <td><code>podcastRole</code></td>
                    <td>No</td>
                    <td>false</td>
                    <td>Whether the user is allowed to administrate Podcasts.</td>
                </tr>
            </table>


            <p>
                Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
            </p>

        </div>

        <div id="side-col">

            <%@ include file="donate.jsp" %>
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
