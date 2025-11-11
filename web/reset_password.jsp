<%-- /reset_password.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activePage" value="" scope="request" />
<jsp:include page="top_menu.jsp" />

<main class="content">
    <div class="form-container" style="max-width: 450px;">
        <h2 class="title1">Đặt Lại Mật Khẩu</h2>

        <%-- Display error messages (invalid token from GET or form errors from POST) --%>
        <c:if test="${not empty requestScope.error}">
            <p class="form-message error">${requestScope.error}</p>
            <%-- Show link to request again only if token was invalid initially --%>
            <c:if test="${empty requestScope.token}">
                 <p class="form-link"><a href="forgot-password">Yêu cầu liên kết mới</a></p>
            </c:if>
        </c:if>

        <%-- Show form only if token is valid (present in request scope) --%>
        <c:if test="${not empty requestScope.token}">
             <p class="form-description">
                 Vui lòng nhập mật khẩu mới (ít nhất 6 ký tự).
             </p>
            <form action="reset-password" method="post">
                <%-- Hidden input to send the token back --%>
                <input type="hidden" name="token" value="${requestScope.token}">

                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới (*):</label>
                    <input type="password" id="newPassword" name="newPassword" required minlength="6">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu mới (*):</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">
                </div>

                <button type="submit" class="submit-btn">Cập Nhật Mật Khẩu</button>
            </form>
             <p class="form-link">
                 <a href="login.jsp">Quay lại Đăng nhập</a>
             </p>
        </c:if>

    </div>
</main>

<jsp:include page="footer.jsp" />

<%-- Basic CSS (Use styles from forgot.jsp or your main CSS) --%>
<style>
    .form-message { text-align: center; padding: 12px; border-radius: 4px; margin-bottom: 15px; font-weight: 500;}
    .form-message.error { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; }
    .form-description { text-align: center; color: #555; margin-bottom: 20px; font-size: 0.95em; }
    .form-link { margin-top: 20px; text-align: center; font-size: 0.9em; }
    .form-link a { color: #0d6efd; text-decoration: none; }
    .form-link a:hover { text-decoration: underline; }
</style>