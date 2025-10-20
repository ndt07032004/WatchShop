<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hồ sơ người dùng</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <h2>Xin chào, ${user.fullname}</h2>
    <ul class="nav nav-tabs" id="profileTab" role="tablist">
        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#info">Thông tin</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#orders">Đơn hàng</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#cart">Giỏ hàng</a></li>
    </ul>
    <div class="tab-content mt-3">
        <div id="info" class="tab-pane fade show active">
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Địa chỉ:</strong> ${user.address}</p>
            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/user/editProfile'/>">Chỉnh sửa</a>
        </div>
        <div id="orders" class="tab-pane fade">
            <c:forEach var="o" items="${userOrders}">
                <div class="card mb-2">
                    <div class="card-body">
                        <h5>Order #${o.id} - ${o.orderDate}</h5>
                        <p>Tổng: ${o.totalMoney}</p>
                        <a href="<c:url value='/order/detail?orderId=${o.id}'/>" class="btn btn-sm btn-secondary">Chi tiết</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div id="cart" class="tab-pane fade">
            <!-- hiển thị giỏ hàng hiện tại -->
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>