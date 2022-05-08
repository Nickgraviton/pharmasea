const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get("id");

const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

async function searchPharmacy(id) {
	const url = "observatory/api/shops/" + id;

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

searchPharmacy(id)
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	// Map
	const latLng = L.latLng(data.lat, data.lng);
	const myMap = L.map('map-id').setView(latLng, 13);

	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
	    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
	    maxZoom: 18,
	    id: 'mapbox/streets-v11',
	    tileSize: 512,
	    zoomOffset: -1,
	    accessToken: 'pk.eyJ1IjoiZ2l0Z3VkIiwiYSI6ImNqc3J1dnBzazFoZWs0M280eWNvaHVzc2EifQ.v3kcNa8Zdkwgn6Joq7N5-g'
	}).addTo(myMap);

	// Map marker
	const marker = L.marker(latLng).addTo(myMap);
	const markerString = "<b>" + data.name + "</b><br>" + data.address;
	marker.bindPopup(markerString).openPopup();

	// Shop info under map
	const shopInfoDiv = document.querySelector("#view-shop-info");
	shopInfoDiv.innerHTML =
		`Name: ${data.name}<br>` + 
		`Address: ${data.address}<br>` +
		`Tags: ${data.tags}`;
}).catch(function(rejection) {
	error("Error fetching data. Try again later.");
})

async function searchPrices(id) {
	const url = "observatory/api/prices?dateFrom=2000-01-01&dateTo=2100-01-01&shops=" + id;

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

searchPrices(id)
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
		const table = document.createElement("table");
		table.setAttribute("align", "center");
		table.classList.add("table", "table-striped");
		const searchResultsDiv = document.querySelector("#view-shop-prices");
		searchResultsDiv.appendChild(table);

		table.innerHTML = `
			<th>Product</th>
			<th>Price</th>
			<th>Last Updated</th>
		`;

		// Print the results as rows
		for (const price of data.prices) {
			const tableResultRow = document.createElement("tr");
			table.appendChild(tableResultRow);
	
			tableResultRow.innerHTML = 
				`<td><a href="product.html?id=${price.productId}">${price.productName}</a></td>` +
				`<td>${price.price}€</td>` +
				`<td>${price.date}</td>`;
		}
}).catch(function(rejection) {
	error("Error fetching data. Try again later.");
})