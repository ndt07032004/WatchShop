<%-- 
    Document   : cart
    Created on : Oct 17, 2025, 4:22:11 PM
    Author     : THAI
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="top_menu.jsp" />


<main class="content">
    <h2 class="title">Giỏ Hàng Của Bạn</h2>
    <c:if test="${empty sessionScope.cart || empty sessionScope.cart.items}">
        <p>Giỏ hàng của bạn đang trống! Hãy tiếp tục mua sắm.</p>
    </c:if>
    <c:if test="${not empty sessionScope.cart.items}">
        <table border="1" width="100%" style="border-collapse: collapse; text-align: center;">
            <thead>
                <tr style="background-color: #f2f2f2;">
                    <th style="padding: 10px;">Sản phẩm</th><th>Tên</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th><th>Xóa</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${sessionScope.cart.items}" var="item">
                    <tr>
                        <td><img src="<c:url value='/${item.product.image}'/>" width="100px"></td>
                        <td>${item.product.name}</td>
                        <td>${item.quantity}</td>
                        <td><fmt:formatNumber value="${item.product.price}" type="currency" currencyCode="VND"/></td>
                        <td><fmt:formatNumber value="${item.totalPrice}" type="currency" currencyCode="VND"/></td>
                        <td><a href="cart?action=remove&id=${item.product.id}" style="color: red;">Xóa</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <h3 style="text-align: right; margin-top: 20px;">
            Tổng cộng: <span style="color: red;"><fmt:formatNumber value="${sessionScope.cart.totalMoney}" type="currency" currencyCode="VND"/></span>
        </h3>
        <div style="text-align: right; margin-top: 20px;">
            <a href="<c:url value='/checkout.jsp'/>" class="submit-btn" style="display: inline-block; width: auto;">Tiến hành thanh toán</a>
        </div>
    </c:if>
</main>

<jsp:include page="footer.jsp" />