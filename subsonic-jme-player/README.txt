<versionInfo version="3.4" buildDate="12.23.2008" buildNumber=1234/>

<artistIndexes lastModified="237462836472342">
  <artistIndex index="A">
    <artist name="ABBA" path="c:/music/abba"/>
    <artist name="Alphaville" path="c:/music/alphaville"/>
  </artistIndex>
  <artistIndex index="XYZ">
    <artist name="Zoo, The" path="c:/music/the zoo"/>
  </artistIndex>
</artistIndexes>

<directory path="c:/music/abba" name="ABBA" parent="c:/music">
  <children>
    <child path="c:/music/abba/bestof" name="Best Of" isDir="true" coverart="c:/music/abba/bestof/folder.jpg"/>
    <child path="c:/music/abba/gold" name="Gold" isDir="true"/>
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