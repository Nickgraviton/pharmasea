const token = localStorage.getItem("token");
const name = document.querySelector("#name");
const address = document.querySelector("#address");
const tags = document.querySelector("#tags");

const createPharmacyButton = document.querySelector("#create-pharmacy-form-submit");

const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

async function createPharmacy(parameters) {
	const url = "observatory/api/shops/";

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

createPharmacyButton.addEventListener("click", e => {
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

		createPharmacy(parameters)
		.then(function(response) {
			if (response.ok) {
				return response.json();
			}
			return Promise.reject(response);
		}).then(function(data) {
			alert("Successfully created pharmacy.");
		}).catch(function(rejection) {
			error("Error creating pharmacy. Try again later.");
		})
	}).catch(function(rejection){
		error("Error fetching address coordinates.");
	})
})