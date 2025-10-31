<%-- success.jsp (Chỉ dành cho COD) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Include menu đầu trang --%>
<jsp:include page="top_menu.jsp" />



<main class="content">
    <%-- Container riêng cho nội dung thành công --%>
    <div class="form-container" style="max-width: 550px; margin: 40px auto; padding: 40px 30px; background-color: #fff; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); text-align: center;">
        <div style="font-size: 50px; color: #2ecc71; margin-bottom: 15px;">✅</div>
        <h2 class="title" style="color: #2c3e50; font-size: 24px; margin-bottom: 10px;">Đặt hàng thành công! (COD)</h2>
        <p style="color: #555; line-height: 1.6; margin-bottom: 25px;">
            Cảm ơn bạn đã mua hàng tại <strong>WatchStore</strong>.
            Đơn hàng của bạn (thanh toán khi nhận hàng) đang được xử lý và sẽ sớm được giao đến bạn.
            Vui lòng chuẩn bị tiền mặt để thanh toán khi nhận hàng.
        </p>
        <div style="margin-top: 25px;">
            <a href="<c:url value='/order-history'/>" class="submit-btn" style="display: inline-block; width: auto; background-color: #3498db; margin-right: 10px;">📊 Xem Lịch sử Đơn hàng</a>
            <a href="<c:url value='/home'/>" class="submit-btn" style="display: inline-block; width: auto; background-color: #95a5a6;">🏠 Tiếp tục mua sắm</a>
        </div>
    </div>
</main>
<%-- Phần nội dung chính kết thúc --%>

<%-- Include chân trang --%>
<jsp:include page="footer.jsp" />