const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-form-submit");
const errorMsg = document.querySelector("#error-msg"); 

function error() {
	const errorString = "Error logging in. Try again later.";
	errorMsg.innerHTML = errorString;
}

async function login(username, password) {
	const url = "observatory/api/login/";
	const parameters = "username=" + username + "&password=" + password;

	const response = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		},
		body: parameters
	})
	return response;
}

loginButton.addEventListener("click", e => {
	e.preventDefault();
	const username = loginForm.username.value;
	const password = loginForm.password.value;

	login(username, password)
	.then(function(response) {
		if (response.ok) {
			return response.json();
		}
		return Promise.reject(response);
	}).then(function(data) {
			const token = data.token;
			localStorage.setItem("username", username);
			localStorage.setItem("token", token);
			window.location.replace("cp-home.html");
	}).catch(function(rejection) {
		error();
	})
})