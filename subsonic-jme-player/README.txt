<versionInfo version="3.4" buildDate="12.23.2008" buildNumber=1234/>

<indexes lastModified="237462836472342">
  <index name="A">
    <artist name="ABBA" path="c:/music/abba"/>
    <artist name="Alphaville" path="c:/music/alphaville"/>
  </index>
  <index name="XYZ">
    <artist name="Zoo, The" path="c:/music/the zoo"/>
  </index>
</indexes>

<directory path="c:/music/abba" name="ABBA">
    <child path="c:/music/abba/bestof" name="Best Of" isDir="true" coverart="c:/music/abba/bestof/folder.jpg"/>
    <child path="c:/music/abba/gold" name="Gold" isDir="true"/>
    <child path="c:/music/abba/foo.mp3" name="Foo" isDir="false" contentType="audio/mpeg"/>
</directory>

http://localhost/subsonic/xml/getVersionInfo?user=sindre&password=secret
http://localhost/subsonic/xml/getArtistIndexes?user=sindre&password=secret
http://localhost/subsonic/xml/getDirectory?user=sindre&password=secret&path=c:/music/abba

Implement version info.
Implement Abaout page.
Handle busy state in player controller.
Go to parent dir when browsing back (?)
Add icon indicating whether this song or album.
Format bytes with kB/MB?
Pause music on incoming call.
Avoid menu flickering.  Coalesce songChanged() and stateChange().
Improve layout in PlayerScreen.
Create Help menu in Settings.
Stop animation timer when screen disappears.  Make sure there are not a lot of threads used.
Refactor PlayerScreen.stateChanged(). Make Command subclass with setEnabled() method?
Include exception class name in Util.showError()
Implement musicdirectory cachin in CachedMusicService.
Make Maven build work.
Authentication. Send username/password in each request?
Use UTF8HEX in paths.
Use multicontroller, ala wap.
BreadCrumbTrail
Use checkboxes to select which songs to play?  Plus button to select all.
Show coverart.
Show media time and duration.
Set default player ID in jad.