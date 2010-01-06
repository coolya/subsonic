<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'demo';
    include("header.php");
?>

<body>

<a name="top"/>

<div id="container">
    <?php include("menu.php");?>

    <div id="content">
        <div id="main-col">
            <h1>Online Demo</h1>
            <p>
                Try the online demo to get a taste of what Subsonic is all about!
            </p>

            <ul class="list">
                <li>
                    Not all Subsonic's features are available in the demo version. For instance, application settings can not be viewed
                    or changed. Please refer to the <a href="screenshots.php">screenshots</a> to see what you're missing.
                </li>
                <li>
                    All the music in the demo is free, and courtesy of <a href="http://www.jamendo.com/">Jamendo</a> and the respective artists.
                </li>
            </ul>

            <p style="text-align:center;font-size:1.3em"><b><a href="http://gosubsonic.com/demo/login.view?user=guest<?php echo(rand(1, 5));?>&password=guest" target="_blank">Start demo</a></b></p>
        </div>

        <div id="side-col">

            <?php include("download-subsonic.php"); ?>
            <?php include("donate.php"); ?>

        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <?php include("footer.php"); ?>
</div>


</body>
</html>
