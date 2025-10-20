<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% request.setAttribute("pageTitle", "Thống kê"); %>
<% request.setAttribute("activePage", "statistics"); %>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Thống Kê Bán Hàng</h2>
    <p>Chức năng này sẽ được phát triển để hiển thị doanh thu và hàng tồn kho.</p>
    
    <%-- Thêm nội dung thống kê ở đây --%>
    
</main>

<jsp:include page="admin_footer.jsp" />