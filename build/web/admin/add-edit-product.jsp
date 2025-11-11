<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Giữ nguyên kiểm tra role --%>
<c:if test="${sessionScope.account.role != 1}"><c:redirect url="../login.jsp"/></c:if>

<!DOCTYPE html>
<html>
<head>
    <title>${empty product ? 'Thêm Sản Phẩm' : 'Sửa Sản Phẩm'}</title>
    <link href="<c:url value='../css/admincss.css' />" rel="stylesheet" type="text/css"/>
    <style>
        /* CSS để hiển thị ảnh preview và ảnh cũ */
        .image-preview-container { margin-top: 10px; text-align: left; }
        .image-preview { max-width: 150px; max-height: 150px; border: 1px solid #ddd; margin-top: 5px; display: none; }
        .image-preview-container img.show { display: block; }
        .current-image { max-width: 100px; max-height: 100px; border: 1px solid #ccc; margin-left: 10px; vertical-align: middle; }
    </style>
</head>
<body>
    <div class="form-container" style="margin-top: 20px;">
        <h2 class="title">${empty product ? 'Thêm Sản Phẩm Mới' : 'Chỉnh Sửa Sản Phẩm'}</h2>

        <%-- Form với enctype --%>
        <form action="<c:url value='/admin/manage-products'/>" method="post" enctype="multipart/form-data">

            <%-- ID và Ảnh cũ (nếu sửa) --%>
            <c:if test="${not empty product}">
                <input type="hidden" name="id" value="${product.id}">
                <input type="hidden" name="currentImage" value="${product.image}">
            </c:if>

            <div class="form-group"><label>Tên sản phẩm:</label><input type="text" name="name" value="${product.name}" required></div>
            <div class="form-group"><label>Mô tả:</label><textarea name="description" rows="5">${product.description}</textarea></div>
            <div class="form-group"><label>Giá bán (VNĐ):</label><input type="number" step="1000" name="price" value="${product.price}" required min="0"></div>
            <div class="form-group"><label>Giá nhập (VNĐ):</label><input type="number" step="1000" name="costPrice" value="${product.costPrice}" required min="0"></div>
            <div class="form-group"><label>Số lượng tồn kho:</label><input type="number" name="stock" value="${product.stock}" required min="0"></div>

            <%-- Chọn file ảnh --%>
            <div class="form-group">
                <label for="imageFile">Chọn hình ảnh:</label>
                <c:if test="${not empty product.image}">
                    <img src="<c:url value='/${product.image}'/>" alt="Ảnh hiện tại" class="current-image">
                </c:if>
                <input type="file" id="imageFile" name="imageFile" accept="image/png, image/jpeg, image/gif, image/webp" onchange="previewImage(event)">
            </div>
            <%-- Xem trước ảnh --%>
            <div class="image-preview-container">
                 <img id="imagePreview" class="image-preview" src="#" alt="Xem trước ảnh"/>
            </div>

            <%-- Chọn Danh mục --%>
            <div class="form-group">
                <label>Danh mục:</label>
                <select name="categoryId" required>
                    <option value="">-- Chọn danh mục --</option>
                    <%-- Dùng categoryListForForm nếu Servlet gửi sang khi Edit --%>
                    <c:choose>
                        <c:when test="${not empty categoryListForForm}">
                            <c:forEach items="${categoryListForForm}" var="cat">
                                <%-- Bỏ qua "Tất cả" (ID=1 theo DB) --%>
                                <c:if test="${cat.id != 1}">
                                     <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise> <%-- Option tĩnh dự phòng (VALUE khớp ID trong bảng categories) --%>
                             <option value="4" ${product.categoryId == 4 ? 'selected' : ''}>Đồng Hồ Nam</option>
                             <option value="5" ${product.categoryId == 5 ? 'selected' : ''}>Đồng Hồ Nữ</option>
                             <option value="6" ${product.categoryId == 6? 'selected' : ''}>Đồng Hồ Cặp</option>
                         
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>

            <button type="submit" class="submit-btn">Lưu Lại</button>
            <p style="margin-top: 10px;"><a href="<c:url value='/admin/manage-products'/>">&larr; Quay lại Danh sách</a></p>
        </form>
    </div>

    <%-- Script xem trước ảnh --%>
    <script>
        function previewImage(event) {
            const reader = new FileReader();
            const imagePreview = document.getElementById('imagePreview');
            reader.onload = function(){
                if (reader.readyState === 2) {
                    imagePreview.src = reader.result;
                    imagePreview.classList.add('show');
                }
            }
            if(event.target.files[0]){ reader.readAsDataURL(event.target.files[0]); }
            else { imagePreview.src = '#'; imagePreview.classList.remove('show'); }
        }
    </script>
</body>
</html>