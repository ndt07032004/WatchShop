<%-- manage-products.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% request.setAttribute("pageTitle", "Quản lý Sản phẩm"); %>
<% request.setAttribute("activePage", "products"); %>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Danh sách sản phẩm</h2>
    <%-- Sửa link đến form thêm mới (chỉ cần tên file vì cùng thư mục /admin/) --%>
    <a href="add-edit-product.jsp" class="submit-btn" style="display:inline-block; width: auto; margin-bottom: 20px;">Thêm sản phẩm mới</a>
    <a href="<c:url value='/admin/export-products'/>" class="submit-btn" style="display:inline-block; width: auto; margin-bottom: 20px;"> Xuất ra Excel</a>
    <%-- Hiển thị thông báo thành công/lỗi --%>
    <c:if test="${not empty sessionScope.adminProductSuccess}"> <p class="admin-message success">${sessionScope.adminProductSuccess}</p> <c:remove var="adminProductSuccess" scope="session"/> </c:if>
    <c:if test="${not empty sessionScope.adminProductError}"> <p class="admin-message error">${sessionScope.adminProductError}</p> <c:remove var="adminProductError" scope="session"/> </c:if>

    <%-- Bảng hiển thị sản phẩm --%>
    <table class="admin-table data-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Ảnh</th>
                <th>Tên sản phẩm</th>
                <th>Giá bán</th>
                <th>Giá nhập</th>
                <th>Tồn kho</th>
                <th>Danh mục </th> <%-- Tạm hiển thị ID --%>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty productList}">
                 <tr><td colspan="8" style="text-align: center; padding: 20px;">Không có sản phẩm nào.</td></tr>
            </c:if>
            <c:forEach items="${productList}" var="p">
                <tr>
                    <td>${p.id}</td>
                    <td>
                        <c:if test="${not empty p.image}">
                            <img src="<c:url value='/${p.image}'/>" alt="${p.name}" style="width: 50px; height: 50px; object-fit: cover; border: 1px solid #eee;">
                        </c:if>
                         <c:if test="${empty p.image}"> <span style="color: grey; font-size: 0.8em;">(N/A)</span> </c:if>
                    </td>
                    <td><c:out value="${p.name}"/></td>
                    <td class="price"><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></td>
                    <td class="price"><fmt:formatNumber value="${p.costPrice}" type="currency" currencyCode="VND"/></td> <%-- Hiển thị giá nhập --%>
                    <td><c:out value="${p.stock}"/></td>
                    <td><c:out value="${p.categoryName}"/></td> <%-- Hiển thị ID (Cần JOIN để có tên) --%>
                    <td class="actions">
                        <%-- Link Sửa (trỏ đến Servlet với action=edit) --%>
                        <a href="<c:url value='/admin/manage-products?action=edit&id=${p.id}'/>" class="action-btn edit-btn">Sửa</a>
                        <%-- Link Xóa (trỏ đến Servlet với action=delete) --%>
                        <a href="<c:url value='/admin/manage-products?action=delete&id=${p.id}'/>" class="action-btn delete-btn" onclick="return confirm('Bạn chắc chắn muốn xóa sản phẩm: ${p.name}?');">Xóa</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
     </table>
</main>

<jsp:include page="admin_footer.jsp" />