var request = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/login/';
request.open('POST', url, true);
request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//request.setRequestHeader("X-Parse-Application-Id", "VnxVYV8ndyp6hE7FlPxBdXdhxTCmxX1111111");
//request.setRequestHeader("X-Parse-REST-API-Key","6QzJ0FRSPIhXbEziFFPs7JvH1l11111111");
function error()
{
 	var errorString = "Υπήρξε ένα σφάλμα κατά την είσοδο. Προσπαθήστε αργότερα.";
	var errorText = document.createTextNode(errorString);
	document.body.appendChild(errorText);

	console.log('error');
}

request.onreadystatechange = function () 
{
	console.log(request.status);

	if(request.readyState == 4) // replacement for request.onload
	{
		var data = JSON.parse(this.response);

		if(request.status >= 200 & request.status < 415)
		{
			if(data.token != null)
			{
				document.cookie = "token="; // delete cookie
				document.cookie = "token=" + data.token; // add cookie
				window.location.replace("cp_home.html");
			}
			else
			{
				window.location.replace("login.html?wrong");
				console.log(data.error);		
			}
		}

	}
}


var parameters = "username=" + localStorage.getItem("username") + "&password=" + localStorage.getItem("password");

console.log(parameters);
request.send(parameters);

// -----------------

var req = new XMLHttpRequest();
var url = 'https://localhost:8765/observatory/api/users/';
req.open('GET', url, true);

req.onreadystatechange = function()
{
	if(req.status == 200 && req.readyState == 4)
	{
		var data = JSON.parse(this.response);
		console.log(data);

		u = data.fname + " " + data.lname;
		loggedIn();
		console.log(u);
	}
}

req.send();
