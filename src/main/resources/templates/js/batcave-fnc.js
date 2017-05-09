function getColorFor(tag){
	
}

function getJsonQ(jsonString){
	return jsonQ(JSON.parse(jsonString));
}

function getLegend(jsonQ){
	//extract tags
	var tags = jsonQ.find("tags").value();
	tags = Array.from(new Set([].concat.apply([], tags)));

	//build legend json
	var legend = {};
	for (var i = 0; i < tags.length; i++) {
		legend[tags[i]] = "#f00";
	}

	//return legend
	return legend;
}

