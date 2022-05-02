// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request = new XMLHttpRequest();
request.open('GET', 'https://localhost:8765/observatory/api/products/' + queryDict.id, true);
function error()
{
	var errorString = "Υπήρξε ένα σφάλμα κατά την προβολή του προϊόντος. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.getElementById("product-view-info").appendChild(errorText);

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
			var productInfo1 = document.createTextNode("Name: " + data.name);
			var productInfo2 = document.createTextNode("Address: " + data.description);
			var productInfo3 = document.createTextNode("Category: " + data.category);
			var productInfo4 = document.createTextNode("Tags: " + data.tags);

			document.getElementById("product-view-info").appendChild(productInfo1);
			document.getElementById("product-view-info").appendChild(document.createElement("BR"));
			document.getElementById("product-view-info").appendChild(productInfo2);
			document.getElementById("product-view-info").appendChild(document.createElement("BR"));
			document.getElementById("product-view-info").appendChild(productInfo3);
			document.getElementById("product-view-info").appendChild(document.createElement("BR"));
			document.getElementById("product-view-info").appendChild(productInfo4);	
		} 
		else 
		{
			error();
		}
	}
}

request.send();	