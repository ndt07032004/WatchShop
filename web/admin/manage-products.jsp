<%-- manage-products.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% request.setAttribute("pageTitle", "Quản lý Sản phẩm"); %>
<% request.setAttribute("activePage", "products"); %>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Danh sách sản phẩm</h2>
    <a href="<c:url value='/admin/add-edit-product.jsp'/>" class="submit-btn" style="display:inline-block; width: auto; margin-bottom: 20px;">Thêm sản phẩm mới</a>
    
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên sản phẩm</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${productList}" var="p">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.name}</td>
                    <td><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></td>
                    <td>${p.stock}</td>
                    <td>
                        <a href="manage-products?action=edit&id=${p.id}" style="color: blue;">Sửa</a> |
                        <a href="manage-products?action=delete&id=${p.id}" onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm: ${p.name}?');" style="color: red;">Xóa</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</main>

<jsp:include page="admin_footer.jsp" />