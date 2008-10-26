<?php
   $q = $_POST['query'].'+site:subsonic.sourceforge.net';
    header("Location:http://www.google.com/search?q=$q");
    die();
?>