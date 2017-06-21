//jQuery execute when doc fully loaded
$(document).ready(function() {
	
	$(".controls-btn").click(function() {
		if ($(this).hasClass("controls-btn-disabled")){
			return;
		}
		var btn = $(this);
		$(this).addClass("controls-btn-disabled");
		$(this).parent().parent().find(".controls-result").text("Bitte warten...");
		$(this).parent().parent().find(".controls-icon").attr("src", "/img/icon-loading.gif");
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