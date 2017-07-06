$(document).ready(function() {
	
	$("#bc-search-reset-btn").click(function(){
		 $("#search-form").find('input:text').val('');
	     return false;
	});
	
	$("#bc-search-similarities-btn").click(function(){
		window.location = "search-similarities?token=" + $("#search-form").find('#search-token').val();
	});
	
});

