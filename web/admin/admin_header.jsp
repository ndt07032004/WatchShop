<%-- 
    Document   : admin_header
    Created on : Oct 17, 2025, 6:46:30 PM
    Author     : THAI
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Kiểm tra Admin Role --%>
<c:if test="${sessionScope.account.role != 1}">
    <c:redirect url="../login.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - ${pageTitle}</title>
    <%-- Thay đổi link CSS sang admin.css --%>
    <link href="<c:url value='../css/admincss.css' />" rel="stylesheet" type="text/css"/>
</head>
<body>
    <div class="admin-container">
        <header class="admin-header">
            <h1 style="margin: 0;">Admin Dashboard</h1>
            <nav class="admin-menu">
                <ul>
                    <%-- Dùng 'activePage' để đánh dấu menu đang chọn --%>
                    <li><a href="<c:url value='/admin/dashboard.jsp'/>" class="${activePage eq 'dashboard' ? 'active' : ''}">Dashboard</a></li>
                    <li><a href="<c:url value='/admin/manage-products'/>" class="${activePage eq 'products' ? 'active' : ''}">Quản lý Sản phẩm</a></li>
                    <li><a href="<c:url value='/admin/statistics.jsp'/>" class="${activePage eq 'statistics' ? 'active' : ''}">Thống kê</a></li>
                    <li><a href="<c:url value='/logout'/>">Đăng xuất (${sessionScope.account.fullname})</a></li>
                </ul>
            </nav>
        </header>
        <%-- Bắt đầu nội dung chính của trang Admin --%>