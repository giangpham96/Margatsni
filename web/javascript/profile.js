/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
'use strict'
let page = 0;

const setupNavbar = () => {
    const home = document.getElementsByClassName('slogan')[0];
    home.addEventListener('click', () => {
        window.location.href = 'https://10.114.32.118:8181/GET/feed.html'
    });
};

const setupUpload = () => {
    const fileinput = document.getElementById('file-input');
    fileinput.onchange = () => {
        const pick = document.getElementById('pick-button');
        if (fileinput.value == '') {
            pick.setAttribute('src', 'image/ic_pick.png');
            return;
        }
        pick.setAttribute('src', 'image/ic_selected.png');
    };
    const fileupload = document.getElementById('upload-button');
    fileupload.onclick = () => {
        if (fileinput.value == '')
            return;

        const caption = document.getElementById('caption');
        const data = new FormData();

        data.append('file', fileinput.files[0]);
        data.append('caption', caption.value);
        const settings = {
            method: 'POST',
            body: data,
            credentials: 'include',
            headers: {
                'auth-token': getCookie('auth-token')
            }
        };

        fetch('https://10.114.32.118:8181/GET/api/post/new', settings)
                .then((response) => {
                    return response.json();
                })
                .then((json) => {
                    caption.value = '';
                    clearFileInput(fileinput);
                    const article = createArticle(json, json);
                    const root = document.getElementsByClassName('wrapper center padding-40')[0];
                    if (root.children[2])
                        root.insertBefore(article, root.children[3]);
                    else
                        root.appendChild(article)
                })
                .catch((err) => {
                    console.log(err);
                });
    }
};

const updateBio = () => {
    const textbox = document.getElementById('BIO');
    const value = textbox.value;
    if(!value)
        return;
    fetch(`https://10.114.32.118:8181/GET/api/profile`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        },
        body:`fav_quote=${value}`
    }).then((response) => {
        return response.json();
    }).then((json) => {
        window.location.href = 'https://10.114.32.118:8181/GET/profile.html'
    }).catch((err) => {
        console.log(err);
    });
}

const setupProfilePic = () => {
    const avatar = document.getElementById('profile_pic');
    avatar.addEventListener('click', () => {
        const modal = document.getElementsByClassName('modal')[0];
        modal.style.display = 'block';
    });
    const btnUploadProfile = document.getElementById('update-profile');
    const btnCancel = document.getElementById('cancel');
    btnCancel.addEventListener('click', () => {
        const modal = document.getElementsByClassName('modal')[0];
        modal.style.display = "none";
    });
    btnUploadProfile.addEventListener('click', () => {
        const fileinput = document.getElementById('profile-input');
        if (fileinput.value == '')
            return;
        const data = new FormData();
        data.append('file', fileinput.files[0]);
        const settings = {
            method: 'POST',
            body: data,
            credentials: 'include',
            headers: {
                'auth-token': getCookie('auth-token')
            }
        };

        fetch('https://10.114.32.118:8181/GET/api/profile/profilepic', settings)
                .then((response) => {
                    return response.json();
                })
                .then((json) => {
                    location.reload(true);
                })
                .catch((err) => {
                    console.log(err);
                });
        const modal = document.getElementsByClassName('modal')[0];
        modal.style.display = "none";
    });
};

window.onload = () => {
    setupNavbar();
    setupUpload();
    setupProfilePic();
};

const clearFileInput = (ctrl) => {
    try {
        ctrl.value = null;
    } catch (ex) {
    }
    if (ctrl.value) {
        ctrl.parentNode.replaceChild(ctrl.cloneNode(true), ctrl);
    }
};

const loadPage = () => {
    fetch(`https://10.114.32.118:8181/GET/api/profile/me?page=${page}`, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        }
    }).then((response) => {
        if (response.status !== 200) {
            throw Error(response.status);
        }
        return response.json();
    }).then((json) => {
        const avatar = document.getElementsByClassName('avatar')[0];
        const avatar1 = document.getElementById('profile_pic');
        const userIcon = document.getElementsByClassName('profile')[1];
        //set avatar picture of profile page
        const now = Math.floor(Date.now());
        if (json.profile_pic) {
            avatar.setAttribute('src', json.profile_pic+`?${now}`);
            avatar1.setAttribute('src', json.profile_pic+`?${now}`);
            userIcon.setAttribute('src', json.profile_pic+`?${now}`);
        }
        const bioText = document.getElementById('bio_display');
        if (json.fav_quote) {
            bioText.innerHTML = json.fav_quote;
        }
        const uname = document.getElementById('uname_display');
        uname.innerHTML = json.uname;
        
        const root = document.getElementsByClassName('wrapper center padding-40')[0];


        const posts = json.post.posts;

        posts.forEach((p) => {
            const article = createArticle(json, p);
            root.appendChild(article);
        });
    }).catch((err) => {
        console.log(err);
        window.location.href = 'https://10.114.32.118:8181/GET/';
    });
};

