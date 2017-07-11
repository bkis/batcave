$(document).ready(function() {
	//erase input values when reset button clicked
	$("#bc-search-reset-btn").click(function(){
		 $("#search-form").find('input:text').val('');
	     return false;
	});
	
	//start similarities search when sim.search button clicked
	$("#bc-search-similarities-btn").click(function(){
		window.location = "search-similarities?token=" + $("#search-form").find('#search-token').val();
	});
	
});

