'use strict';
const tabs = document.querySelectorAll('li');
const panels = document.querySelectorAll('.tab');
for(i = 0; i < tabs.length; i++) {
  var tab = tabs[i];
  setTabHandler(tab, i);
}
function setTabHandler(tab, tabPos) {
  tab.onclick = function() {
    for(i = 0; i < tabs.length; i++) {
      tabs[i].className = '';
    }
    tab.className = 'active';
    for(i = 0; i < panels.length; i++) {
      panels[i].className = '';
    }
    panels[tabPos].className = 'active-panel';
  }
}