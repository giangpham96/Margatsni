/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
'use strict'
let page = 0;
const loadPage = () => {
    fetch(`https://10.114.32.118:8181/GET/api/profile/me?page=${page}`, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        }
    }).then((response) => {
        return response.json();
    }).then((json) => {
        const avatar = document.getElementsByClassName('avatar')[0];
        //set avatar picture of profile page
        if(json.profile_pic)
            avatar.setAttribute('src', json.profile_pic);
        
        const root = document.getElementsByClassName('wrapper center padding-40')[0];
        
        
        const posts = json.post.posts;
        
        posts.forEach((p) => {
            const article = document.createElement('article');
            article.className = 'item white shadow cf';
            
            const divheader = document.createElement('div');
            divheader.className = 'row padding';
            
            const divavatar = document.createElement('div');
            divavatar.className = 'col-1 col-persist';
            
            const imgavatar = document.createElement('img');
            imgavatar.className = 'pull-left width-100 round avatar';
            
            if (p.profile_pic) {
                imgavatar.setAttribute('src', p.profile_pic);
            } else {
                imgavatar.setAttribute('src', './image/avatar.png');
            }
            
            divavatar.appendChild(imgavatar);
            divheader.appendChild(divavatar);
            
            const divusername = document.createElement('div');
            divusername.className = "col-11 col-persist gutter-h-10 padding-top-15";
            
            const h5username = document.createElement('h5');
            h5username.className = "text-15 text700 pull-left";
            h5username.innerHTML = json.uname;
            
            const ats = document.createElement('a');
            ats.className = 'pull-right label fill-white text-gray';
            ats.innerHTML = '12h ago';
            
            divusername.appendChild(h5username);
            divusername.appendChild(ats);
            divheader.appendChild(divusername);
            
            article.appendChild(divheader);
            
            const divcontent = document.createElement('div');
            divcontent.className = 'row';
            
            const pcaption = document.createElement('p');
            pcaption.className = 'padding -padding-top';
            if(p.caption)
                pcaption.innerHTML = p.caption;
            
            divcontent.appendChild(pcaption);
            const img = document.createElement('img');
            img.className = 'pull-left width-100';
            img.setAttribute('src', p.src);
            
            divcontent.appendChild(img);
            article.appendChild(divcontent);

            const divLike = document.createElement('div');
            divLike.className = 'row padding';
            
            const divLikeChild = document.createElement('div');
            divLikeChild.className = 'pull-left';
            
            const alikebutton = document.createElement('a');
            alikebutton.className = "btn icon round text-red fill-silver";
            
            alikebutton.innerHTML = `<img style="border-radius: 100%; height: 1.5em; width:1.5em;" src="https://cdn.pixabay.com/photo/2013/07/12/14/15/like-148087_960_720.png">`;
            
            divLikeChild.appendChild(alikebutton);
            
            const alikeno = document.createElement('a');
            alikeno.className = "btn white hover-disable text-red text600";
            
            alikeno.innerHTML = p.likes;
            
            divLikeChild.appendChild(alikeno);
            
            divLike.appendChild(divLikeChild);
            
            article.appendChild(divLike);
            
            const divComment = document.createElement('div');

            const ulComment = document.createElement('ul');
            ulComment.className = 'overflow';
            
            p.comments.forEach((c) => {
                const li = document.createElement('li');
                li.className = 'row';
                
                const divInfo = document.createElement('div');
                divInfo.className = "row padding";
                
                const divImg = document.createElement('div');
                divImg.className = "col-1 col-persist";
                const ava = (c.profile_pic) ? c.profile_pic : './image/avatar.png';
                divImg.innerHTML = `<img class="pull-left width-100 round" src=${ava} />`;
                
                divInfo.appendChild(divImg);
                
                divInfo.innerHTML += `<div class="col-11 col-persist gutter-h-10 padding-top-15">
                                    
                                    <h5 class="text-15 text700 pull-left">${c.uname}</h5>
                                    <a class="pull-right label fill-white text-gray">12h ago</a>
                                </div>`;
                li.appendChild(divInfo);
                li.innerHTML += c.content;
                ulComment.appendChild(li);
            });
            
            divComment.appendChild(ulComment);
            
            const commentForm = document.createElement('form');
            commentForm.className = 'form relative padding';
            const divAvaComment = document.createElement('div');
            divAvaComment.className = 'col-1 col-persist';
            const imgAvaComment = document.createElement('img');
            imgAvaComment.className = 'pull-left width-100 round avatar';
            imgAvaComment.setAttribute('src', (json.profile_pic) ? json.profile_pic : "./image/avatar.png");
            divAvaComment.appendChild(imgAvaComment);
            commentForm.appendChild(divAvaComment);
            
            const divCommentText = document.createElement('div');
            divCommentText.className = 'col-9 col-persist gutter-h-10 padding-top-5';
            const textArea = document.createElement('textarea');
            textArea.setAttribute('placeholder', "Write a comment...");
            divCommentText.appendChild(textArea);
            commentForm.appendChild(divCommentText);
           
            const divCommentButton = document.createElement('div');
            divCommentButton.className = 'col-2 col-persist';
            divCommentButton.innerHTML = `<a class="btn l icon round text-gray hover-text-red">
                                <i class="fa fa-video-camera"></i>
                            </a>`;
            
            divCommentButton.addEventListener('click', () => {
                console.log(textArea.value);
            });
            commentForm.appendChild(divCommentButton);
            divComment.appendChild(commentForm);
            
            article.appendChild(divComment);
            root.appendChild(article);
        });
    }).catch((err) => {
        console.log(err);
    });
};

const getCookie = (cname) => {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
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

loadPage();