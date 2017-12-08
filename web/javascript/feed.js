'use strict';

var colCount = 0;
var colWidth = 0;
var margin = 20;
var windowWidth = 0;
var blocks = [];
function setupBlocks() {
    windowWidth = window.innerWidth;
    console.log(windowWidth);
    colWidth = Array.from(window.document.getElementsByClassName("block"))[0].offsetWidth;
    console.log(colWidth)
    blocks=[];
    colCount = Math.floor(windowWidth / (colWidth+margin*2));
    for(var i=0; i < colCount; i++) {
        blocks.push(margin);
    }
    positionBlocks();
}
function positionBlocks() {
    var block = document.getElementsByClassName("block");
    for (var i=0, max = block.length;i<max;i++){
        var min = Array.min(blocks);
        
        var index = blocks.indexOf(min);
        var leftPos = margin+(index*(colWidth+margin));
        block[i].style.left = leftPos + 'px';
        block[i].style.top = min + 'px';
        blocks[index] = min + block[i].clientHeight+margin;
    }
    
}

window.onresize = setupBlocks;
 //Function to get the Min value in Array
Array.min = function(array) {
    return Math.min.apply(Math, array);
};

//$(function(){
//	$(window).resize(setupBlocks);
//});
//
//function setupBlocks() {
//	windowWidth = $(window).width();
//	colWidth = $('.block').outerWidth();
//    console.log(colWidth);
//	blocks = [];
//	console.log(blocks);
//	colCount = Math.floor(windowWidth/(colWidth+margin*2));
//	for(var i=0;i<colCount;i++){
//		blocks.push(margin);
//	}
//	positionBlocks();
//}
//
//function positionBlocks() {
//	$('.block').each(function(){
//		var min = Array.min(blocks);
//		var index = $.inArray(min, blocks);
//		var leftPos = margin+(index*(colWidth+margin));
//		$(this).css({
//			'left':leftPos+'px',
//			'top':min+'px'
//		});
//		blocks[index] = min+$(this).outerHeight()+margin;
//	});	
//}
//
//// Function to get the Min value in Array
//Array.min = function(array) {
//    return Math.min.apply(Math, array);
//};