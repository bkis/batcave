//load color-hash util and set color lightness
var colorHash = new ColorHash({lightness: 0.6});

//returns color based on tag-string
function getColorFor(tagString){
	return colorHash.hex(tagString);
}

//returns a json-object that maps tag names to colors
function getTags(json){
	var json = jsonQ(json);
	//extract tags
	var tags = json.find("tags").value();
	tags = Array.from(new Set([].concat.apply([], tags)));

	//build tags json
	var tagsData = {};
	for (var i = 0; i < tags.length; i++) {
		tagsData[tags[i]] = getColorFor(tags[i]);
	}

	//return tags data
	return tagsData;
}

//builds the html-data for the text to display
function getDisplayData(json){
	var out = "";
	
	for (obj in json.objects){
		out += '<span class="bc-object" data-locked="false" data-tag="' + 
				json.objects[obj].tags + '">' + 
				json.objects[obj].display + '</span>';
	}
	
	return out;
}

//jQuery execute when doc fully loaded
$(document).ready(function() {

	var jsonString = '{"meta":{"volume":"Volume1","chapter":"KapitelDings","page":"32"},"objects":[{"display":"Peter","tags":["NN"]},{"display":"ist","tags":["V_PP"]},{"display":"heute","tags":["PRÄP_TEMP"]},{"display":"wieder","tags":["CONJ"]},{"display":"besonders","tags":["ADV"]},{"display":"begriffsstutzig.","tags":["ADJ"]}]}';
	var json = JSON.parse(jsonString);

	//extract and prepare data
	var tags = getTags(json);

	//fill tags area in DOM
	$("#bc-tags").children(".bc-tags-item").detach();
	for (var key in tags) {
		$("#bc-tags").append('<span class="bc-tags-item" data-tag="' + key + '" data-locked="false">' + key + '</span>');
	}
	
	//fill display
	$("#bc-display").append(getDisplayData(json));
	
	//set tags colors
	$(".bc-tags-item").each(function() {
		$( this ).css( "border-color", tags[$(this).attr("data-tag")] );
	});
	
	//set hover actions for tags items
	$(".bc-tags-item").hover(function() {
		$(this).addClass("bc-tags-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", tags[$(this).attr("data-tag")]);
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
	}, function() {
		var target = $(this);
		$(".bc-tags-item").each(function(){
			if (target.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseout();
			}
		});
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
	
});

