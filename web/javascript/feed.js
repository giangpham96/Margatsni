'use strict';

var colCount = 0;
var colWidth = 0;
var margin = 20;
var windowWidth = 0;
var blocks = [];
/*function setupBlocks() {
    windowWidth = window.innerWidth;
    colWidth = window.document.querySelectorAll(".block").clientWidth;
    colCount = Math.floor(windowWidth/(colWidth+margin));
    for(var i=0; i < colCount; i++) {
        blocks.push(margin);
    }
    alert(blocks);
}
function positionBlocks() {
    var block = window.document.getElementsByTagName(".block");
    for (var i=0, max = block.length)
    $('.block').each(function(){
        var min = Array.min(blocks);
        var index = $.inArray(min, blocks);
        var leftPos = margin+(index*(colWidth+margin));
        $(this).css({
            'left':leftPos+'px',
            'top':min+'px'
        });
        blocks[index] = min+block.outerHeight()+margin;
    });
}

// Function to get the Min value in Array
Array.min = function(array) {
    return Math.min.apply(Math, array);
};
*/
$(function(){
	$(window).resize(setupBlocks);
});

function setupBlocks() {
	windowWidth = $(window).width();
	colWidth = $('.block').outerWidth();
	blocks = [];
	console.log(blocks);
	colCount = Math.floor(windowWidth/(colWidth+margin*2));
	for(var i=0;i<colCount;i++){
		blocks.push(margin);
	}
	positionBlocks();
}

function positionBlocks() {
	$('.block').each(function(){
		var min = Array.min(blocks);
		var index = $.inArray(min, blocks);
		var leftPos = margin+(index*(colWidth+margin));
		$(this).css({
			'left':leftPos+'px',
			'top':min+'px'
		});
		blocks[index] = min+$(this).outerHeight()+margin;
	});	
}

// Function to get the Min value in Array
Array.min = function(array) {
    return Math.min.apply(Math, array);
};