const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get("id");

const token = localStorage.getItem("token");

const name = document.querySelector("#name");
const description = document.querySelector("#description");
const category = document.querySelector("#category");
const tags = document.querySelector("#tags");
const editMedicineButton = document.querySelector("#edit-medicine-form-submit");
const deleteMedicineButton = document.querySelector("#delete-medicine-button");
const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

fetch("observatory/api/products/" + id, {
	method: "GET"
}).then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data){
	name.value = data.name;
	description.value = data.description;
	category.value = data.category;
	tags.value = data.tags.join();
})

async function editMedicine(parameters) {
	const url = "observatory/api/products/" + id;

	const response = await fetch(url, {
		method: "PUT",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		},
		body: parameters
	})
	return response;
}

async function deleteMedicine() {
	const url = "observatory/api/products/" + id;
	const response = await fetch(url, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		}
	})
	return response;
}

editMedicineButton.addEventListener("click", e => {
	e.preventDefault();

	let parameters = "name=" + name.value + "&description=" +
		description.value + "&category=" + category.value + "&withdrawn=false";
	for (const tag of tags.value.split(",")) {
		parameters += "&tags=" + tag;
	}

	editMedicine(parameters)
	.then(function(response) {
		if (response.ok) {
			return response.json();
		}
		return Promise.reject(response);
	}).then(function(data) {
		alert("Successfully edited medicine.");
	}).catch(function(rejection) {
		error("Error editing medicine. Try again later.");
	})
})

deleteMedicineButton.addEventListener("click", e => {
	e.preventDefault();

	let q = confirm("Are you sure you want to delete this medicine?");

	if(q) {
		deleteMedicine()
		.then(function(response) {
			if (response.ok) {
				return response.json();
			}
			return Promise.reject(response);
		}).then(function(data) {
			alert("Successfully deleted medicine.");
			window.location.replace("index.html");
		}).catch(function(rejection) {
			error("Error deleting medicine. Try again later.");
		})
	}
})