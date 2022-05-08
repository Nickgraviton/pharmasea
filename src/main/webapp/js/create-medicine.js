const token = localStorage.getItem("token");
const name = document.querySelector("#name");
const description = document.querySelector("#description");
const category = document.querySelector("#category");
const tags = document.querySelector("#tags");

const createMedicineButton = document.querySelector("#create-medicine-form-submit");

const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

async function createMedicine(parameters) {
	const url = "observatory/api/products/";

	const response = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		},
		body: parameters
	})
	return response;
}

createMedicineButton.addEventListener("click", e => {
	e.preventDefault();

	let parameters = "name=" + name.value + "&description=" +
		description.value + "&category=" + category.value + "&withdrawn=false";
	for (const tag of tags.value.split(",")) {
		parameters += "&tags=" + tag;
	}

	createMedicine(parameters)
	.then(function(response) {
		if (response.ok) {
			return response.json();
		}
		return Promise.reject(response);
	}).then(function(data) {
		alert("Successfully created medicine.");
	}).catch(function(rejection) {
		error("Error creating medicine. Try again later.");
	})
})