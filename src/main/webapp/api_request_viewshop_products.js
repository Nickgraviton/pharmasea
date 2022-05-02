// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request2 = new XMLHttpRequest();
request2.open('GET', 'https://localhost:8765/observatory/api/prices?dateFrom=2000-01-01&dateTo=2020-01-01&shops=' + queryDict.id, true);
function error()
{
	var errorString = "Υπήρξε ένα σφάλμα κατά την προβολή του καταστήματος. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.getElementById("shop-view-prices").appendChild(errorText);

	console.log('error');
}

request2.onreadystatechange = function () 
{
	if(request2.readyState == 4) // replacement for request2.onload
	{
		var data2 = JSON.parse(this.response);
		console.log(data2);

		if (request2.status >= 200 && request2.status < 400) 
		{		
			// Create search results table
			var table = document.createElement("TABLE");
			table.setAttribute("align", "center");
			table.setAttribute("class", "table table-striped");
			document.getElementById("shop-view-prices").appendChild(table);

			var tableHeaderProduct = document.createElement("TH")
			var tableHeaderProductText = document.createTextNode("Product");
			tableHeaderProduct.appendChild(tableHeaderProductText);
			table.appendChild(tableHeaderProduct);

			var tableHeaderPrice = document.createElement("TH")
			var tableHeaderPriceText = document.createTextNode("Price");
			tableHeaderPrice.appendChild(tableHeaderPriceText);
			table.appendChild(tableHeaderPrice);

			var tableHeaderDate = document.createElement("TH")
			var tableHeaderDateText = document.createTextNode("Last Updated");
			tableHeaderDate.appendChild(tableHeaderDateText);
			table.appendChild(tableHeaderDate);

			// And print the search results as rows

			for(var i=0; i < data2.prices.length; i++)
			{
				var ay = data2.prices[i];

				var tableResultProductText = document.createTextNode(ay.productName);
				var tableResultPriceText = document.createTextNode(ay.price + "€");
				var tableResultDateText = document.createTextNode(ay.date);
			
				var tableResultRow = document.createElement("TR")
				table.appendChild(tableResultRow);

				var tableResultProduct = document.createElement("TD");
				var tableResultProductLink = document.createElement("A");
				tableResultProductLink.setAttribute('href', 'product.html?id=' + ay.productId);
				tableResultProductLink.appendChild(tableResultProductText);
				tableResultProduct.appendChild(tableResultProductLink);
				tableResultRow.appendChild(tableResultProduct);

				var tableResultPrice = document.createElement("TD");
				tableResultPrice.appendChild(tableResultPriceText);
				tableResultRow.appendChild(tableResultPrice);

				var tableResultDate = document.createElement("TD");
				tableResultDate.appendChild(tableResultDateText);
				tableResultRow.appendChild(tableResultDate);
			}
		} 
		else 
		{
			error();
		}
	}
}

request2.send();	