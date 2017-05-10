var colorHash = new ColorHash({lightness: 0.7});


function getColorFor(tagString){
	return colorHash.hex(tagString);
}


function getJsonQ(json){
	return jsonQ(json);
}


function getTags(json){
	var jsonQ = getJsonQ(json);
	//extract tags
	var tags = jsonQ.find("tags").value();
	tags = Array.from(new Set([].concat.apply([], tags)));

	//build tags json
	var tagsData = {};
	for (var i = 0; i < tags.length; i++) {
		tagsData[tags[i]] = getColorFor(tags[i]);
	}

	//return tags data
	return tagsData;
}


function getDisplayData(json){
	var out = "";
	
	for (obj in json.objects){
		out += '<span class="bc-object" data-tag="' + 
				json.objects[obj].tags + '">' + 
				json.objects[obj].display + '</span>';
	}
	
	return out;
}
