<%-- bank_payment.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%-- ⭐ THÊM TAGLIB ⭐ --%>
<%
    // Lấy tên người dùng từ session để chào hỏi
    String hoTen = (String) session.getAttribute("hoTen");
    // Lấy thông tin tài khoản từ session (nếu cần hiển thị thêm)
    // com.watchstore.model.User user = (com.watchstore.model.User) session.getAttribute("account");
%>

<%-- ⭐ INCLUDE TOP MENU ⭐ --%>
<jsp:include page="top_menu.jsp" />


<main class="content"> <%-- Hoặc thẻ div chính của bạn --%>
    <%-- Container riêng cho nội dung thanh toán --%>
    <div class="form-container" style="max-width: 600px; margin: 20px auto; padding: 30px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center;">
        <h2 class="title" style="color: #2d3436; margin-bottom: 20px;">💳 Thanh toán Chuyển khoản Ngân hàng</h2>

        <p style="font-size: 16px; color: #2d3436;">Xin chào, <strong><%= hoTen != null ? hoTen : "Quý khách" %></strong></p>
        <p style="font-size: 16px; color: #2d3436;">Vui lòng chuyển khoản với nội dung <strong style="color: #d63031;">[Tên người đặt] + [Số điện thoại]</strong> đến:</p>

        <%-- Hiển thị thông tin tài khoản --%>
        <ul style="text-align: left; display: inline-block; margin: 15px auto 20px auto; font-size: 16px; list-style: none; padding: 0;">
            <li style="margin-bottom: 8px;"><strong>Ngân hàng:</strong> MB Bank</li>
            <li style="margin-bottom: 8px;"><strong>Số tài khoản:</strong> <strong style="color: #007bff;">10207032004</strong></li>
            <li style="margin-bottom: 8px;"><strong>Chủ tài khoản:</strong> NGUYEN DANH THAI</li>
        </ul>

        <%-- Hiển thị mã QR (nếu có) --%>
        <div class="qr" style="margin: 20px 0;">
            <%-- ⭐ SỬA ĐƯỜNG DẪN ẢNH QR CHO PHÙ HỢP ⭐ --%>
            <img src="<c:url value='/images/bank_payment.jpg'/>" alt="QR Code MB Bank" width="200" style="border: 1px solid #eee;">
            <%-- Đảm bảo bạn có file vcb_qr.jpg trong thư mục /images --%>
        </div>

        <p style="font-size: 14px; color: #555; margin-top: 15px;">
            Sau khi chuyển khoản thành công, chúng tôi sẽ xử lý và xác nhận đơn hàng của bạn trong thời gian sớm nhất.
        </p>

        <%-- Link quay lại cửa hàng/lịch sử đơn hàng --%>
        <div style="margin-top: 25px;">
             <%-- ⭐ SỬA LINK QUAY VỀ CHO PHÙ HỢP ⭐ --%>
            <a href="<c:url value='/home'/>" style="text-decoration: none; color: #007bff; margin-right: 15px;">&larr; Quay về Trang chủ</a>
            <a href="<c:url value='/order-history'/>" style="text-decoration: none; color: #555;">Xem Lịch sử Đơn hàng &rarr;</a>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />