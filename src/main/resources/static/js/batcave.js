//load color-hash util and set color lightness
var colorHash = new ColorHash({lightness: 0.6});

//original scan width (set when img loaded)
var scanWidth = 1;

//returns color based on tag-string
function col(tagString){
	return colorHash.hex(tagString);
}

//returns a json-object that maps tag names to colors
//function getTags(json){
//	var json = jsonQ(json);
//	//extract tags
//	var tags = json.find("tags").value();
//	tags = Array.from(new Set([].concat.apply([], tags)));
//
//	//build tags json
//	var tagsData = {};
//	for (var i = 0; i < tags.length; i++) {
//		tagsData[tags[i]] = getColorFor(tags[i]);
//	}
//
//	//return tags data
//	return tagsData;
//}

//builds the html-data for the text to display
//function getDisplayData(json){
//	var out = "";
//	var i = 0;
//	for (obj in json.objects){
//		out += '<span class="bc-object" data-locked="false" data-index="' + i + '" data-tag="' + 
//				json.objects[obj].tags + '">' + 
//				json.objects[obj].display + '</span>';
//		i++;
//	}
//	
//	return out;
//}

//show position in scan
function showScanPosition(x, y, w, h){
	var scaleFactor = getScaleFactor();
	//draw box
	$("#bc-scan").prepend(
		'<div id="scanbox"></div>'
	);
	//show image portion
	$("#scan-img").css("margin-top", "-" + parseInt((y-200) * scaleFactor) + "px");
	//set position and dimensions
	$("#scanbox").css("width", (w * scaleFactor) + 20);
	$("#scanbox").css("height", (h * scaleFactor) + 20);
	$("#scanbox").css("top", (200 * scaleFactor) - 10);
	$("#scanbox").css("left", (x * scaleFactor) - 10);
}
//hide position in scan
function hideScanPosition(){
	$("#scanbox").remove();
	$("#scan-img").css("margin-top", "0px");
}
//calculate scan positions relative to scan size
function getScaleFactor(){
	return parseInt($("#scan-img").prop("width")) / scanWidth;
}

function getScanWidth(){
	$("#scan-img").load(function(){
		scanWidth = $(this).prop("naturalWidth");
	});
}


//build frontend elements based on data
function prepareFrontend(json){
	//extract and prepare data
//	var tags = getTags(json);

	//fill tags area in DOM
//	$("#bc-tags").children(".bc-tags-item").detach();
//	for (var key in tags) {
//		$("#bc-tags").append('<span class="bc-tags-item" data-tag="' + key + '" data-locked="false">' + key + '</span>');
//	}
	
	//fill display
	//$("#bc-display").append(getDisplayData(json));
	
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
		$(".bc-tags-item").each(function(){
			if (target.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseenter();
			}
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
		$(".bc-tags-item").each(function(){
			if (target.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseout();
			}
		});
		hideScanPosition();
	});
	
	//set click actions for display objects
	$(".bc-object").click(function() {
		var target = $(this);
		$(".bc-tags-item").each(function(){
			if (target.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).click();
			}
		});
	});
	
	//set click action for tag-reset button
	$("#bc-tags-reset").click(function() {
		$(".bc-tags-item[data-locked='true']").click().mouseout();
	});
}

//jQuery execute when doc fully loaded
$(document).ready(function() {
	prepareFrontend();
	getScanWidth();
});

