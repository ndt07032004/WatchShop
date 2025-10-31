<%-- /forgot.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activePage" value="" scope="request" />
<jsp:include page="top_menu.jsp" />

<main class="content">
    <div class="form-container" style="max-width: 450px;">
        <h2 class="title">Quên Mật Khẩu</h2>

         <%-- Display feedback messages --%>
         <c:if test="${not empty requestScope.error}">
            <p class="form-message error">${requestScope.error}</p>
         </c:if>
         <c:if test="${not empty requestScope.success}">
            <p class="form-message success">${requestScope.success}</p>
         </c:if>

         <p class="form-description">
             Nhập địa chỉ email đã đăng ký. Nếu tài khoản tồn tại, chúng tôi sẽ gửi liên kết đặt lại mật khẩu.
         </p>

        <form action="forgot-password" method="post">
            <div class="form-group">
                <label for="email">Email (*):</label>
                <input type="email" id="email" name="email" required placeholder="Nhập email của bạn">
            </div>
            <button type="submit" class="submit-btn">Gửi Liên Kết</button>
        </form>
         <p class="form-link">
             <a href="login.jsp">Quay lại Đăng nhập</a>
         </p>
    </div>
</main>

<jsp:include page="footer.jsp" />

<%-- Basic CSS for messages (Add to your main CSS) --%>
<style>
    .form-message { text-align: center; padding: 12px; border-radius: 4px; margin-bottom: 15px; font-weight: 500;}
    .form-message.error { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; }
    .form-message.success { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; }
    .form-description { text-align: center; color: #555; margin-bottom: 20px; font-size: 0.95em; }
    .form-link { margin-top: 20px; text-align: center; font-size: 0.9em; }
    .form-link a { color: #0d6efd; text-decoration: none; }
    .form-link a:hover { text-decoration: underline; }
</style>