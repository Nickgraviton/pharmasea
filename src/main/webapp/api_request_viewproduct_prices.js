// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request2 = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/prices?dateFrom=2000-01-01&dateTo=2020-01-01&products=';

var geoDist = localStorage.getItem("geoDist");
var geoLat = localStorage.getItem("geoLat");
var geoLng = localStorage.getItem("geoLng");

if(geoDist == undefined || geoLat  == undefined || geoLng == undefined)
	request2.open('GET', url + queryDict.id, true);
else
	request2.open('GET', url + queryDict.id + "&geoDist=" + geoDist + "&geoLat=" + geoLat + "&geoLng=" + geoLng, true);
function error()
{
	var errorString = "Υπήρξε ένα σφάλμα κατά την προβολή του προϊόντος. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	//document.getElementById("product-view-prices").appendChild(errorText);

	//console.log('error');
}

request2.onreadystatechange = function ()
{
	if(request2.readyState == 4) // replacement for request2.onload
	{
		var data = JSON.parse(this.response);
		console.log(data);

		if (request2.status >= 200 && request2.status < 400)
		{
			// Create search results table
			var table = document.createElement("TABLE");
			table.setAttribute("align", "center");
			table.setAttribute("class", "table table-striped");
			document.getElementById("product-view-prices").appendChild(table);

			var tableHeaderShop = document.createElement("TH")
			var tableHeaderShopText = document.createTextNode("Shop");
			tableHeaderShop.appendChild(tableHeaderShopText);
			table.appendChild(tableHeaderShop);

			var tableHeaderPrice = document.createElement("TH")
			var tableHeaderPriceText = document.createTextNode("Price");
			tableHeaderPrice.appendChild(tableHeaderPriceText);
			table.appendChild(tableHeaderPrice);

			var tableHeaderDate = document.createElement("TH")
			var tableHeaderDateText = document.createTextNode("Last Updated");
			tableHeaderDate.appendChild(tableHeaderDateText);
			table.appendChild(tableHeaderDate);

			/*adding address */
			var tableHeaderAddress = document.createElement("TH")
			var tableHeaderAddressText = document.createTextNode("Address");
			tableHeaderAddress.appendChild(tableHeaderAddressText);
			table.appendChild(tableHeaderAddress);

			// And print the search results as rows

			for(var i=0; i < data.prices.length; i++)
			{
				var ay = data.prices[i];

				if(i < data.prices.length-1)
				{
					if(data.prices[i].shopName != data.prices[i+1].shopName) // multiple value check
					{
						var tableResultShopText = document.createTextNode(ay.shopName);
						var tableResultPriceText = document.createTextNode(ay.price + "€");
						var tableResultDateText = document.createTextNode(ay.date);
						var tableResultAddressText = document.createTextNode(ay.shopAddress);

						var tableResultRow = document.createElement("TR")
						table.appendChild(tableResultRow);

						var tableResultShop = document.createElement("TD");
						var tableResultShopLink = document.createElement("A");
						tableResultShopLink.setAttribute('href', 'shop.html?id=' + ay.shopId);
						tableResultShopLink.appendChild(tableResultShopText);
						tableResultShop.appendChild(tableResultShopLink);
						tableResultRow.appendChild(tableResultShop);

						var tableResultPrice = document.createElement("TD");
						tableResultPrice.appendChild(tableResultPriceText);
						tableResultRow.appendChild(tableResultPrice);

						var tableResultDate = document.createElement("TD");
						tableResultDate.appendChild(tableResultDateText);
						tableResultRow.appendChild(tableResultDate);

						var tableAddressShop = document.createElement("TD");
						tableAddressShop.appendChild(tableResultAddressText);
						tableResultRow.appendChild(tableAddressShop);				
					}			
				}
				else
				{
					var tableResultShopText = document.createTextNode(ay.shopName);
					var tableResultPriceText = document.createTextNode(ay.price + "€");
					var tableResultDateText = document.createTextNode(ay.date);
					var tableResultAddressText = document.createTextNode(ay.shopAddress);

					var tableResultRow = document.createElement("TR")
					table.appendChild(tableResultRow);

					var tableResultShop = document.createElement("TD");
					var tableResultShopLink = document.createElement("A");
					tableResultShopLink.setAttribute('href', 'shop.html?id=' + ay.shopId);
					tableResultShopLink.appendChild(tableResultShopText);
					tableResultShop.appendChild(tableResultShopLink);
					tableResultRow.appendChild(tableResultShop);

					var tableResultPrice = document.createElement("TD");
					tableResultPrice.appendChild(tableResultPriceText);
					tableResultRow.appendChild(tableResultPrice);

					var tableResultDate = document.createElement("TD");
					tableResultDate.appendChild(tableResultDateText);
					tableResultRow.appendChild(tableResultDate);

					var tableAddressShop = document.createElement("TD");
					tableAddressShop.appendChild(tableResultAddressText);
					tableResultRow.appendChild(tableAddressShop);							
				}
			}

		/*********************initializing map************************************/
			//	if(mymap2!=undefined){
			//	mymap2.off();
			//	mymap2.remove();
			//	}

			var latlng2 = L.latLng(37.971626,23.726670);
			var mymap2 = L.map('multimap').setView(latlng2, 13);

			L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
			attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
				maxZoom: 18,
				id: 'mapbox.streets',
				accessToken: 'pk.eyJ1IjoiZ2l0Z3VkIiwiYSI6ImNqc3J1dnBzazFoZWs0M280eWNvaHVzc2EifQ.v3kcNa8Zdkwgn6Joq7N5-g'
			}).addTo(mymap2);

			/******************************************************/
			var adr=[];
			var name=[];

			for (var i=0; i < data.prices.length; i++)
			{
				adr[i]=data.prices[i].shopAddress;
				name[i]=data.prices[i].shopName;
				console.log( "hi" + adr[i]);
			}

			requests=new Array(adr.length);
			markers=new Array(adr.length);
			markerString=new Array(adr.length);
			var items=[];


			for (let i=0; i < adr.length; i++)
			{

			var url='http://dev.virtualearth.net/REST/v1/Locations/' + adr[i] + '?o=json&key=AvjCC7Itxwd50bq3hMyIGJYreE4RZ74KhbG0aTb8AfwFBG6rf3RRxEzp9tfeQTtD';
			requests[i]=new XMLHttpRequest();
			requests[i].open('GET',url, true);

			requests[i].onreadystatechange = function()
			{
				if(requests[i].readyState == 4)
				{
					var d = JSON.parse(requests[i].response);

					if(requests[i].status >= 200 && requests[i].status < 400)
					{
						var preformater=document.createTextNode(' ' + i + ')');
						var postformater=document.createTextNode('_');
					//	var k=document.createTextNode(d.rersourceSets[0].resources[0].name);

						//console.log( "yo" + d.rersourceSets[0].resources[0].name);
						var k =document.createTextNode(d.resourceSets[0].resources[0].name);
						var t = document.createTextNode(d.resourceSets[0].resources[0].point.coordinates[0] + ", " + d.resourceSets[0].resources[0].point.coordinates[1]);
						var lg=d.resourceSets[0].resources[0].point.coordinates[0];
						var late=d.resourceSets[0].resources[0].point.coordinates[1];
						//document.getElementById("try").appendChild(preformater);
						//document.getElementById("try").appendChild(t);
						//	document.getElementById("try").appendChild(postformater);
						//document.getElementById("try").appendChild(k);
						console.log(d.resourceSets[0].resources[0].point.coordinates[0]);
						console.log(d.resourceSets[0].resources[0].point.coordinates[1]);

					/**************map markers***********/


						// Map marker


						markers[i] =new  L.marker(L.latLng(lg,late)).addTo(mymap2);
						markerString[i] = "<b>" + name[i] + "</b><br />" + adr[i];
						markers[i].bindPopup(markerString[i],{closeOnClick: false, autoClose: false}).openPopup();

						//if(mymap2!=undefined) mymap2.remove();

					/******************************/
					}
					else
					{
						console.log("error");

					}
				}
			}

			requests[i].send();

			if(geoDist != undefined && geoLat != undefined && geoLng != undefined)
				L.circle([geoLat, geoLng], (geoDist * 1000) + 1500).addTo(mymap2);
		}
		}
		
		if(data.prices.length == 0)
		{
			var empty = document.createTextNode("Το προϊόν αυτό δεν πωλείται σε κάποιο φαρμακείο αυτή τη στιγμή.");
			document.getElementById("product-view-prices").appendChild(empty);
		}

  		/*localStorage.removeItem("geoLat");
		localStorage.removeItem("geoLng");
		localStorage.removeItem("geoDist");*/
	} 
	else 
	{
		error();
	}
}

request2.send();
