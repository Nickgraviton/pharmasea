const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get("id");

const token = localStorage.getItem("token");

const name = document.querySelector("#name");
const address = document.querySelector("#address");
const tags = document.querySelector("#tags");
const editPharmacyButton = document.querySelector("#edit-pharmacy-form-submit");
const deletePharmacyButton = document.querySelector("#delete-pharmacy-button");
const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

fetch("observatory/api/shops/" + id, {
	method: "GET"
}).then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data){
	name.value = data.name;
	address.value = data.address;
	tags.value = data.tags.join();
})

async function editPharmacy(parameters) {
	const url = "observatory/api/shops/" + id;

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

async function deletePharmacy() {
	const url = "observatory/api/shops/" + id;
	const response = await fetch(url, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"X-OBSERVATORY-AUTH": token
		}
	})
	return response;
}

editPharmacyButton.addEventListener("click", e => {
	e.preventDefault();

	const geourl = 'https://dev.virtualearth.net/REST/v1/Locations/' + address.value + '?o=json&key=AvjCC7Itxwd50bq3hMyIGJYreE4RZ74KhbG0aTb8AfwFBG6rf3RRxEzp9tfeQTtD';
	fetch(geourl, {
		method: "GET"
	}).then(function(response) {
		if (response.ok) {
			return response.json();
		}
		return Promise.reject(response);
	}).then(function(data) {
		const lat = parseFloat(data.resourceSets[0].resources[0].point.coordinates[0]);
		const lng = parseFloat(data.resourceSets[0].resources[0].point.coordinates[1]);

		let parameters = "name=" + name.value + "&address=" + address.value +
			"&lat=" + lat + "&lng=" + lng + "&withdrawn=false";
		for (const tag of tags.value.split(",")) {
			parameters += "&tags=" + tag;
		}

		editPharmacy(parameters)
		.then(function(response) {
			if (response.ok) {
				return response.json();
			}
			return Promise.reject(response);
		}).then(function(data) {
			alert("Successfully edited pharmacy.");
		}).catch(function(rejection) {
			error("Error editing pharmacy. Try again later.");
		})
	})
})

deletePharmacyButton.addEventListener("click", e => {
	e.preventDefault();

	let q = confirm("Are you sure you want to delete this pharmacy?");

	if(q) {
		deletePharmacy()
		.then(function(response) {
			if (response.ok) {
				return response.json();
			}
			return Promise.reject(response);
		}).then(function(data) {
			alert("Successfully deleted pharmacy.");
			window.location.replace("index.html");
		}).catch(function(rejection) {
			error("Error deleting pharmacy. Try again later.");
		})
	}
})