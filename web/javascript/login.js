fetch('api/authorized', {
    credentials: 'include', 
    headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'auth-token': getCookie('auth-token')
            }})
        .then((response) => {
            return response.json()
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

initViews = () => {
    setupLogin()
//    setupSignup()
}

setupLogin = () => {
    const loginForm = document.getElementById('login')
    loginForm.addEventListener('submit', (event) => {
        event.preventDefault()
        const emailInput = document.querySelector('#login > input[type="email"]')
        const pwInput = document.querySelector('#login > input[type="password"]')
        const errorMsg = document.querySelector('#login > p')

        fetch('api/authorized', {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'include',
            method: 'POST',
            body: `email=${emailInput.value}&password=${pwInput.value}`
        })
                .then((response) => {
                    return response.json()
                })
                .then((json) => {
                    if (json.error) {
//                        errorMsg.innerHTML = json.error
                        
                    console.log(json.error)
                        return
                    }
        document.cookie = "auth-token" + "=" + json['auth-token'] + ";" + "path=/";
            
//                    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err)
                });

    })
}

function getCookie(cname) {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}