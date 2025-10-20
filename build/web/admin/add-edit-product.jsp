<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Giữ nguyên kiểm tra role để đảm bảo an toàn --%>
<c:if test="${sessionScope.account.role != 1}"><c:redirect url="../login.jsp"/></c:if>

<!DOCTYPE html>
<html>
<head>
    <title>${empty product ? 'Thêm Sản Phẩm' : 'Sửa Sản Phẩm'}</title>
    <%-- Đổi link CSS --%>
    <link href="<c:url value='../css/admincss.css' />" rel="stylesheet" type="text/css"/> 
</head>
<body>
    <div class="form-container" style="margin-top: 20px;">
        <h2 class="title">${empty product ? 'Thêm Sản Phẩm Mới' : 'Chỉnh Sửa Sản Phẩm'}</h2>
        <form action="<c:url value='/admin/manage-products'/>" method="post">
            <%-- Nếu là edit thì gửi kèm id --%>
            <c:if test="${not empty product}">
                <input type="hidden" name="id" value="${product.id}">
            </c:if>
            
            <div class="form-group"><label>Tên sản phẩm:</label><input type="text" name="name" value="${product.name}" required></div>
            <div class="form-group"><label>Mô tả:</label><textarea name="description" rows="5">${product.description}</textarea></div>
            <div class="form-group"><label>Giá (VNĐ):</label><input type="number" name="price" value="${product.price}" required min="0"></div>
            <div class="form-group"><label>Số lượng tồn kho:</label><input type="number" name="stock" value="${product.stock}" required min="0"></div>
            <div class="form-group"><label>Link hình ảnh:</label><input type="text" name="image" value="${product.image}"></div>
            <div class="form-group">
                <label>Danh mục:</label>
                <select name="categoryId">
                    <option value="1" ${product.categoryId == 1 ? 'selected' : ''}>Đồng Hồ Nam</option>
                    <option value="4" ${product.categoryId == 4 ? 'selected' : ''}>Đồng Hồ Nữ</option>
                    <option value="6" ${product.categoryId == 6 ? 'selected' : ''}>Đồng Hồ Cặp</option>
                    <option value="5" ${product.categoryId == 5 ? 'selected' : ''}>Đồng Hồ Thông Minh</option>
                    <%-- Nếu có danh sách categoryList được gửi sang, bạn sẽ dùng c:forEach ở đây --%>
                </select>
            </div>
            <button type="submit" class="submit-btn">Lưu Lại</button>
            <p style="margin-top: 10px;"><a href="<c:url value='/admin/manage-products'/>">Quay lại danh sách</a></p>
        </form>
    </div>
</body>
</html>