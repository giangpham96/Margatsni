'use strict';

let colCount = 0;
let colWidth = 0;
let margin = 20;
let windowWidth = 0;
let blocks = [];
const setupBlocks = () => {
    windowWidth = window.innerWidth;
    console.log(windowWidth);
    if (window.document.getElementsByClassName("block").length===0)
        return;
    colWidth = Array.from(window.document.getElementsByClassName("block"))[0].offsetWidth;
    blocks = [];
    colCount = Math.floor(windowWidth / (colWidth + margin * 2));
    for (let i = 0; i < colCount; i++) {
        blocks.push(margin);
    }
    positionBlocks();
};
const positionBlocks = () => {
    let block = document.getElementsByClassName("block");
    for (let i = 0, max = block.length; i < max; i++) {
        let min = Array.min(blocks);

        let index = blocks.indexOf(min);
        let leftPos = margin + (index * (colWidth + margin));
        block[i].style.left = leftPos + 'px';
        block[i].style.top = min + 'px';
        blocks[index] = min + block[i].clientHeight + margin;
    }

};

window.onresize = setupBlocks;
//Function to get the Min value in Array
Array.min = function (array) {
    return Math.min.apply(Math, array);
};

let page = 0;

const loadFeed = () => {
    fetch(`https://10.114.32.118:8181/GET/api/feed?page=${page}`, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        }
    }).then((response) => {
        return response.json();
    }).then((json) => {
        const content = document.getElementById('content');
        json.forEach((p) => {
            const post = document.createElement('img');
            post.className = 'block';
            post.setAttribute('src', p.src);
            content.appendChild(post);
        });
        setupBlocks();
        page++;
    }).catch((err) => console.log(err));
};

const getCookie = (cname) => {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
};

loadFeed();

const moveToFeed = () => window.location.href = 'https://10.114.32.118:8181/GET/feed.html';

