//load color-hash util and set color lightness
var colorHash = new ColorHash({lightness: 0.6});

//original scan width (set when img loaded)
var scanWidth = 1;

//returns color based on tag-string
function col(tagString){
	return colorHash.hex(tagString);
}

//show position in scan
function showScanPosition(x, y, w, h){
	var scaleFactor = getScaleFactor();
	//draw box
	$("#bc-scan").prepend(
		'<div id="scanbox"></div>'
	);
	//show image portion
	//$("#scan-img").css("margin-top", "-" + parseInt((y-200) * scaleFactor) + "px");
	//set position and dimensions
	$("#scanbox").css("width", (w * scaleFactor) + 20);
	$("#scanbox").css("height", (h * scaleFactor) + 20);
	$("#scanbox").css("top", (y * scaleFactor) - 10);
	$("#scanbox").css("left", (x * scaleFactor) - 10);
	
	//setViewport(document.getElementById("scan-img"), x * scaleFactor, y * scaleFactor);
}

//hide position in scan
function hideScanPosition(){
	$("#scanbox").remove();
	setViewport(document.getElementById("scan-img"), 0, 0);
}

//calculate scan positions relative to scan size
function getScaleFactor(){
	return parseInt($("#scan-img").prop("width")) / scanWidth;
}

function setViewport(img, x, y) {
    img.style.left = "-" + (x-50) + "px";
    img.style.top  = "-" + (y-50) + "px";
}

function getScanWidth(){
	$("#scan-img").load(function(){
		scanWidth = $(this).prop("naturalWidth");
	});
}

//analysis window back button
function setupAnalysisBackButton(){
	$("#bc-analysis-close").click(function(){
		$("#bc-analysis").fadeOut(function (){
			$("#bc-display").fadeIn();
		});
	});
}


//build frontend elements based on data
function initPageJS(){
	//insert line breaks
	$(".bc-object.new-line").before("<br>");
	
	//set tags colors
	$(".bc-tags-item").each(function() {
		$( this ).css( "border-color", col($(this).attr("data-tag")) );
	});
	
	//set hover actions for tags items
	$(".bc-tags-item").hover(function() {
		$(this).addClass("bc-tags-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", col($(this).attr("data-tag")) );
	}, function() {
		if ($(this).attr("data-locked") == "false"){
			$(this).removeClass("bc-tags-item-hover");
			$(".bc-object[data-locked='false'][data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", "#fff");
		}
	});
	
	//set click actions for tags items
	$(".bc-tags-item").click(function() {
		//lock tag items and display objects
		if ($(this).attr("data-locked") == "false"){
			$(this).attr("data-locked", "true")
				.addClass("bc-tags-item-locked");
			$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']")
				.attr("data-locked", "true");
		} else {
			$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']")
				.attr("data-locked", "false");
			$(this)
				.attr("data-locked", "false")
				.removeClass("bc-tags-item-locked");;
		}
	});

	//set hover actions for display objects
	$(".bc-object").hover(function() {
		var target = $(this);
		$(".bc-tags-item[data-tag*='" + $(this).attr("data-tag") + "']").each(function(){
			$(this).mouseenter();
		});
		//var index = parseInt(target.attr("data-index"));
		showScanPosition(
				$(this).attr("data-x"),
				$(this).attr("data-y"),
				$(this).attr("data-w"),
				$(this).attr("data-h")
		);
	}, function() {
		var target = $(this);
		$(".bc-tags-item[data-tag*='" + $(this).attr("data-tag") + "']").each(function(){
			$(this).mouseout();
		});
		hideScanPosition();
	});
	
	//set click actions for display objects
	$(".bc-object").click(function() {

		$("#bc-display").fadeOut(function(){
			$("#bc-analysis").fadeIn();
			setupAnalysisBackButton();
			//scroll
			$('html, body').animate({
		        scrollTop: $("#bc-analysis").offset().top
		    }, 100);
		});
		
		var target = $(this);
		
		var request = $.ajax({
		    url: '/similarity',
		    type: 'GET',
		    data: { word: target.text(), language : $("#bc-display").attr("data-language")} ,
		    contentType: 'text/plain; charset=utf-8'
		});

		request.done(function(data) {
		      $("#bc-analysis").html(data);
		});
		
	});
	
	//set click action for tag-reset button
	$("#bc-tags-reset").click(function() {
		$(".bc-tags-item[data-locked='true']").click().mouseout();
	});
	
	$("#bc-display").mouseleave(function(){
		$("#scan-img").css("margin-top", "0px");
	});
	
}


//jQuery execute when doc fully loaded
$(document).ready(function() {
	getScanWidth();
	initPageJS();
});