const createArticle = (json, p) => {
    const article = document.createElement('article');
    article.className = 'item white shadow cf';

    const divheader = document.createElement('div');
    divheader.className = 'row padding';

    const divavatar = document.createElement('div');
    divavatar.className = 'col-1 col-persist';

    const imgavatar = document.createElement('img');
    imgavatar.className = 'pull-left width-100 round avatar';

    if (json.profile_pic) {
        imgavatar.setAttribute('src', json.profile_pic);
    } else {
        imgavatar.setAttribute('src', './image/avatar.png');
    }

    divavatar.appendChild(imgavatar);
    divheader.appendChild(divavatar);

    const divusername = document.createElement('div');
    divusername.className = "col-11 col-persist gutter-h-10 padding-top-15";

    const h5username = document.createElement('h5');
    h5username.className = "text-15 text700 pull-left red-text";
    h5username.innerHTML = json.uname;

    const ats = document.createElement('a');
    ats.className = 'pull-right label fill-white text-gray';
    ats.innerHTML = timeSince(p.timestamp);

    divusername.appendChild(h5username);
    divusername.appendChild(ats);
    divheader.appendChild(divusername);

    article.appendChild(divheader);

    const divcontent = document.createElement('div');
    divcontent.className = 'row';

    const pcaption = document.createElement('p');
    pcaption.className = 'padding -padding-top';
    if (p.caption)
        pcaption.innerHTML = p.caption;

    divcontent.appendChild(pcaption);
    const img = document.createElement('img');
    img.className = 'pull-left width-100';
    img.setAttribute('src', p.src);

    divcontent.appendChild(img);
    article.appendChild(divcontent);

    if (p.can_like) {
        const divLike = document.createElement('div');
        divLike.className = 'row padding';

        const divLikeChild = document.createElement('div');
        divLikeChild.className = 'pull-left';

        const alikebutton = document.createElement('a');
        alikebutton.className = "btn icon round text-red fill-silver";

        const like_icon = (p.liked)
                ? 'https://image.flaticon.com/icons/png/128/148/148836.png'
                : 'https://image.flaticon.com/icons/png/128/126/126471.png';
        alikebutton.innerHTML = `<img style="border-radius: 100%; height: 1.5em; width:1.5em;" src=${like_icon}>`;


        divLikeChild.appendChild(alikebutton);

        const alikeno = document.createElement('a');
        alikeno.className = "btn white hover-disable text-red text600";

        alikeno.innerHTML = p.likes;

        alikebutton.addEventListener('click', () => {
            like(alikebutton, alikeno, p.postId);
        });

        divLikeChild.appendChild(alikeno);

        divLike.appendChild(divLikeChild);

        article.appendChild(divLike);
    }
    const divComment = document.createElement('div');

    const ulComment = document.createElement('ul');
    ulComment.className = 'overflow';

    p.comments.forEach((c) => {
        const li = renderComment(c);
        ulComment.appendChild(li);
    });

    divComment.appendChild(ulComment);

    if (p.can_comment) {
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
                                <img style="height: 1.5em; width:1.5em; padding: 5px;" src="./image/ic_send.png"/>
                            </a>`;

        divCommentButton.addEventListener('click', () => {
            createComment(ulComment, textArea, p.postId);
        });
        commentForm.appendChild(divCommentButton);
        divComment.appendChild(commentForm);
    }
    article.appendChild(divComment);
    return article;
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

const createComment = (ulComment, textArea, post_id) => {
    if (textArea.value.trim() === '')
        return;
    fetch('https://10.114.32.118:8181/GET/api/comment', {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        },
        credentials: 'include',
        method: 'POST',
        body: `post=${post_id}&content=${textArea.value}`
    })
            .then((response) => {
                return response.json();
            })
            .then((json) => {
                if (!json.message) {
                    const li = renderComment(json);
                    ulComment.appendChild(li);
                    textArea.value = '';
                    return;
                }
            })
            .catch((err) => {
                console.log(err);
            });
};

const renderComment = (c) => {
    const li = document.createElement('li');
    li.className = 'row';

    const divInfo = document.createElement('div');
    divInfo.className = "row padding";

    const divImg = document.createElement('div');
    divImg.className = "col-1 col-persist";
    const ava = (c.profile_pic) ? c.profile_pic : './image/avatar.png';
    divImg.innerHTML = `<img class="pull-left width-100 round avatar" src=${ava} />`;

    divInfo.appendChild(divImg);

    divInfo.innerHTML += `<div class="col-11 col-persist gutter-h-10 padding-top-15">
                                    
                                    <h5 class="text-15 text700 pull-left red-text">${c.uname}</h5>
                                    <a class="pull-right label fill-white text-gray">${timeSince(c.timestamp)}</a>
                                </div>`;
    li.appendChild(divInfo);
    li.innerHTML += c.content;

    return li;
}

const like = (alikebutton, alikeno, post_id) => {
    fetch('https://10.114.32.118:8181/GET/api/like', {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        },
        credentials: 'include',
        method: 'POST',
        body: `post=${encodeURIComponent(post_id)}`
    })
            .then((response) => {
                return response.json();
            })
            .then((json) => {
                if (!json.message) {
                    const like_icon = (json.liked)
                            ? 'https://image.flaticon.com/icons/png/128/148/148836.png'
                            : 'https://image.flaticon.com/icons/png/128/126/126471.png';
                    alikebutton.innerHTML = `<img style="border-radius: 100%; height: 1.5em; width:1.5em;" src=${like_icon}>`;
                    alikeno.innerHTML = json.likes;
                    return;
                }
            })
            .catch((err) => {
                console.log(err);
            });
}

const timeSince = (date) => {

    let seconds = Math.floor((new Date() - date) / 1000);

    let interval = Math.floor(seconds / 31536000);

    if (interval > 1) {
        return interval + " years ago";
    }
    interval = Math.floor(seconds / 2592000);
    if (interval > 1) {
        return interval + " months ago";
    }
    interval = Math.floor(seconds / 86400);
    if (interval > 1) {
        return interval + " days ago";
    }
    interval = Math.floor(seconds / 3600);
    if (interval > 1) {
        return interval + " hours ago";
    }
    interval = Math.floor(seconds / 60);
    if (interval > 1) {
        return interval + " minutes ago";
    }
    if (seconds < 0)
        seconds = 0;
    return Math.floor(seconds) + " seconds";
};

loadPage();