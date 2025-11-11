<%-- 
    Document   : login
    Created on : Oct 17, 2025, 4:23:17 PM
    Author     : THAI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activePage" value="login" scope="request"/>
<jsp:include page="top_menu.jsp" />

<main class="content">
    <div class="form-container">
        <h2 class="title1">Đăng Nhập Tài Khoản</h2>
        <p style="color:red; text-align: center;">${requestScope.error}</p>
        <p style="color:green; text-align: center;">${requestScope.success}</p>
        
        <form action="<c:url value='/login'/>" method="post">
            <div class="form-group">
                <label for="username">Tên đăng nhập:</label>
                <input type="text" id="username" name="username" placeholder="Nhập tên đăng nhập" required>
            </div>
            
            <div class="form-group">
                <label for="password">Mật khẩu:</label>
                <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>
                
                <span class="toggle-eye" onclick="togglePassword()">👁️</span>
            </div>
            <button type="submit" class="submit-btn">Đăng Nhập</button>
            
            <div class="form-link" style="text-align: center; margin-top: 10px;">
                <a href="<c:url value='/forgot.jsp'/>">Quên mật khẩu?</a>
            </div>
            
            <div class="signup" style="text-align: center">    
                <hr><br>
                <a href="<c:url value='/register.jsp'/>" class="signup-btn">Tạo tài khoản mới</a>
            </div>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />

<script>
    function togglePassword() {
        var field = document.getElementById("password");
        var eye = document.querySelector(".toggle-eye");
        if (field.type === "password") {
            field.type = "text";
            
        } else {
            field.type = "password";
           
        }
    }
</script>