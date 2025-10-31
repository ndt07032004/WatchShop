<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Đặt activePage TRƯỚC khi include top_menu --%>
<c:set var="activePage" value="profile" scope="request" />
<jsp:include page="top_menu.jsp" />

<%-- Phần nội dung chính bắt đầu sau nav.top-menu và div#wrapper > div.container --%>
<%-- Include menu trái nếu layout của bạn yêu cầu (đặt trong div.container nếu cần) --%>
<%-- <jsp:include page="left_menu.jsp" /> --%>

<main class="content"> <%-- Đặt nội dung chính trong thẻ main.content --%>
    <h2 class="title">Lịch Sử Đơn Hàng Của Tôi</h2>

    <c:if test="${empty requestScope.orderList}">
        <p style="text-align: center; font-size: 1.1em; margin: 20px;">
            Bạn chưa có đơn hàng nào. Hãy bắt đầu <a href="home">mua sắm</a>!
        </p>
    </c:if>

    <c:if test="${not empty requestScope.orderList}">
        <table border="1" width="100%" style="border-collapse: collapse; text-align: left; margin-top: 20px; font-size: 0.9em;">
            <thead>
                <tr style="background-color: #f2f2f2;">
                    <th style="padding: 10px;">Mã Đơn</th>
                    <th style="padding: 10px;">Ngày Đặt</th>
                    <th style="padding: 10px;">Tổng Tiền</th>
                    <th style="padding: 10px;">Thanh toán</th>
                    <th style="padding: 10px;">Trạng thái</th>
                    <th style="padding: 10px;">Chi tiết</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${requestScope.orderList}" var="order">
                    <tr>
                        <td style="padding: 8px;">#${order.id}</td>
                        <td style="padding: 8px;"><fmt:formatDate value="${order.orderDate}" pattern="dd-MM-yyyy HH:mm"/></td>
                        <td style="padding: 8px; color: red; font-weight: bold;">
                            <fmt:formatNumber value="${order.totalMoney}" type="currency" currencyCode="VND"/>
                        </td>
                        <td style="padding: 8px;">${order.paymentMethod}</td>
                        <td style="padding: 8px; font-weight: bold;">
                            <c:choose>
                                <c:when test="${order.status == 'Đã giao thành công'}"><span style="color: green;">${order.status}</span></c:when>
                                <c:when test="${order.status == 'Đang giao hàng'}"><span style="color: blue;">${order.status}</span></c:when>
                                <c:when test="${order.status == 'Đã hủy'}"><span style="color: grey; text-decoration: line-through;">${order.status}</span></c:when>
                                <c:otherwise><span style="color: orange;">${order.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td style="padding: 8px;">
                            <a href="order-detail?id=${order.id}" style="text-decoration: none;">Xem</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</main>
<%-- Phần nội dung chính kết thúc --%>

<jsp:include page="footer.jsp" />