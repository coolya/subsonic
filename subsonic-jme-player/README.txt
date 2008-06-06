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

<directory path="c:/music/abba" name="ABBA" parent="c:/music">
  <children>
    <child path="c:/music/abba/bestof" name="Best Of" isDir="true" coverart="c:/music/abba/bestof/folder.jpg"/>
    <child path="c:/music/abba/gold" name="Gold" isDir="true"/>
    <child path="c:/music/abba/foo.mp3" name="Foo" isDir="false" contentType="audio/mpeg"/>
  </children>
</directory>


http://localhost/subsonic/xml/getVersionInfo?user=sindre&password=secret
http://localhost/subsonic/xml/getArtistIndexes?user=sindre&password=secret
http://localhost/subsonic/xml/getDirectory?user=sindre&password=secret&path=c:/music/abba

o Authentication
  - send username/password in each request?

o Use UTF8HEX in paths.

o Use multicontroller, ala wap.

o Cache all artists.

o Create cache from path to <directory> info.

o Create service proxy, with mock implementation to simplify development of gui.

o Only support for playing one song.

o BreadCrumbTrail

o Use checkboxes to select which songs to play?  Plus button to select all.

o Create main menu.