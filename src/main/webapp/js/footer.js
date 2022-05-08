class Footer extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.innerHTML = `
            <nav class="footer fixed-bottom text-center text-white">
                <div class="container">
                    <section>
                        <div class="row text-center d-flex justify-content-center pt-3">
                            <div class="col-md-2">
                                <a href="#" class="text-white">Top</a>
                            </div>
                            <div class="col-md-2">
                                <a href="faq.html" class="text-white">FAQ</a>
                            </div>
                            <div class="col-md-2">
                                <a href="privacypolicy.html" class="text-white">Policy</a>
                            </div>

                            <div class="col-md-2">
                                <a href="cookies.html" class="text-white">Cookies</a>
                            </div>

                            <div class="col-md-2">
                                <a href="contact.html" class="text-white">Contact</a>
                            </div>
                        </div>
                    </section>

                    <hr class="my-2" />

                    <section class="text-center mb-2">
                        <a href="" class="text-white me-4">
                            <i class="fab fa-facebook-f"></i>
                        </a>
                        <a href="" class="text-white me-4">
                            <i class="fab fa-twitter"></i>
                        </a>
                        <a href="" class="text-white me-4">
                            <i class="fab fa-instagram"></i>
                        </a>
                        <a href="" class="text-white me-4">
                            <i class="fab fa-github"></i>
                        </a>
                        <a href="" class="text-white me-4">
                            <i class="fab fa-gitlab"></i>
                    </a>
                    </section>
                </div>

                <div class="copyright text-center p-3">
                    © 2022 Copyright | PharmaSea
                </div>
            </nav>
      `;
    }
}

customElements.define('footer-component', Footer);
