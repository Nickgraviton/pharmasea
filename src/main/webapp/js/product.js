const slider = document.querySelector("#my-range");
const output = document.querySelector("#output");
const addPriceButton = document.querySelector("#add-price-button");
const editButton = document.querySelector("#edit-button");
const reloadButton = document.querySelector("#reload-button");
const productInfo = document.querySelector("#product-view-info");

const params = new URLSearchParams(window.location.search);
id = params.get("id");

let locationAsk = false;
output.innerHTML = slider.value + "km"; // Display the default slider value

function showPosition(position) {
	localStorage.setItem("geoLat", position.coords.latitude);
	localStorage.setItem("geoLng", position.coords.longitude);
}

// Update the current slider value (each time you drag the slider handle)
slider.addEventListener("input", e => {
	output.innerHTML = slider.value + "km";
  	localStorage.setItem("geoDist", slider.value);
  	if(!locationAsk && localStorage.getItem("geoLat") == undefined)
	  navigator.geolocation.getCurrentPosition(showPosition);
  	locationAsk = true;
})

addPriceButton.addEventListener("click", e => {
	window.location.replace("cp-add-price.html?id=" + id);
})

editButton.addEventListener("click", e => {
	window.location.replace("cp-edit-medicine.html?id=" + id);
})

reloadButton.addEventListener("click", e => {
	window.location.reload();
})

if(localStorage.getItem("token")) {
	addPriceButton.disabled = false;
	editButton.disabled = false;
}

const errorMsg = document.querySelector("#error-msg"); 

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

async function getProduct() {
	const url = "observatory/api/products/" + id;

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

getProduct()
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	productInfo.innerHTML = `
		Name: ${data.name}<br>
		Address: ${data.description}<br>
		Category: ${data.category}<br>
		Tags: ${data.tags}<br>
	`;
}).catch(function(rejection) {
	error("Error viewing product. Try again later.");
})

const geoDist = localStorage.getItem("geoDist");
const geoLat = localStorage.getItem("geoLat");
const geoLng = localStorage.getItem("geoLng");
let url = "observatory/api/prices?dateFrom=2000-01-01&dateTo=2100-01-01&products=";

if(geoDist == undefined || geoLat  == undefined || geoLng == undefined)
	url += id;
else
	url += id + "&geoDist=" + geoDist + "&geoLat=" + geoLat + "&geoLng=" + geoLng;

async function searchPrices() {
	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

searchPrices()
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
		const table = document.createElement("table");
		table.setAttribute("align", "center");
		table.classList.add("table", "table-striped");
		const searchResultsDiv = document.querySelector("#product-view-prices");
		searchResultsDiv.appendChild(table);

		table.innerHTML = `
			<th>Shop</th>
			<th>Price</th>
			<th>Last Updated</th>
			<th>Address</th>
		`;

		// Print the results as rows
		for (const price of data.prices) {
			const tableResultRow = document.createElement("tr");
			table.appendChild(tableResultRow);
	
			tableResultRow.innerHTML = 
				`<td><a href="shop.html?id=${price.shopId}">${price.shopName}</a></td>` +
				`<td>${price.price}€</td>` +
				`<td>${price.date}</td>` +
				`<td>${price.shopAddress}</td>`;
		}

		const latLng = L.latLng(37.971626,23.726670);
		const myMap = L.map("multimap").setView(latLng, 13);

		L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
			attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
			maxZoom: 18,
			id: 'mapbox/streets-v11',
			tileSize: 512,
			zoomOffset: -1,
			accessToken: 'pk.eyJ1IjoiZ2l0Z3VkIiwiYSI6ImNqc3J1dnBzazFoZWs0M280eWNvaHVzc2EifQ.v3kcNa8Zdkwgn6Joq7N5-g'
		}).addTo(myMap);

		let addr = [];
		let name = [];

		for (const price of data.prices) {
			addr.push(price.shopAddress);
			name.push(price.shopName);
		}

		requests = new Array(addr.length);
		markers = new Array(addr.length);
		markerString = new Array(addr.length);

		for (let i = 0; i < addr.length; i++) {
			let url='https://dev.virtualearth.net/REST/v1/Locations/' + addr[i] +
				'?o=json&key=AvjCC7Itxwd50bq3hMyIGJYreE4RZ74KhbG0aTb8AfwFBG6rf3RRxEzp9tfeQTtD';
			fetch(url, {
				method: "GET"
			}).then(function(response) {
				if (response.ok) {
					return response.json();
				}
				return Promise.reject(response);
			}).then(function(data) {
					const lg = data.resourceSets[0].resources[0].point.coordinates[0];
					const late = data.resourceSets[0].resources[0].point.coordinates[1];

					// Map marker
					markers[i] = new L.marker(L.latLng(lg, late)).addTo(myMap);
					markerString[i] = "<b>" + name[i] + "</b><br />" + addr[i];
					markers[i].bindPopup(markerString[i],{closeOnClick: false, autoClose: false}).openPopup();
			}).catch(function(rejection) {
				error("Error loading shop locations. Try again later.");
			})
		}

		if(geoDist != undefined && geoLat != undefined && geoLng != undefined) {
			L.circle([geoLat, geoLng], (geoDist * 1000) + 1500).addTo(myMap);
		}
		
		if(data.prices.length == 0) {
			const empty = document.createTextNode("This product is not currently being sold in pharmacy within range.");
			document.getElementById("product-view-prices").appendChild(empty);
		} 
}).catch(function(rejection) {
	error("Error fetching data. Try again later.");
})