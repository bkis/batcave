
$(document).ready(function() {

	var jsonString = '{"meta":{"volume":"Volume1","chapter":"KapitelDings","page":"32"},"objects":[{"display":"Peter","tags":["NN"]},{"display":"ist","tags":["V_PP"]},{"display":"heute","tags":["PRÄP_TEMP"]},{"display":"wieder","tags":["CONJ"]},{"display":"besonders","tags":["ADV","ADJ"]},{"display":"begriffsstutzig.","tags":["ADJ"]}]}';
	var json = JSON.parse(jsonString);

	//extract and prepare data
	var legend = getLegend(json);

	//fill legend in DOM
	$("#bc-legend").empty();
	for (var key in legend) {
		$("#bc-legend").append('<span class="bc-legend-item" data-tag="' + key + '">' + key + '</span>');
	}
	
	//set legend colors
	$(".bc-legend-item").each(function() {
		$( this ).css( "border-color", legend[$(this).attr("data-tag")] );
	});
	
	//set hover actions for legend items
	$(".bc-legend-item").hover(function() {
		$(this).addClass("bc-legend-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", legend[$(this).attr("data-tag")]);
	}, function() {
		$(this).removeClass("bc-legend-item-hover");
		$(".bc-object[data-tag*='" + $(this).attr("data-tag") + "']").css("background-color", "#fff");
	});
	
	//fill display
	$("#bc-display").append(getDisplayData(json));

	//set hover actions for display objects
	$(".bc-object").hover(function() {
		$(".bc-legend-item").mouseenter();
	}, function() {
		$(".bc-legend-item").mouseout();
	});
});

