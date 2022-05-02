var request = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/shops/';
var token = document.cookie.replace('token=', '').split(";")[0];
request.open('POST', url, true);
request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
request.setRequestHeader("X-OBSERVATORY-AUTH", token);
function error()
{
 	var errorString = "Υπήρξε ένα σφάλμα κατά την δημιουργία φαρμακείου. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.getElementById("res").appendChild(errorText);

	console.log('error');
}

request.onreadystatechange = function () 
{
	console.log(request.status);

	if(request.readyState == 4) // replacement for request.onload
	{
		if (request.status != 200) 
		{	
			error();
		}
		else
		{
			var successText = document.createTextNode("Επιτυχής δημιουργία φαρμακείου!");
			document.getElementById("res").appendChild(successText);		
		}
	}
}

var tags = localStorage.getItem("tags").split(",");

function getTags()
{
	var t = null;

	for(var i=0; i < tags.length; i++)
	{
		if(i == 0)
			t = "&tags=" + tags[i];
		else
			t = t + "&tags=" + tags[i];
	}

	return t;
}

var parameters = "name=" + localStorage.getItem("name") + "&address=" + localStorage.getItem("address") + "&lat=" + parseFloat(localStorage.getItem("lat")) + "&lng=" + parseFloat(localStorage.getItem("lng")) + getTags() + "&withdrawn=false";

console.log(parameters);
request.send(parameters);