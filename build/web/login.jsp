<%-- 
    Document   : login
    Created on : Oct 17, 2025, 4:23:17 PM
    Author     : THAI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="top_menu.jsp" />


<main class="content">
    <div class="form-container">
        <h2 class="title">Đăng Nhập Tài Khoản</h2>
        <p style="color:red; text-align: center;">${requestScope.error}</p>
        <p style="color:green; text-align: center;">${requestScope.success}</p>
        <form action="login" method="post">
            <div class="form-group">
                <label for="username">Tên đăng nhập:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="submit-btn">Đăng Nhập</button>
            <p class="form-link">Chưa có tài khoản? <a href="<c:url value='/register.jsp'/>">Đăng ký ngay</a></p>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />
