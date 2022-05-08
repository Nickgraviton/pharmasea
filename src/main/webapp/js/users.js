const errorMsg = document.querySelector("#error-msg"); 

function error() {
	const errorString = "Error completing request.";
	errorMsg.innerHTML = errorString;
}

async function getUsers() {
	const token = localStorage.getItem("token");
	const url = "observatory/api/users/";

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		}
	})
	return response;
}

async function deleteUser(id) {
	const token = localStorage.getItem("token");
	const url = "observatory/api/users/" + id;

	const response = await fetch(url, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		}
	})
	return response;
}

getUsers()
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	if (data) {
		const table = document.createElement("table");
		table.setAttribute("align", "center");
		table.classList.add("table", "table-striped");
		const userListDiv = document.querySelector(".user-list");
		userListDiv.appendChild(table);
	
		table.innerHTML = `
			<th>Username</th>
			<th>First Name</th>
			<th>Last Name</th>
			<th>e-mail</th>
			<th>Delete</th>
		`;
	
		// Print the results as rows
		for (const user of data.users) {
			const tableResultRow = document.createElement("tr")
			table.appendChild(tableResultRow);
	
			tableResultRow.innerHTML =
				`<td>${user.username}</td>` +
				`<td>${user.fname}</td>` +
				`<td>${user.lname}</td>` +
				`<td>${user.email}</td>`;
	
			const deleteLink = `<td><a class="delete-button" href="#" id="` + user.id + `">Delete</a></td>`;
			
			if(user.username != "admin") {
				tableResultRow.innerHTML += deleteLink;
			}
		}
	}
	// This section needs to be inside the promise so that the buttons have been created
	const deleteButtons = document.querySelectorAll(".delete-button");

	for (const button of deleteButtons) {

		button.addEventListener("click", e => {
			let buttonId = button.id;
			const id = buttonId.replace('user', '');
			let q = confirm("You are about to delete this user. Are you sure?");

			if(q) {
				deleteUser(id)
				.then(function(response) {
					if (response.ok) {
						return response;
					}
					return Promise.reject(response);
				}).then(function(response) {
					window.location.replace("cp-users.html");
				}).catch(function(rejection) {
					error();
				})
			}
		})
	}	
}).catch(function(rejection) {
	error();
})
