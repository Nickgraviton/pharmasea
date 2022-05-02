// Get the GET parameters to figure out which results to return
// https://stackoverflow.com/questions/5448545/how-to-retrieve-get-parameters-from-javascript
var queryDict = {}
location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]});

var request = new XMLHttpRequest();
request.open('GET', 'https://localhost:8765/observatory/api/users/', true);

var token = document.cookie.replace('token=', '').split(";")[0];
request.setRequestHeader("X-OBSERVATORY-AUTH", token);
function error()
{
	var errorString = "Υπήρξε ένα σφάλμα κατά την προβολή χρηστών. Προσπαθήστε αργότερα.";
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
		console.log(data.users[0]);

		if (request.status >= 200 && request.status < 400)
		{
			// Create search results table
			var table = document.createElement("TABLE");
			table.setAttribute("align", "center");
			table.setAttribute("class", "table table-striped");
			document.getElementById("user-view-info").appendChild(table);

			var tableHeaderUsername = document.createElement("TH")
			var tableHeaderUsernameText = document.createTextNode("Username");
			tableHeaderUsername.appendChild(tableHeaderUsernameText);
			table.appendChild(tableHeaderUsername);

			var tableHeaderFName = document.createElement("TH")
			var tableHeaderFNameText = document.createTextNode("First Name");
			tableHeaderFName.appendChild(tableHeaderFNameText);
			table.appendChild(tableHeaderFName);

			var tableHeaderLName = document.createElement("TH")
			var tableHeaderLNameText = document.createTextNode("Last Name");
			tableHeaderLName.appendChild(tableHeaderLNameText);
			table.appendChild(tableHeaderLName);

			var tableHeaderEMail = document.createElement("TH")
			var tableHeaderEMailText = document.createTextNode("e-mail");
			tableHeaderEMail.appendChild(tableHeaderEMailText);
			table.appendChild(tableHeaderEMail);

			var tableHeaderDelete = document.createElement("TH")
			var tableHeaderDeleteText = document.createTextNode("Delete");
			tableHeaderDelete.appendChild(tableHeaderDeleteText);
			table.appendChild(tableHeaderDelete);

			// And print the results as rows

			for(var i=0; i < data.users.length; i++)
			{
				var ay = data.users[i];

				var tableResultUsernameText = document.createTextNode(ay.username);
				var tableResultFNameText = document.createTextNode(ay.fname);
				var tableResultLNameText = document.createTextNode(ay.lname);
				var tableResultEmailText = document.createTextNode(ay.email);
				var tableResultDeleteText = document.createTextNode("Delete");

				var tableResultRow = document.createElement("TR")
				table.appendChild(tableResultRow);

				var tableResultUsername = document.createElement("TD");
				tableResultUsername.appendChild(tableResultUsernameText);
				tableResultRow.appendChild(tableResultUsername);

				var tableResultFName = document.createElement("TD");
				tableResultFName.appendChild(tableResultFNameText);
				tableResultRow.appendChild(tableResultFName);

				var tableResultLName = document.createElement("TD");
				tableResultLName.appendChild(tableResultLNameText);
				tableResultRow.appendChild(tableResultLName);

				var tableAddressEmail = document.createElement("TD");
				tableAddressEmail.appendChild(tableResultEmailText);
				tableResultRow.appendChild(tableAddressEmail);

				var tableResultDelete = document.createElement("TD");
				var tableResultDeleteLink = document.createElement("A");
				
				tableResultDeleteLink.setAttribute("href", "cp_delete_user.html?id=" + ay.id);
				
				if(ay.username != "admin") tableResultDeleteLink.appendChild(tableResultDeleteText);
				tableResultDelete.appendChild(tableResultDeleteLink);
				tableResultRow.appendChild(tableResultDelete);				
			}			
		}
	}
}


request.send();

function deletePrompt()
{
	var q = confirm("Πρόκειται να σβήσετε αυτόν τον χρήστη. Είστε σίγουρος;");

	if(q) del();
}

function del()
{
	var req = new XMLHttpRequest();
	var url = 'https://localhost:8765/observatory/api/users/' + queryDict.id;
	var token = document.cookie.replace('token=', '').split(";")[0];
	req.open('DELETE', url, true);
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	req.setRequestHeader("X-OBSERVATORY-AUTH", token);

	req.onreadystatechange = function ()
	{
		if(req.readyState == 4)
		{
			var d = this.response;
			console.log(d);

			if(req.status == 200)
			{
				window.location.replace("index.html");	
			}
		}				
	}

	req.send();
}