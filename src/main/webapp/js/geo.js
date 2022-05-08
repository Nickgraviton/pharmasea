// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var req = new XMLHttpRequest();
var a = queryDict.address;
req.open('GET', 'http://dev.virtualearth.net/REST/v1/Locations/' + a + '?o=json&key=AvjCC7Itxwd50bq3hMyIGJYreE4RZ74KhbG0aTb8AfwFBG6rf3RRxEzp9tfeQTtD', true);

req.onreadystatechange = function()
{
	if(req.readyState == 4)
	{
		var d = JSON.parse(this.response);

		if(req.status >= 200 && req.status < 400)
		{
			var t = document.createTextNode(d.resourceSets[0].resources[0].point.coordinates[0] + ", " + d.resourceSets[0].resources[0].point.coordinates[1]);
			document.getElementById("result").appendChild(t);
		}
		else
		{
			var error = document.createTextNode(":(");
			document.getElementById("result").appendChild(error);
		}						
	}
}

req.send();