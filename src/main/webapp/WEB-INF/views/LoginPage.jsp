<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring MVC Security with DB - LDAP - OKTA - OAuth2 Authentication</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <style>
        body {
            background-color: #f4f7fc;
        }
        .login-card {
            max-width: 400px;
            margin-top: 100px;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .login-card h2 {
            font-weight: 600;
        }
        .login-card .btn {
            font-size: 16px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
            <div class="card login-card">
                <div class="card-body">
                    <h2 class="text-center mb-4">Spring MVC Security with DB - LDAP - OKTA - OAuth2 Authentication</h2>

                    <!-- Display error message if error=true is present in the URL -->
                    <div class="alert alert-danger" style="display: none;" id="error-message">
                       Login Error
                    </div>

                    <!-- Login Form -->
                    <form action="/questdemo_war_exploded/login" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <!-- Username Field -->
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" name="username" placeholder="Enter your username" required>
                        </div>
                        <!-- Password Field -->
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" placeholder="Enter your password" required>
                        </div>
                        <!-- Login Button -->
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Login</button>
                        </div>
                    </form>

                    <div class="text-center my-3">
                        <span>OR</span>
                    </div>

                    <!-- Okta Login Button -->
                    <div class="d-grid gap-2">
                        <a href="/questdemo_war_exploded/oauth2/authorization/okta" class="btn btn-outline-dark">
                            <img src="https://1000logos.net/wp-content/uploads/2023/01/Okta-Logo-2015-1536x864.png" alt="Okta" width="20" height="20" class="me-2">
                            Login with Okta
                        </a>
                    </div>

                    <!-- Google Login Button -->
                    <div class="d-grid gap-2 mt-2">
                        <a href="/questdemo_war_exploded/oauth2/authorization/google" class="btn btn-outline-danger">
                            <img src="https://developers.google.com/identity/images/g-logo.png" alt="Google" width="20" height="20" class="me-2">
                            Login with Google
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS and Popper.js (CDN) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>

<!-- Custom JS to handle the error message -->
<script>
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('error')) {
            document.getElementById('error-message').style.display = 'block';
        }
    };
</script>

</body>
</html>
