// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request = new XMLHttpRequest();
// API CONTROLS
// example: ?start=0&count=4&status=ALL&sort=name%7CASC
// start: the starting element
// count: amount of elements to display
// status: ACTIVE/WITHDRAWN/ALL
// sort: name,id|ASC,DESC (pipe doesn't work so you have to use %7C)

console.log(queryDict.count);
if(queryDict.type == 'medicine') // medicine GET requests
{
	if(queryDict.count != null) request.open('GET', 'https://localhost:8765/observatory/api/products/name/' + queryDict.q + '?count=' + queryDict.count + "&status=ACTIVE", true);
	else 						request.open('GET', 'https://localhost:8765/observatory/api/products/name/' + queryDict.q + '?count=20' + "&status=ACTIVE", true);
}
else // pharmacy GET requests
{
	if(queryDict.count != null) request.open('GET', 'https://localhost:8765/observatory/api/shops/name/' + queryDict.q + '?count=' + queryDict.count + "&status=ACTIVE", true);
	else 						request.open('GET', 'https://localhost:8765/observatory/api/shops/name/' + queryDict.q + '?count=20' + "&status=ACTIVE", true);
}

function error()
{
	var errorString = "Υπήρξε ένα σφάλμα στην αναζήτηση. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.getElementById("search-results").appendChild(errorText);

	console.log('error');
}

request.onreadystatechange = function () 
{
	if(request.readyState == 4) // replacement for request.onload
	{
		var data = JSON.parse(this.response);
		console.log(data);

		if (request.status >= 200 && request.status < 400) 
		{	
			if(queryDict.type === 'medicine')	// medicines (products)
			{
				// Create search results table
				var table = document.createElement("TABLE");
				table.setAttribute("align", "center");
				table.setAttribute("class", "table table-striped");
				document.getElementById("search-results").appendChild(table);

				var tableHeaderName = document.createElement("TH")
				var tableHeaderNameText = document.createTextNode("Name");
				tableHeaderName.appendChild(tableHeaderNameText);
				table.appendChild(tableHeaderName);

				var tableHeaderDesc = document.createElement("TH")
				var tableHeaderDescText = document.createTextNode("Description");
				tableHeaderDesc.appendChild(tableHeaderDescText);
				table.appendChild(tableHeaderDesc);

				var tableHeaderCat = document.createElement("TH")
				var tableHeaderCatText = document.createTextNode("Category");
				tableHeaderCat.appendChild(tableHeaderCatText);
				table.appendChild(tableHeaderCat);

				// And print the search results as rows

				for(var i=0; i < data.products.length; i++)
				{
					var ay = data.products[i];

					var tableResultNameText = document.createTextNode(ay.name);
					var tableResultDescText = document.createTextNode(ay.description);
					var tableResultCatText = document.createTextNode(ay.category);
				
					var tableResultRow = document.createElement("TR")
					table.appendChild(tableResultRow);

					var tableResultName = document.createElement("TD");
					var tableResultNameLink = document.createElement("A");
					tableResultNameLink.setAttribute('href', 'product.html?id=' + ay.id);
					tableResultNameLink.appendChild(tableResultNameText);
					tableResultName.appendChild(tableResultNameLink);
					tableResultRow.appendChild(tableResultName);

					var tableResultDesc = document.createElement("TD");
					tableResultDesc.appendChild(tableResultDescText);
					tableResultRow.appendChild(tableResultDesc);

					var tableResultCat = document.createElement("TD");
					tableResultCat.appendChild(tableResultCatText);
					tableResultRow.appendChild(tableResultCat);
				}
			}
			else if(queryDict.type === 'pharmacy')	// pharmacies (shops)
			{
				var table = document.createElement("TABLE");
				table.setAttribute("align", "center");
				table.setAttribute("class", "table table-striped");
				document.getElementById("search-results").appendChild(table);

				var tableHeaderName = document.createElement("TH")
				var tableHeaderNameText = document.createTextNode("Name");
				tableHeaderName.appendChild(tableHeaderNameText);
				table.appendChild(tableHeaderName);

				var tableHeaderAddr = document.createElement("TH")
				var tableHeaderAddrText = document.createTextNode("Address");
				tableHeaderAddr.appendChild(tableHeaderAddrText);
				table.appendChild(tableHeaderAddr);

				// And print the search results as rows

				for(var i=0; i < data.shops.length; i++)
				{
					var ay = data.shops[i];

					var tableResultNameText = document.createTextNode(ay.name);
					var tableResultAddrText = document.createTextNode(ay.address);
				
					var tableResultRow = document.createElement("TR")
					table.appendChild(tableResultRow);

					var tableResultName = document.createElement("TD");
					var tableResultNameLink = document.createElement("A");
					tableResultNameLink.setAttribute('href', 'shop.html?id=' + ay.id);
					tableResultNameLink.appendChild(tableResultNameText);
					tableResultName.appendChild(tableResultNameLink);
					tableResultRow.appendChild(tableResultName);

					var tableResultAddr = document.createElement("TD");
					tableResultAddr.appendChild(tableResultAddrText);
					tableResultRow.appendChild(tableResultAddr);
				}
			}
			else 
			{
				error();
			}
		} 
		else 
		{
			error();
		}
	}
}

request.send();	