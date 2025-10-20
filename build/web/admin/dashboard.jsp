<%-- 
    Document   : dashboard
    Created on : Oct 17, 2025, 4:20:04 PM
    Author     : THAI
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% request.setAttribute("pageTitle", "Tổng quan"); %>
<% request.setAttribute("activePage", "dashboard"); %>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Xin chào, ${sessionScope.account.fullname}!</h2>
    <p>Đây là trang tổng quan quản trị. Chọn một mục từ menu để bắt đầu.</p>
    
    <%-- Thêm nội dung dashboard ở đây --%>
    
    <p style="margin-top: 30px;">Các đường dẫn nhanh:</p>
    <ul>
        <li><a href="<c:url value='/admin/manage-products'/>">Quản lý Sản phẩm</a></li>
        <li><a href="<c:url value='/admin/statistics.jsp'/>">Xem Thống kê Doanh thu</a></li>
    </ul>
    
</main>

<jsp:include page="admin_footer.jsp" />
