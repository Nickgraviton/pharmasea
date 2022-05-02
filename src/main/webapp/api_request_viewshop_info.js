// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request = new XMLHttpRequest();

request.open('GET', 'https://localhost:8765/observatory/api/shops/' + queryDict.id, true);
function error()
{
	var errorString = "Υπήρξε ένα σφάλμα κατά την προβολή του καταστήματος. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.getElementById("shop-view-info").appendChild(errorText);

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
			// Map
			var latlng = L.latLng(data.lat, data.lng);
			console.log(latlng);
			var mymap = L.map('mapid').setView(latlng, 13);

			L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
			attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
	    	maxZoom: 18,
	    	id: 'mapbox.streets',
	    	accessToken: 'pk.eyJ1IjoiZ2l0Z3VkIiwiYSI6ImNqc3J1dnBzazFoZWs0M280eWNvaHVzc2EifQ.v3kcNa8Zdkwgn6Joq7N5-g'
			}).addTo(mymap);

			// Map marker
			var marker = L.marker(latlng).addTo(mymap);
			var markerString = "<b>" + data.name + "</b><br>" + data.address;
			marker.bindPopup(markerString).openPopup();

			var productInfo1 = document.createTextNode("Name: " + data.name);
			var productInfo2 = document.createTextNode("Address: " + data.address);
			var productInfo3 = document.createTextNode("Tags: " + data.tags);

			document.getElementById("shop-view-info").appendChild(productInfo1);
			document.getElementById("shop-view-info").appendChild(document.createElement("BR"));
			document.getElementById("shop-view-info").appendChild(productInfo2);
			document.getElementById("shop-view-info").appendChild(document.createElement("BR"));
			document.getElementById("shop-view-info").appendChild(productInfo3);
		} 
		else 
		{
			error();
		}
	}
}

request.send();