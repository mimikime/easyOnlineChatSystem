
document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    if (loginForm) {
        loginForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !password) {
                alert("请输入用户名和密码");
                return;
            }

            fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            })
            .then(response => {
                if (response.ok) return response.text();
                else throw new Error("用户名或密码错误");
            })
            .then(message => {
                alert(message);
                window.location.href = "/"; // 登录成功后跳转主页
            })
            .catch(error => alert(error.message));
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !password) {
                alert("请输入用户名和密码");
                return;
            }

            fetch("/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            })
            .then(response => {
                if (response.ok) return response.text();
                else throw new Error("用户名已存在");
            })
            .then(message => {
                alert(message);
                window.location.href = "/login"; // 注册成功后跳转登录页
            })
            .catch(error => alert(error.message));
        });
    }


});
