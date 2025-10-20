<%-- 
    Document   : register
    Created on : Oct 17, 2025, 4:23:32 PM
    Author     : THAI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activePage" value="register" scope="request"/>
<jsp:include page="top_menu.jsp" />


<main class="content">
    <div class="form-container">
        <h2 class="title">Đăng Ký Tài Khoản Mới</h2>
        <p style="color:red; text-align: center;">${requestScope.error}</p>
        <form action="register" method="post">
            <div class="form-group"><label>Tên đăng nhập (*):</label><input type="text" name="username" required></div>
            <div class="form-group"><label>Mật khẩu (*):</label><input type="password" name="password" required></div>
            <div class="form-group"><label>Nhập lại mật khẩu (*):</label><input type="password" name="repassword" required></div>
            <div class="form-group"><label>Họ và Tên (*):</label><input type="text" name="fullname" required></div>
            <div class="form-group"><label>Email (*):</label><input type="email" name="email" required></div>
            <div class="form-group"><label>Số điện thoại:</label><input type="text" name="phone"></div>
            <div class="form-group"><label>Địa chỉ:</label><input type="text" name="address"></div>
            <button type="submit" class="submit-btn">Đăng Ký</button>
            <p class="form-link">Đã có tài khoản? <a href="<c:url value='/login.jsp'/>">Đăng nhập</a></p>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />