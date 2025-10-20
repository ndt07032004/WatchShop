<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý sản phẩm</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between mb-3">
        <h2>Danh sách sản phẩm</h2>
        <a class="btn btn-primary" href="<c:url value='/admin/add-edit-product'/>">Thêm sản phẩm</a>
    </div>
    <table class="table table-hover">
        <thead>
            <tr><th>ID</th><th>Tên</th><th>Giá</th><th>Kho</th><th>Hình</th><th>Hành động</th></tr>
        </thead>
        <tbody>
            <c:forEach var="p" items="${productList}">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.name}</td>
                    <td>${p.price}</td>
                    <td>${p.stock}</td>
                    <td><img src="${p.image}" style="height:40px"/></td>
                    <td>
                        <a class="btn btn-sm btn-warning" href="<c:url value='/admin/add-edit-product?id=${p.id}'/>">Sửa</a>
                        <a class="btn btn-sm btn-danger" href="<c:url value='/admin/deleteProduct?id=${p.id}'/>" onclick="return confirm('Xóa?')">Xóa</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>