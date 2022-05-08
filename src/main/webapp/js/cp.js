if (localStorage.getItem("username") == "admin") {
    const controlPanel = document.querySelector(".control-panel-list");
    
    const usersLink = document.createElement("a");
    usersLink.setAttribute("href", "cp-users.html");
    usersLink.classList.add("cp-item");
    var usersText = document.createTextNode("Manage users");
    usersLink.appendChild(usersText);

    controlPanel.insertBefore(document.createElement("br"), controlPanel.firstChild);
    controlPanel.insertBefore(usersLink, controlPanel.firstChild);
}

// Use cp logout button to perform the logout
const cpLogoutButton = document.querySelector("#cp-logout");

async function logout() {
    const token = localStorage.getItem("token");
	const url = "observatory/api/logout/";

	const response = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
            "X-OBSERVATORY-AUTH": token
		}
	});
	return response;
}

cpLogoutButton.addEventListener("click", e => {
    e.preventDefault();

    logout()
    .then(function(response) {
        if (response.ok) {
            localStorage.removeItem("username");
            localStorage.removeItem("token");
            window.location.replace("index.html");
        }
        return Promise.reject(response);
    }).catch(function(rejection) {
        alert("Error logging out");
    })
})