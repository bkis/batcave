//jQuery execute when doc fully loaded
$(document).ready(function() {
	//set click event for search result line
	$(".bc-result").click(function() {
		window.location = "page?id=" + $(this).attr("data-id") + "&hl=" + $(this).attr("data-index");
	});
	
});