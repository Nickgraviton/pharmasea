var u = null;
var login = false;

function loggedIn()
{
	login = true;
	var link = document.createElement("A");
	link.setAttribute('href', 'cp_home.html');
	var linkText = document.createTextNode("\u00A0Πίνακας |");
	link.appendChild(linkText);

	var link2 = document.createElement("A");
	link2.setAttribute('href', 'logout.html');
	var link2Text = document.createTextNode("\u00A0Έξοδος ");
	link2.appendChild(link2Text);

	var text = document.createTextNode("Σύνδεση ως:\u00A0");
	var username = document.createElement("B");
	var usernameText = document.createTextNode(localStorage.getItem("username"));
	username.appendChild(usernameText);

	document.getElementById("user").appendChild(text);
	document.getElementById("user").appendChild(username);
	document.getElementById("user").appendChild(link);
	document.getElementById("user").appendChild(link2);
}

var token = document.cookie.replace('token=', '').split(";")[0];
console.log(token);

if(token != "")
{
	loggedIn();
}
else
{
	var link = document.createElement("A");
	link.setAttribute('href', 'login.html');
	var linkText = document.createTextNode("(Είσοδος)");
	link.appendChild(linkText);

	var text = document.createTextNode("Δεν έχετε συνδεθεί.\u00A0")
	document.getElementById("user").appendChild(text);
	document.getElementById("user").appendChild(link);
}