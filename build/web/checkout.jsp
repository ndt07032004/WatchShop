<%-- checkout.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- ⭐ THÊM DÒNG NÀY ĐỂ DÙNG fmt:formatNumber ⭐ --%>

<%-- Bắt buộc người dùng phải đăng nhập --%>
<c:if test="${empty sessionScope.account}">
    <c:redirect url="login.jsp"/>
</c:if>

<%-- Include menu đầu trang --%>
<jsp:include page="top_menu.jsp" />

<%-- Phần nội dung chính (Nằm giữa top_menu và footer) --%>
<%-- Include menu trái nếu layout của bạn yêu cầu --%>
<%-- <jsp:include page="left_menu.jsp" /> --%>

<main class="content"> <%-- Hoặc thẻ div chính của bạn --%>
    <div class="form-container" style="max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <h2 class="title1">Xác Nhận Đơn Hàng</h2>

        <%-- Hiển thị thông báo Lỗi (nếu có) --%>
        <c:if test="${not empty requestScope.error}">
            <p style="color:red; text-align: center; font-weight: bold; background-color: #ffeeee; padding: 10px; border-radius: 4px; border: 1px solid #fcc; margin-bottom: 15px;">
                ${requestScope.error}
            </p>
        </c:if>

        <%-- Chỉ hiển thị form nếu chưa có thông báo thành công (tránh hiển thị lại sau khi đặt hàng) --%>
        <c:if test="${empty requestScope.success}">
            <%-- ⭐ BẮT ĐẦU FORM ⭐ --%>
            <form action="checkout" method="post">
                <h3>Thông tin giao hàng</h3>
                <div style="text-align: left; margin-bottom: 15px; padding: 10px; border: 1px solid #eee; border-radius: 4px;">
                    <p style="margin: 5px 0;"><strong>Người nhận:</strong> ${sessionScope.account.fullname}</p>
                    <p style="margin: 5px 0;"><strong>Địa chỉ:</strong> ${sessionScope.account.address}</p>
                    <p style="margin: 5px 0;"><strong>Số điện thoại:</strong> ${sessionScope.account.phone}</p>
                </div>
                <hr style="margin: 15px 0;">

                <h3>Sản phẩm trong giỏ</h3>
                 <div style="text-align: left; margin-bottom: 15px; max-height: 200px; overflow-y: auto;">
                     <c:forEach items="${sessionScope.cart.items}" var="item">
                         <p style="margin: 8px 0; border-bottom: 1px dashed #eee; padding-bottom: 5px;">
                             <img src="<c:url value='/${item.product.image}'/>" alt="" style="width: 30px; height: 30px; vertical-align: middle; margin-right: 5px;">
                             ${item.product.name} x <strong>${item.quantity}</strong>
                             <span style="float: right; font-size: 0.9em; color: #555;">
                                 <fmt:formatNumber value="${item.totalPrice}" type="currency" currencyCode="VND"/>
                             </span>
                         </p>
                     </c:forEach>
                 </div>
                 <hr style="margin: 15px 0;">

                <%-- Tổng tiền --%>
                <h3 style="color: red; text-align: right; margin-bottom: 20px;">
                    Tổng tiền: <fmt:formatNumber value="${sessionScope.cart.totalMoney}" type="currency" currencyCode="VND"/>
                </h3>

                <%-- Phần chọn phương thức thanh toán --%>
                <hr style="margin: 15px 0;">
                <h3 style="text-align: left;">Chọn phương thức thanh toán</h3>
                <div class="payment-options" style="text-align: left; margin-bottom: 25px;">
                    <div class="form-group-radio" style="margin-bottom: 10px;">
                        <input type="radio" id="payment_cod" name="paymentMethod" value="COD" checked style="margin-right: 8px; transform: scale(1.1);">
                        <label for="payment_cod">Thanh toán khi nhận hàng (COD)</label>
                    </div>
                    <div class="form-group-radio" style="margin-bottom: 10px;">
                        <input type="radio" id="payment_bank" name="paymentMethod" value="BANK" style="margin-right: 8px; transform: scale(1.1);">
                        <label for="payment_bank">Chuyển khoản ngân hàng</label>
                    </div>
                    <div class="form-group-radio">
                        <input type="radio" id="payment_momo" name="paymentMethod" value="MOMO" style="margin-right: 8px; transform: scale(1.1);">
                        <label for="payment_momo">Thanh toán qua ví MOMO</label>
                    </div>
                </div>

                <%-- Nút submit --%>
                <div class="form-group">
                    <input type="submit" value="Xác nhận đặt hàng" class="submit-btn" style="width: 100%;"> <%-- Nút chiếm hết chiều rộng --%>
                    <a href="<c:url value='/cart'/>" class="action-btn" style="display: inline-block;width: auto;margin-top: 12px;margin-left: 254px;">Quay về giỏ hàng</a>
                </div>
            <%-- ⭐ ĐÓNG FORM ⭐ --%>
            </form>
        </c:if> <%-- Kết thúc if empty requestScope.success --%>

        <%-- Hiển thị nếu đặt hàng thành công (Mặc dù servlet đã redirect, để phòng trường hợp forward) --%>
        <c:if test="${not empty requestScope.success}">
             <p style="color:green; text-align: center; font-weight: bold; background-color: #e6ffed; padding: 15px; border-radius: 4px; border: 1px solid #b7ebc9; margin-top: 20px;">
                 ${requestScope.success}
             </p>
             <div style="text-align: center; margin-top: 20px;">
                  
                  <a href="<c:url value='/order-history'/>" class="submit-btn" style="display: inline-block; width: auto; background-color: #9b59b6; margin-left: 10px;">Xem đơn hàng</a>
             </div>
        </c:if>
    </div>
</main>
<%-- Phần nội dung chính kết thúc --%>

<%-- Include chân trang --%>
<jsp:include page="footer.jsp" />