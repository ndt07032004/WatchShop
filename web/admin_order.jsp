<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Đơn hàng</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <h2>Quản lý đơn hàng</h2>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Order ID</th>
                <th>Khách hàng</th>
                <th>Tổng tiền</th>
                <th>Ngày đặt</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="o" items="${orders}">
                <tr>
                    <td>${o.id}</td>
                    <td>${o.customerName}</td>
                    <td>${o.totalMoney}</td>
                    <td>${o.orderDate}</td>
                    <td>${o.status}</td>
                    <td>
                        <form method="post" action="<c:url value='/admin/changeOrderStatus'/>" style="display:inline">
                            <input type="hidden" name="orderId" value="${o.id}"/>
                            <input type="hidden" name="action" value="changeStatus"/>
                            <select name="status" class="form-select form-select-sm d-inline w-auto">
                                <option value="Chưa xử lý">Chưa xử lý</option>
                                <option value="Đang giao">Đang giao</option>
                                <option value="Đã giao">Đã giao</option>
                                <option value="Đã huỷ">Đã huỷ</option>
                            </select>
                            <button class="btn btn-sm btn-primary">Cập nhật</button>
                        </form>
                        <a class="btn btn-sm btn-secondary" href="<c:url value='/order/detail?orderId=${o.id}'/>">Chi tiết</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>