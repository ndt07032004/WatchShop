<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Bắt buộc đăng nhập --%>
<c:if test="${empty sessionScope.account}"><c:redirect url="login.jsp"/></c:if>

<%-- Đặt activePage TRƯỚC khi include top_menu --%>
<c:set var="activePage" value="profile" scope="request" />
<jsp:include page="top_menu.jsp" />


<main class="content"> <%-- Đặt nội dung chính trong thẻ main.content --%>
    <div class="form-container" style="max-width: 800px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <h2 class="title1">Hồ Sơ Của Tôi</h2>

        <%-- Hiển thị thông báo --%>
        <c:if test="${not empty requestScope.success}"><p style="color:green; text-align: center; font-weight: bold; background-color: #e6ffed; padding: 10px; border-radius: 4px; border: 1px solid #b7ebc9; margin-bottom: 15px;">${requestScope.success}</p></c:if>
        <c:if test="${not empty requestScope.error}"><p style="color:red; text-align: center; font-weight: bold; background-color: #ffeeee; padding: 10px; border-radius: 4px; border: 1px solid #fcc; margin-bottom: 15px;">${requestScope.error}</p></c:if>

            <div style="display: flex; flex-wrap: wrap; justify-content: space-between; gap: 30px; margin-top: 20px;">

                <form action="edit-profile" method="post" style="flex: 1 1 45%; min-width: 300px;">
                    <input type="hidden" name="action" value="updateUserProfile">
                    <h3>Thông tin cá nhân</h3>
                    <div class="form-group">
                        <label>Tên đăng nhập:</label>
                        <input type="text" value="${sessionScope.account.username}" disabled style="background-color: #eee;">
                </div>
                <div class="form-group">
                    <label>Họ và Tên:</label>
                    <input type="text" name="fullname" value="<c:out value="${sessionScope.account.fullname}"/>" required>
                </div>
                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" value="<c:out value="${sessionScope.account.email}"/>" required>
                </div>
                <div class="form-group">
                    <label>Số điện thoại:</label>
                    <input type="tel" name="phone" value="<c:out value="${sessionScope.account.phone}"/>" required>
                </div>
                <div class="form-group">
                    <label>Địa chỉ:</label>
                    <textarea name="address" rows="3" required style="resize: vertical;"><c:out value="${sessionScope.account.address}"/></textarea>
                </div>
                <div class="form-group">
                    <input type="submit" value="Cập nhật thông tin" class="submit-btn">
                </div>
            </form>

            <%-- Phần form đổi mật khẩu trong edit_profile.jsp --%>
            <form action="edit-profile" method="post" style="flex: 1 1 45%; min-width: 300px;">
                <%-- ⭐ ĐÚNG: Gửi action=updatePassword --%>
                <input type="hidden" name="action" value="updatePassword">
                <h3>Đổi mật khẩu</h3>
                <div class="form-group">
                    <label>Mật khẩu cũ:</label>
                    <%-- ⭐ ĐÚNG: name="oldPassword" --%>
                    <input type="password" name="oldPassword" id="oldPassword" required>
                    <span class="toggle-eye" onclick="togglePassword('oldPassword', this)">👁️</span>
                </div>
                <div class="form-group">
                    <label>Mật khẩu mới:</label>
                    <%-- ⭐ ĐÚNG: name="newPassword" --%>
                    <input type="password" name="newPassword" id="newPassword" required pattern=".{6,}" title="Mật khẩu phải có ít nhất 6 ký tự">
                    <span class="toggle-eye" onclick="togglePassword('newPassword', this)">👁️</span>
                </div>
                <div class="form-group">
                    <label>Xác nhận mật khẩu mới:</label>
                    <%-- ⭐ ĐÚNG: name="confirmPassword" --%>
                    <input type="password" name="confirmPassword" id="confirmPassword" required>
                     <span class="toggle-eye" onclick="togglePassword('confirmPassword', this)">👁️</span>
                </div>
                <div class="form-group">
                    <input type="submit" value="Đổi mật khẩu" class="submit-btn" style="background-color: #f39c12;">
                </div>
            </form>
        </div>

        <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;">
            <a href="order-history" style="text-decoration: none; color: #007bff;">Xem lịch sử đơn hàng &rarr;</a>
        </div>
    </div>
</main>
<%-- Phần nội dung chính kết thúc --%>

<jsp:include page="footer.jsp" />
<script>
    function togglePassword(fieldId, eyeElement) {
        var field = document.getElementById(fieldId);
        if (field.type === "password") {
            field.type = "text";
            eyeElement.textContent = "🙈"; // Changed to a "hide" icon
        } else {
            field.type = "password";
            eyeElement.textContent = "👁️"; // Changed to a "show" icon
        }
    }
</script>