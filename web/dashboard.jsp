<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Admin</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <h2>Dashboard</h2>
    <div class="row">
        <div class="col-md-4">
            <div class="card p-3">
                <h5>Doanh thu năm</h5>
                <h3>${yearlyRevenue}</h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3">
                <h5>Đơn chờ xử lý</h5>
                <h3>${pendingOrders}</h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3">
                <h5>Tổng đơn</h5>
                <h3>${totalOrders}</h3>
            </div>
        </div>
    </div>
</div>
</body>
</html>