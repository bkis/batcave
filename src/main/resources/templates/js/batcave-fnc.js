var colorHash = new ColorHash({lightness: 0.7});


function getColorFor(tagString){
	return colorHash.hex(tagString.length + tagString);
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
		legend[tags[i]] = getColorFor(tags[i]);
	}

	//return legend
	return legend;
}


function getDisplayData(jsonQ){
	var data = jsonQ.find("objects").value()[0];
	var out = "";
	
	for (obj in data){
		out += '<span class="bc-object" data-tag="' + data[obj].tags + '">' + data[obj].display + '</span>';
	}
	
	return out;
}
