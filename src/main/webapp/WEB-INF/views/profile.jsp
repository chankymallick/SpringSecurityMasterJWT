<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" />
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-2 bg-dark text-white p-3">
            <h4>Dashboard</h4>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link text-white" href="home">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="profile">Profile</a>
                </li>
                <li class="nav-item">
                    <form action="logout" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button class="btn btn-primary btn-block" type="submit">Logout</button>
                    </form>
                </li>
            </ul>
        </div>
        <!-- Main content -->
        <div class="col-md-10 p-4">
            <h2>User Profile</h2>
            <c:if test="${not empty name}">
                <p><strong>Name:</strong> ${name}</p>
            </c:if>
            <c:if test="${not empty email}">
                <p><strong>Email:</strong> ${email}</p>
            </c:if>
            <c:if test="${not empty picture}">
                <p><strong>Picture:</strong><br><img src="${picture}" alt="Profile Picture" style="max-width:120px;"></p>
            </c:if>
            <pre>${profile}</pre>
            <a href="home" class="btn btn-secondary mt-3">Back to Home</a>
        </div>
    </div>
</div>
</body>
</html>
