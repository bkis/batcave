//load color-hash util and set color lightness
var colorHash = new ColorHash({lightness: 0.6});

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
	//draw box
	$("#bc-extra").prepend(
		'<div id="scanbox"></div>'
	);
	//set position and dimensions
	$("#scanbox").css("top", y);
	$("#scanbox").css("left", x);
	$("#scanbox").css("width", w);
	$("#scanbox").css("height", h);
}
//hide position in scan
function hideScanBox(){
	$("#scanbox").remove();
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
				$(this).attr("data-scan-x"),
				$(this).attr("data-scan-y"),
				$(this).attr("data-scan-w"),
				$(this).attr("data-scan-h")
		);
	}, function() {
		var target = $(this);
		$(".bc-tags-item").each(function(){
			if (target.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseout();
			}
		});
		showScanPosition(false);
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
});

