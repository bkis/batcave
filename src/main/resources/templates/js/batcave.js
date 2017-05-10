
$(document).ready(function() {

	var jsonString = '{"meta":{"volume":"Volume1","chapter":"KapitelDings","page":"32"},"objects":[{"display":"Peter","tags":["NN"]},{"display":"ist","tags":["V_PP"]},{"display":"heute","tags":["PRÄP_TEMP"]},{"display":"wieder","tags":["CONJ"]},{"display":"besonders","tags":["ADV","ADJ"]},{"display":"begriffsstutzig.","tags":["ADJ"]}]}';
	var json = JSON.parse(jsonString);

	//extract and prepare data
	var tags = getTags(json);

	//fill tags area in DOM
	$("#bc-tags").empty();
	for (var key in tags) {
		$("#bc-tags").append('<span class="bc-tags-item" data-tag="' + key + '">' + key + '</span>');
	}
	
	//set tags colors
	$(".bc-tags-item").each(function() {
		$( this ).css( "border-color", tags[$(this).attr("data-tag")] );
	});
	
	//set hover actions for tags items
	$(".bc-tags-item").hover(function() {
		$(this).addClass("bc-tags-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", tags[$(this).attr("data-tag")]);
	}, function() {
		$(this).removeClass("bc-tags-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", "#fff");
	});
	
	//fill display
	$("#bc-display").append(getDisplayData(json));

	//set hover actions for display objects
	$(".bc-object").hover(function() {
		var hovered = $(this);
		$(".bc-tags-item").each(function(){
			if (hovered.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseenter();
			}
		});
	}, function() {
		var hovered = $(this);
		$(".bc-tags-item").each(function(){
			if (hovered.attr("data-tag").includes($(this).attr("data-tag"))){
				$(this).mouseout();
			}
		});
	});
});

