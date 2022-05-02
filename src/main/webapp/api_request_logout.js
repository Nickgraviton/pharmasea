var request = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/logout/';
var token = document.cookie.replace('token=', '').split(";")[0];
request.open('POST', url, true);
request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
request.setRequestHeader("X-OBSERVATORY-AUTH", token);

function error()
{
 	var errorString = "Υπήρξε ένα σφάλμα κατά την έξοδο. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.body.appendChild(errorText);

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
			document.cookie = "token=;username="; // delete cookie
			localStorage.clear();
			window.location.replace("index.html");
		}
	}
}

request.send();