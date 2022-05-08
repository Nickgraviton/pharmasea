const signupForm = document.querySelector("#signup-form");
const signupButton = document.querySelector("#signup-form-submit");
const errorMsg = document.querySelector("#error-msg"); 

function error() {
	const errorString = "Error signing up. Try again later.";
	errorMsg.innerHTML = errorString;
}

async function signup(username, fname, lname, email, password) {
	const url = "observatory/api/users/";
	const parameters = "username=" + username + "&password=" + password +
		"&FName=" + fname + "&LName=" + lname + "&email=" + email;

	const response = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		},
		body: parameters
	})
	return response;
}

signupButton.addEventListener("click", e => {
	e.preventDefault();
	const username = signupForm.username.value;
	const fname = signupForm.fname.value;
	const lname = signupForm.lname.value;
	const email = signupForm.email.value;
	const password = signupForm.password.value;

	signup(username, fname, lname, email, password)
	.then(function(response) {
		if (response.ok) {
			return response;
		}
		return Promise.reject(response);
	}).then(function(response) {
		window.location.replace("signup-success.html");
	}).catch(function(rejection) {
		error();
	})
})