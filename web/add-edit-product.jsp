<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Thêm / Sửa Sản phẩm</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <h2>${param.id == null ? 'Thêm sản phẩm' : 'Sửa sản phẩm'}</h2>
    <form method="post" action="<c:url value='/admin/saveProduct'/>" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${product.id}"/>
        <div class="mb-3">
            <label class="form-label">Tên</label>
            <input class="form-control" name="name" value="${product.name}"/>
        </div>
        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea class="form-control" name="description">${product.description}</textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá</label>
            <input class="form-control" name="price" value="${product.price}"/>
        </div>
        <div class="mb-3">
            <label class="form-label">Kho</label>
            <input class="form-control" name="stock" value="${product.stock}"/>
        </div>
        <div class="mb-3">
            <label class="form-label">Ảnh</label>
            <input class="form-control" type="file" name="image"/>
            <c:if test="${not empty product.image}">
                <img src="${product.image}" style="height:80px;margin-top:10px"/>
            </c:if>
        </div>
        <button class="btn btn-success">Lưu</button>
        <a href="<c:url value='/admin/manage-products'/>" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>