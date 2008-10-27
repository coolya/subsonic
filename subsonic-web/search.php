<?php
   $q = $_POST['query'].'+site:subsonic.sourceforge.net+OR+site:activeobjects.no';
    header("Location:http://www.google.com/search?q=$q");
    die();
?>
        