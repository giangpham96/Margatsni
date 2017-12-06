'use strict';
const tabs = document.querySelectorAll('a');
const panels = document.querySelectorAll('.tab');
for(var i = 0; i < tabs.length; i++) {
  var tab = tabs[i];
  setTabHandler(tab, i);
}
function setTabHandler(tab, tabPos) {
  tab.onclick = function() {
    for(i = 0; i < tabs.length; i++) {
      tabs[i].className = 'inactive';
    }
    tab.className = 'active';
    for(i = 0; i < panels.length; i++) {
      panels[i].className = 'hidden';
    }
    panels[tabPos].className = 'active-panel tab';
  };
}


fetch('https://10.114.32.118:8181/GET/api/authorized', {
    credentials: 'include', 
    headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'auth-token': getCookie('auth-token')
            }})
        .then((response) => {
            return response.json();
        })
        .then((json) => {
            if (json.message == "authorized") {
//                window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                return
            }
            initViews();
        })
        .catch((err) => {
            console.log(err)
        });

const initViews = () => {
    setupLogin()
    setupSignup()
}

const setupLogin = () => {
    const loginForm = document.getElementById('logInbtn')
    loginForm.onclick = function(){
        const emailInput = document.querySelector('#logInForm > input[type="email"]');
        const pwInput = document.querySelector('#logInForm > input[type="password"]');
        
        fetch('https://10.114.32.118:8181/GET/api/authorized', {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'include',
            method: 'POST',
            body: `email=${emailInput.value}&password=${pwInput.value}`
        })
                .then((response) => {
                    return response.json();
                })
                .then((json) => {
                    if (json.message) {
//                        errorMsg.innerHTML = json.error
                        
                    console.log(json.message)
                        return
                    }
        document.cookie = "auth-token" + "=" + json['auth-token'] + ";" + "path=/";
            
//                    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err)
                });

    };
};

const setupSignup = () => {
    const loginForm = document.getElementById('signUpbtn')
    loginForm.onclick = function(){
        const unameInput = document.querySelector('#signUpForm > input[type="text"]');
        const emailInput = document.querySelector('#signUpForm > input[type="email"]');
        const pwInput = document.querySelector('#signUpForm > input[type="password"]');
        
        fetch('https://10.114.32.118:8181/GET/api/signup', {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'include',
            method: 'POST',
            body: `uname=${unameInput.value}&email=${emailInput.value}&password=${pwInput.value}`
        })
                .then((response) => {
                    return response.json();
                })
                .then((json) => {
                    if (json.message) {
//                        errorMsg.innerHTML = json.error
                        
                    console.log(json.message)
                        return
                    }
        document.cookie = "auth-token" + "=" + json['auth-token'] + ";" + "path=/";
            
//                    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err)
                });

    };
};


function getCookie(cname) {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}