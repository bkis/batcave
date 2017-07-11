//jQuery execute when doc fully loaded
$(document).ready(function() {
	
	//system maintenance command button behavior
	$(".controls-btn").click(function() {
		//quit if button disabled
		if ($(this).hasClass("controls-btn-disabled")){
			return;
		}
		//disable button visually
		$(this).addClass("controls-btn-disabled");
		//set wait text
		$(this).parent().parent().find(".controls-result").text("Bitte warten...");
		//set loading icon
		$(this).parent().parent().find(".controls-icon").attr("src", "/img/icon-loading.gif");
		//send request, show result string when finished
		var btn = $(this);
		$(this).parent().parent().find(".controls-result").load(
				"/actions",
				{ action: $(this).attr("data-action") },
				function() {
					$(this).parent().parent()
						.find(".controls-icon")
						.attr("src", "/img/icon-ok.png");
					btn.removeClass("controls-btn-disabled");
		});
	});
	
});