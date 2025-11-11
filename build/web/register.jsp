<%-- 
    Document   : register
    Created on : Oct 17, 2025, 4:23:32 PM
    Author     : THAI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activePage" value="register" scope="request"/>
<jsp:include page="top_menu.jsp" />


<main class="content">
    <div class="form-container">
        <h2 class="title1">Đăng Ký Tài Khoản Mới</h2>
        <p style="color:red; text-align: center;">${requestScope.error}</p>
        <form action="<c:url value='/register'/>" method="post">
            
            <div class="form-group">
                <label>Tên đăng nhập (*):</label>
                <input type="text" name="username" placeholder="Ví dụ: nguyenvanA" required>
            </div>
            
            <div class="form-group">
                <label>Mật khẩu (*):</label>
                <input type="password" name="password" id="password" placeholder="Nhập mật khẩu (ít nhất 6 ký tự)" required>
                <span class="toggle-eye" onclick="togglePassword('password', this)">👁️</span>
            </div>
            
            <div class="form-group">
                <label>Nhập lại mật khẩu (*):</label>
                <input type="password" name="repassword" id="repassword" placeholder="Nhập lại mật khẩu của bạn" required>
                <span class="toggle-eye" onclick="togglePassword('repassword', this)">👁️</span>
            </div>
            
            <div class="form-group">
                <label>Họ và Tên (*):</label>
                <input type="text" name="fullname" placeholder="Ví dụ: Nguyễn Văn A" required>
            </div>
            
            <div class="form-group">
                <label>Email (*):</label>
                <input type="email" name="email" placeholder="Ví dụ: email@example.com" required>
            </div>
            
            <div class="form-group">
                <label>Số điện thoại:</label>
                <input type="text" name="phone" placeholder="Ví dụ: 0987654321">
            </div>
            
            <div class="form-group">
                <label>Địa chỉ:</label>
                <input type="text" name="address" placeholder="Ví dụ: Số 1, đường ABC, Hà Nội">
            </div>
            
            <button type="submit" class="submit-btn">Đăng Ký</button>
            <p class="form-link" style="text-align: center; margin-top: 15px;">Đã có tài khoản? <a href="<c:url value='/login.jsp'/>">Đăng nhập</a></p>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />

<script>
    function togglePassword(fieldId, eyeElement) {
        var field = document.getElementById(fieldId);
        if (field.type === "password") {
            field.type = "text";
            eyeElement.textContent = "🙈";
        } else {
            field.type = "password";
            eyeElement.textContent = "👁️";
        }
    }
</script>