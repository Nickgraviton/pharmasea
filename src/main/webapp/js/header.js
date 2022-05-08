class Header extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.innerHTML = `
            <nav class="navbar fixed-top navbar-expand-lg navbar-dark bg-dark">
                <a class="navbar-brand" href="index.html">
                    PharmaSea
                </a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav mr-auto">
                        <li class="nav-item active">
                            <a class="nav-link" href="pharma-info.html">
                                For pharmacists
                            </a>
                        </li>

                        <li class="nav-item active">
                            <a class="nav-link" href="vol-info.html">
                                For volunteers
                            </a>
                        </li>

                        <li class="nav-item dropdown active">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                For developers
                            </a>

                            <div class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDropdown">
                                <a class="dropdown-item" href="dev-api.html">View API</a>
                                <a class="dropdown-item" href="dev-info.html">Info</a>
                            </div>
                        </li>
                    </ul>
                    <ul id="account-buttons" class="nav navbar-nav navbar-right">
                        <li><a href="signup.html" class="nav-item nav-link"><i class="fa fa-user" aria-hidden="true"></i> Sign Up</a></li>
                        <li><a href="login.html" class="nav-item nav-link"><i class="fa fa-sign-in" aria-hidden="true"></i> Login</a></li>
                    </ul>
                </div>
            </nav>
      `;
    }
}

customElements.define("header-component", Header);

// If user has logged in replace login/signup buttons with control panel/logout button
let username = localStorage.getItem("username")
if (username) {
	const accountButtons = document.querySelector("#account-buttons");
	accountButtons.innerHTML = `
		<li><span class="nav-item nav-link active">Logged in as: ${username}</span></li>
		<li><a href="cp-home.html" class="nav-item nav-link active"> Control Panel</a></li>
		<li><a id="logout" href="#" class="nav-item nav-link active"> Logout</a></li>
		`;
}

// Use above logout button to perform the logout
const logoutButton = document.querySelector("#logout");

async function logout() {
    const token = localStorage.getItem("token");
	const url = "observatory/api/logout/";

	const response = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
            "X-OBSERVATORY-AUTH": token
		}
	});
	return response;
}

if (logoutButton) {
    logoutButton.addEventListener("click", e => {
        e.preventDefault();

        logout()
        .then(function(response) {
            if (response.ok) {
                localStorage.removeItem("username");
                localStorage.removeItem("token");
                window.location.replace("index.html");
            }
            return Promise.reject(response);
        }).catch(function(rejection) {
            alert("Error logging out");
        })
    })
}