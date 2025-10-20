<%-- 
    Document   : checkout
    Created on : Oct 17, 2025, 4:22:18 PM
    Author     : THAI
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Bắt buộc người dùng phải đăng nhập mới vào được trang này --%>
<c:if test="${empty sessionScope.account}">
    <c:redirect url="login.jsp"/>
</c:if>

<jsp:include page="top_menu.jsp" />


<main class="content">
    <div class="form-container">
        <h2 class="title">Xác Nhận Đơn Hàng</h2>
        <c:if test="${not empty requestScope.success}">
            <p style="color:green; text-align: center;">${requestScope.success}</p>
            <a href="<c:url value='/home'/>">Tiếp tục mua sắm</a>
        </c:if>

        <c:if test="${empty requestScope.success}">
            <h3>Thông tin giao hàng</h3>
            <p><strong>Người nhận:</strong> ${sessionScope.account.fullname}</p>
            <p><strong>Địa chỉ:</strong> ${sessionScope.account.address}</p>
            <p><strong>Số điện thoại:</strong> ${sessionScope.account.phone}</p>
            <hr>
            <h3>Sản phẩm trong giỏ</h3>
            <c:forEach items="${sessionScope.cart.items}" var="item">
                <p>${item.product.name} x ${item.quantity}</p>
            </c:forEach>
            <hr>
            <h3 style="color: red;">Tổng tiền: ${sessionScope.cart.totalMoney} VNĐ</h3>
            
            <form action="checkout" method="post">
                <button type="submit" class="submit-btn">Xác Nhận và Đặt Hàng</button>
            </form>
        </c:if>
    </div>
</main>

<jsp:include page="footer.jsp" />
