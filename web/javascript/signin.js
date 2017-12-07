/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

fetch('api/v1/authorized', {credentials: 'include'})
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

        const data = new FormData();
        data.append('email', emailInput.value)
        data.append('password', pwInput.value)

        fetch('api/v1/authorized', {
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
                        errorMsg.innerHTML = json.error
                        return
                    }
//                    window.location.href = "https://10.114.32.118:8181/GET/feed.html"
                })
                .catch((err) => {
                    console.log(err)
                });

    })
}