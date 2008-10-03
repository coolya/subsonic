$(document).ready(function() {

    $.localScroll();

	$("#home-reviews").newsticker().css({height:'6em'});
	$("#awards-fader").newsticker().css({height:'110px'});

	$('#whygetgoldbox').hide();
	$('a.whygetgold').click(function(){
		$('#whygetgoldbox').slideToggle('slow');
		return false;
	});

	$('#existinggoldbox').hide();
	$('a.existinggold').click(function(){
		$('#existinggoldbox').slideToggle('slow');
		return false;
	});

	/* ---------- Banner Control/Animation ---------- */

	// $('#bannernavigation').show();

	currentSlide = 0;
	numberSlides = $('#bannercontent div.slide').length;

	numberSlides--; // making the numbers work!

	function slideTo(slideNumber) {
		currentSlide = slideNumber;
		if(currentSlide>numberSlides) {
			currentSlide = 0;
			slideNumber = 0;
		}
		/*
		if(currentSlide==0) {
			$('#banner-prev').hide();
		} else {
			$('#banner-prev').show();
		}
		if(currentSlide==numberSlides) {
			$('#banner-next').hide();
		} else {
			$('#banner-next').show();
		}
		*/
		slidePosition = slideNumber*900;
		$('#bannercontent').animate({left: '-'+slidePosition+'px'},{duration:1000,complete:bannerLoop});
	}

	slideTo(0);

	$('#banner-full').prepend('<div id="slideprogress"><div></div></div>');
	$('#banner-mini').prepend('<div id="slideprogress"><div></div></div>');
	$('#slideprogress').css({width:'100px',height:'2px',right:'4px',bottom:'4px',border:'1px solid #fff',position:'absolute'}).fadeTo(1,.3);
	$('#slideprogress div').css({width:'0px',height:'2px',backgroundColor:'#ccc'});

	function bannerLoop() {
		$('#slideprogress div').animate({width:'100%'},{duration:10000,easing:'linear',complete:bannerLoop2});
	}
	function bannerLoop2() {
		slideTo(currentSlide+1);
		$('#slideprogress div').animate({width:'0px'},{duration:500});
	}

	/*
	$('#banner-next').click(function() {
		slideTo(currentSlide+1);
	});

	$('#banner-prev').click(function() {
		slideTo(currentSlide-1);
	});

	$('#banner-next').hover(function() {
		$(this).addClass('hover');
	}, function() {
		$(this).removeClass('hover');
	});

	$('#banner-prev').hover(function() {
		$(this).addClass('hover');
	}, function() {
		$(this).removeClass('hover');
	});
	*/

	/* ----------- Menu Actions ----------
	$('#menu-information a,#menu-addons a').click(function() {
		$('#nav a').removeClass('active');
		$(this).addClass('active');
		var menuID = $(this).parent().attr('id');
		$('#subnav').slideUp(100,function() {
			$('#subnav ul').hide();
			$('#sub-'+menuID).show();
			$('#subnav').slideDown(100);
		});
		return false;
	});
	*/

	$('#language').hover(function() {
		$('#language .list').slideDown(1);
	}, function() {
		$('#language .list').slideUp(1);
	});

});