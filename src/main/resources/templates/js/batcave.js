
$(document).ready(function() {

	var jsonString = '{"meta":{"volume":"Volume1","chapter":"KapitelDings","page":"32"},"objects":[{"display":"Haus","tags":["NN","ADV","V_PP"]},{"display":"bauen","tags":["V_PP"]}]}';
	var jsonQ = getJsonQ(jsonString);

	//generate legend data object
	var legend = getLegend(jsonQ);

	//fill legend in DOM
	for (var key in legend) {
		$("#bc-legend").append('<span class="bc-legend-item" data-tag="' + key + '">' + key + '</span>');
	}
	//set legend colors
	$(".bc-legend-item").each(function() {
		$( this ).css( "border-color", legend[$(this).attr("data-tag")] );
	});


});

