<script type="text/javascript">
    var quoteIndex = 0;

    var quotes = new Array();
    quotes[0] = "Please don't ever stop development on this project! Subsonic 4 life!";
    quotes[1] = "I've used many media servers and this is by far the best.";
    quotes[2] = "It's the best streaming app I've ever seen! And I've tried them all.";
    quotes[3] = "You release often and listen to user feedback. Awesome!";
    quotes[4] = "I am extremely impressed with the stability of the program.";
    quotes[5] = "I just switched from Jinzora and I'm really impressed about Subsonic. The performance is great.";
    quotes[6] = "I just installed Subsonic and immediately forgot about all previous php-based jukeboxes (including my own...)";
    quotes[7] = "Subsonic is beautiful in simplicity of the end user interface. I had no issues setting it up and the guide was brilliant.";
    quotes[8] = "One word describes Subsonic: AWESOME!";

    var authors = new Array();
    authors[0] = "cup0spam";
    authors[1] = "ClemsonJeeper";
    authors[2] = "Eloquence";
    authors[3] = "Eloquence";
    authors[4] = "chugmonkey";
    authors[5] = "k3tana";
    authors[6] = "cellulit";
    authors[7] = "labrat-radio";
    authors[8] = "Ghostrider";

    function hideQuote() {
        new Effect.Opacity("quote", { from: 1.0, to: 0.0, duration: 1.5 });
        setTimeout(showQuote, 1700);
    }

    function showQuote() {
        $("quote").update('"' + quotes[quoteIndex] + '"&nbsp;&nbsp;&nbsp;&ndash;&nbsp;' + authors[quoteIndex ]);
        quoteIndex = (quoteIndex + 1) % quotes.length;

        new Effect.Opacity("quote", { from: 0.0, to: 1.0, duration: 1.5 });
        setTimeout(hideQuote, 4000);
    }

    setTimeout(hideQuote, 4000);

</script>

<div class="sidebox" style="height:75px">

    <h2>What people say</h2>
    <div id="quote" style="font-size: 11px;">
        "Just the media server I need! I have been using Andromeda and AjaxAmp etc but Subsonic beats everything!"&nbsp;&nbsp;&nbsp;&ndash;&nbsp;Marc
    </div>
</div>
