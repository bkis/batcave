
$(document).ready(function() {

	var jsonString = '{"meta":{"volume":"Volume1","chapter":"KapitelDings","page":"32"},"objects":[{"display":"Haus","tags":["NN","ADV","V_PP"]},{"display":"bauen","tags":["V_PP"]}]}';
	var jsonQ = getJsonQ(jsonString);

	//extract and prepare data
	var legend = getLegend(jsonQ);

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
		$(".bc-object[data-tag='" + $(this).attr("data-tag") + "']").css("background-color", legend[$(this).attr("data-tag")]);
	}, function() {
		$(".bc-object[data-tag='" + $(this).attr("data-tag") + "']").css("background-color", "#fff");
	});
	
	//fill display
	$("#bc-display").append(getDisplayData(jsonQ));


});

