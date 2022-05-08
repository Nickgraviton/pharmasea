const urlParams = new URLSearchParams(window.location.search);
const token = localStorage.getItem("token");
const id = urlParams.get("id");
const name = document.querySelector("#name");
const pharmacy = document.querySelector("#pharmacy");
const price = document.querySelector("#price");
const dateFrom = document.querySelector("#dateFrom");
const dateTo = document.querySelector("#dateTo");

const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

async function getProduct(id) {
	const url = "observatory/api/products/" + id;

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

getProduct(id)
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	const productName = document.querySelector("#name");
	productName.value = data.name;
}).catch(function(rejection) {
	error("Error getting product name. Try again later.");
})

async function getShops() {
	const url = "observatory/api/shops";

	const response = await fetch(url, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

getShops()
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	for (const shop of data.shops) {
		let option = document.createElement("option");
		option.text = shop.name;
		option.value = shop.id;
		pharmacy.options.add(option);
	}
}).catch(function(rejection) {
	error("Error fetching shop names. Try again later.");
})

dateTo.max = new Date().toISOString().split("T")[0]; // max
dateFrom.max = dateTo.max;

const addPriceButton = document.querySelector("#add-price-form-submit");

async function addPrice(parameters) {
	const url = "observatory/api/prices/";

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

addPriceButton.addEventListener("click", e => {
	e.preventDefault();
	const parameters = "price=" + price.value + "&dateFrom=" +
	dateFrom.value + "&dateTo=" + dateTo.value + "&productId=" +
	id + "&shopId=" + pharmacy.value;

	addPrice(parameters)
	.then(function(response) {
		if (response.ok) {
			return response.json();
		}
		return Promise.reject(response);
	}).then(function(data) {
			alert("Successfully added price.");
	}).catch(function(rejection) {
		error("Error adding price. Try again later.");
	})
})