'use strict';
const tabs = document.querySelectorAll('a');
const panels = document.querySelectorAll('.tab');
let validsu = false;
let validli = false;

//Set up tab switching
const setTabHandler = (tab, tabPos) => {
    tab.addEventListener('click', (evt) => {
        for (let i = 0; i < tabs.length; i++) {
            tabs[i].className = 'inactive';
        }
        tab.className = 'active';
        for (let i = 0; i < panels.length; i++) {
            panels[i].className = 'hidden';
        }
        panels[tabPos].className = 'active-panel tab';
    });
};

for (let i = 0; i < tabs.length; i++) {
    let tab = tabs[i];
    setTabHandler(tab, i);
}

//Log in form set up
const setupLogin = () => {
    const loginForm = document.querySelector('#logInbtn');
    loginForm.addEventListener('click', (evt) => {
        const emailInput = document.querySelector('#logInForm > input[type="email"]');
        const pwInput = document.querySelector('#logInForm > input[type="password"]');
        const errorp = document.querySelector('#errorli');

        emailInput.addEventListener('input', (evt) => {
            if (emailInput.validity.valid) {
                errorp.className = "invisible";
            }
        });

        if (!emailInput.validity.valid) {
            errorp.innerHTML = "invalid email";
            errorp.className = "visible";
        } else {
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
                    if (json.message || json.error) {
                        errorp.innerHTML = json.message;
                        errorp.className = "visible";
                        console.log(json.message);
                        return;
                    }
                    document.cookie = "auth-token" + "=" + json['auth-token'] + ";" + "path=/";
                    errorp.className = "invisible";
                    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err);
                });

        }

    });
};


//Sign up form set up
const setupSignup = () => {
    const loginForm = document.querySelector('#signUpbtn');
    loginForm.addEventListener('click', (evt) => {
        const unameInput = document.querySelector('#signUpForm > input[type="text"]');
        const emailInput = document.querySelector('#signUpForm > input[type="email"]');
        const pwInput = document.querySelector('#signUpForm > input[type="password"]');
        const errorp = document.querySelector('#errorsu');

        emailInput.addEventListener('input', (evt) => {
            if (emailInput.validity.valid) {
                errorp.className = "invisible";
            }
        });

        pwInput.addEventListener('input', (evt) => {
            if (pwInput.validity.valid) {
                errorp.className = "invisible";
            }
        });

        unameInput.addEventListener('input', (evt) => {
            if (unameInput.validity.valid) {
                errorp.className = "invisible";
            }
        });


        if (!unameInput.validity.valid) {
            errorp.innerHTML = "invalid username";
            errorp.className = "visible";
        } else if (!emailInput.validity.valid) {
            errorp.innerHTML = "invalid email";
            errorp.className = "visible";
        } else if (!pwInput.validity.valid) {
            errorp.innerHTML = "invalid password";
            errorp.className = "visible";
        } else {

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
                    if (json.message  || json.error) {
                        errorp.innerHTML = json.message;
                        errorp.className = "visible";
                        console.log(json.message);
                        return;
                    }
                    document.cookie = "auth-token" + "=" + json['auth-token'] + ";" + "path=/";

                    //window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err);
                });
        }

    });
};

//Set up log in and sign up forms
const initViews = () => {
    setupLogin();
    setupSignup();
    document.body.className = "visible"
}

//Checks cookie
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
document.body.className = "invisible";
//Attempt to check if logged in
fetch('https://10.114.32.118:8181/GET/api/authorized', {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'auth-token': getCookie('auth-token')
        }
    })
    .then((response) => {
        return response.json();
    })
    .then((json) => {
        if (json['auth-token']) {
            window.location.href = "https://10.114.32.118:8181/GET/feed.html";
            return;
        }
        initViews();

    })
    .catch((err) => {
        console.log(err);
    });

document.querySelector('#contbtn').addEventListener('click', () => {
    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
});