var request = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/prices/';
var token = document.cookie.replace('token=', '').split(";")[0];
request.open('POST', url, true);
request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
request.setRequestHeader("X-OBSERVATORY-AUTH", token);
function error()
{
 	var errorString = "Υπήρξε ένα σφάλμα κατά την προσθήκη τιμής. Προσπαθήστε αργότερα.";
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
			var successText = document.createTextNode("Επιτυχής προσθήκη τιμής");
			document.getElementById("res").appendChild(successText);		
		}
	}
}

var parameters = "price=" + localStorage.getItem("price") + "&dateFrom=" + localStorage.getItem("dateFrom") + "&dateTo=" + localStorage.getItem("dateTo") + "&productId=" + localStorage.getItem("productId")+ "&shopId=" + localStorage.getItem("pharmacy");

console.log(parameters);
request.send(parameters);