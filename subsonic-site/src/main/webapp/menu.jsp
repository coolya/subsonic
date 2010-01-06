<div id="logo"><a href="index.php"><img src="inc/img/subsonic.png" alt="Subsonic"/></a></div>

<div class="hide">
</div>

<div id="search">
    <table><tr>
        <form method="post" action="search.php" name="searchForm">
            <td><input type="text" name="query" id="query" size="18" value="Search" onclick="document.searchForm.query.select();"/></td>
            <td><a href="javascript:document.searchForm.submit()"><img src="inc/img/search.png" alt="Search" title="Search"/></a></td>
        </form>
    </tr></table>
</div>

<hr/>
<div id="nav">
    <ul>
        <li id="menu-home"><a href="index.php" class="<?php if ($current == 'home') echo('open');?>"><span>Home</span></a></li>
        <li id="menu-features"><a href="features.php" class="<?php if ($current == 'features') echo('open');?>"><span>Features</span></a></li>
        <li id="menu-screenshots"><a href="screenshots.php" class="<?php if ($current == 'screenshots') echo('open');?>"><span>Screenshots</span></a></li>
        <li id="menu-demo"><a href="demo.php" class="<?php if ($current == 'demo') echo('open');?>"><span>Demo</span></a></li>
        <li id="menu-download"><a href="download.php" class="<?php if ($current == 'download') echo('open');?>"><span>Download</span></a></li>
        <li id="menu-installation"><a href="installation.php" class="<?php if ($current == 'installation') echo('open');?>"><span>Installation</span></a></li>
        <li id="menu-transcoding"><a href="transcoding.php" class="<?php if ($current == 'transcoding') echo('open');?>"><span>Transcoding</span></a></li>
        <li id="menu-changelog"><a href="changelog.php" class="<?php if ($current == 'changelog') echo('open');?>"><span>Change Log</span></a></li>
        <li id="menu-forum"><a href="forum.php" class="<?php if ($current == 'forum') echo('open');?>"><span>Forum</span></a></li>
        <li id="menu-api"><a href="api.php" class="<?php if ($current == 'api') echo('open');?>"><span>API</span></a></li>
    </ul>
</div>
<hr/>
