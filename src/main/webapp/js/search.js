// API CONTROLS
// example: ?start=0&count=4&status=ALL&sort=name%7CASC
// start: the starting element
// count: amount of elements to display
// status: ACTIVE/WITHDRAWN/ALL
// sort: name,id|ASC,DESC (pipe doesn't work so you have to use %7C)

const urlParams = new URLSearchParams(window.location.search);

const errorMsg = document.querySelector("#error-msg");

function error(errorString) {
	errorMsg.innerHTML = errorString;
}

// Select the correct endpoint and append the search query
let url = "";
if (urlParams.get("searchFor") == "medicine") {
	url = "products/name/";
} else {
	url = "shops/name/";
}
url += urlParams.get("searchQuery");

// Add the rest of the parameters or use their default values
const count = urlParams.get("count");
if (count) {
	url += "?count" + count + "&status=ACTIVE";
} else {
	url += "?count=20&status=ACTIVE";
}

async function search(url) {
	const fullUrl = "observatory/api/" + url;

	const response = await fetch(fullUrl, {
		method: "GET",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	})
	return response;
}

search(url)
.then(function(response) {
	if (response.ok) {
		return response.json();
	}
	return Promise.reject(response);
}).then(function(data) {
	if(urlParams.get("searchFor") == "medicine") {
		if (data) {
			if (data.products.length == 0) {
				error("No products found matching your query. Try again!");
			} else {
				const table = document.createElement("table");
				table.setAttribute("align", "center");
				table.classList.add("table", "table-striped");
				const searchResultsDiv = document.querySelector("#search-results");
				searchResultsDiv.appendChild(table);
			
				table.innerHTML = `
					<th>Name</th>
					<th>Description</th>
					<th>Category</th>
					<th>Edit</th>
				`;

				// Print the results as rows
				for (const product of data.products) {
					const tableResultRow = document.createElement("tr");
					table.appendChild(tableResultRow);
			
					tableResultRow.innerHTML = 
						`<td><a href="product.html?id=${product.id}">${product.name}</a></td>` +
						`<td>${product.description}</td>` +
						`<td>${product.category}</td>`;
					
					const editLink =
						`<td><a class="edit-button"` +
						`href="cp-edit-medicine.html?id=${product.id}"` +
						`id="product${product.id}">Edit</a></td>`;
					
					// Check if logged in
					if (localStorage.getItem("token")) {
						tableResultRow.innerHTML += editLink;
					}
				}
			}
		}
	} else {
		if (data) {
			if (data.shops.length == 0) {
				error("No shops found matching your query. Try again!");
			} else {
				const table = document.createElement("table");
				table.setAttribute("align", "center");
				table.classList.add("table", "table-striped");
				const searchResultsDiv = document.querySelector("#search-results");
				searchResultsDiv.appendChild(table);

				table.innerHTML = `
					<th>Name</th>
					<th>Address</th>
					<th>Edit</th>
				`;

				// Print the results as rows
				for (const shop of data.shops) {
					const tableResultRow = document.createElement("tr")
					table.appendChild(tableResultRow);
			
					tableResultRow.innerHTML =
						`<td><a href="shop.html?id=${shop.id}">${shop.name}</a></td>` +
						`<td>${shop.address}</td>`;

					const editLink =
						`<td><a class="edit-button"` +
						`href="cp-edit-pharmacy.html?id=${shop.id}"` +
						`id="product${shop.id}">Edit</a></td>`;
					
					// Check if logged in
					if (localStorage.getItem("token")) {
						tableResultRow.innerHTML += editLink;
					}
				}
			}
		}
	}
}).catch(function(rejection) {
	error("Error fetching data. Try again later.");
})