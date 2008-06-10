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
Implement About page.
Handle busy state in player controller.
Go to parent dir when browsing back (?)
Add icon indicating whether this is song or album.
Pause music on incoming call.
Avoid menu flickering.  Coalesce songChanged() and stateChange().
Create Help menu in Settings.
Stop animation timer when screen disappears.  Make sure there are not a lot of threads used.
Refactor PlayerScreen.stateChanged(). Make Command subclass with setEnabled() method?
Include exception class name in Util.showError()
Make Maven build work.
Authentication. Send username/password in each request?
BreadCrumbTrail
Use checkboxes to select which songs to play?  Plus button to select all.
Show coverart.
Show media time and duration.
Set default player ID in jad.
Escape < > & ' " in XML.
Add debug printing of player events etc.
Implement "longName".
Implement logger.
Try to use the createPlayer(url, contenttype) method.
When populating list, make sure that the selected index is cleared.
Can it be wrong duration that makes it stop?
Use bufferingStarted/stopped
Add log statement when changing midlet lifecycle state.
Use minimal TestMIDlet to reproduce error.
Why are there two streams?

Change layout of PlayScreen:
-----------------------------
|  2 of 6                   |
|  World Leader Pretend (b) |
|  R.E.M (b)                |
|                           |
|  Playing (36 KB)          |
|                           |
|    ---------------        |
|    |             |        |
|    |  Coverart   |        |
|    |             |        |
|    ---------------        |
|                           |
| Pause               Menu  |
-----------------------------

On-device debugging with Sony Ericsson Toolkit
file://localhost/C:/progs/SonyEricsson/JavaME_SDK_CLDC/docs/index.html
http://developer.sonyericsson.com/thread/44274?tstart=0
http://developer.sonyericsson.com/docs/DOC-1734
pin 5622


--------------

INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c41636f7573746963204775697461722e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c41636f7573746963204775697461722e6d7033&suffix=.mp3&player=2
*********** entering setupDefaultConfig
** Trying to read log config file: /semc_logging.cl **
********************************************
* Configuraton file: /semc_logging.cl for logging is missing
********************************************
*********** exiting setupDefaultConfig foundConfig=false
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c41636f7573746963204775697461722e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@468ade
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4372756e6368204775697461722e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4372756e6368204775697461722e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4372756e6368204775697461722e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@2b7a99
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020312e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020312e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020312e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffea66f2
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020322e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020322e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4472756d204c6f6f7020322e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ff9bc93c
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c46617420426173732e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c46617420426173732e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c46617420426173732e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@24f04d
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c48656c6c6f2e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c48656c6c6f2e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c48656c6c6f2e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffc13623
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffc13623
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffc13623
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffc13623
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffc13623
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4f7267616e2e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4f7267616e2e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c4f7267616e2e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@4d88d6
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c5361756365722e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c5361756365722e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c5361756365722e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@235ae1
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@235ae1
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@235ae1
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@235ae1
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@235ae1
INFO PlayerController Opening URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c54616d626f7572696e652e6d7033&suffix=.mp3&player=2
INFO PlayerController Creating player for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c54616d626f7572696e652e6d7033&suffix=.mp3&player=2
INFO PlayerController Player created for URL http://sindre.dyndns.org:8080/stream?pathUtf8Hex=633a5c6d757369635c53616d706c65735c54616d626f7572696e652e6d7033&suffix=.mp3&player=2: com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'started' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'bufferingStarted' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController End of stream reached.
DEBUG PlayerController End of stream reached.
DEBUG PlayerController Stream closed.
DEBUG PlayerController Got event 'bufferingStopped' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'durationUpdated' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'endOfMedia' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b
DEBUG PlayerController Got event 'closed' from player com.sonyericsson.mmapi.player.GenericAudioVideoPlayer@ffd6b84b