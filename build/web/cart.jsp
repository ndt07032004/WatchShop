<%-- /cart.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Đặt activePage TRƯỚC khi include top_menu --%>
<c:set var="activePage" value="cart" scope="request" />
<jsp:include page="top_menu.jsp" />

<main class="content">
    <div class="content2">
    <h2 class="title1">Giỏ Hàng Của Bạn</h2>

    <%-- Hiển thị thông báo từ session (ví dụ: lỗi tồn kho) --%>
    <c:if test="${not empty sessionScope.cartMessage}">
        <p class="cart-message error" style="text-align: center; color: red; font-weight: bold; margin: 15px 0; padding: 10px; background-color: #ffebee; border: 1px solid #e57373; border-radius: 4px;">
            <i class="fas fa-exclamation-circle"></i> ${sessionScope.cartMessage}
        </p>
        <c:remove var="cartMessage" scope="session"/> <%-- Xóa thông báo sau khi hiển thị --%>
    </c:if>

    <%-- Hiển thị nếu giỏ hàng trống --%>
    <c:if test="${empty sessionScope.cart || empty sessionScope.cart.items}">
        <p style="text-align: center; font-size: 1.1em; margin: 40px 0;">
            Giỏ hàng của bạn đang trống! Hãy <a href="<c:url value='/home'/>" style="color: #0d6efd; text-decoration: none; font-weight: bold;">tiếp tục mua sắm</a>.
        </p>
    </c:if>

    <%-- Hiển thị bảng sản phẩm nếu giỏ hàng không trống --%>
    <c:if test="${not empty sessionScope.cart.items}">
        <table class="cart-table data-table"> <%-- Thêm class data-table nếu bạn dùng chung style --%>
            <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>Tên</th>
                    <th style="width: 130px;">Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                    <th>Xóa</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${sessionScope.cart.items}" var="item">
                    <tr>
                        <td class="cart-img">
                            <a href="detail?pid=${item.product.id}">
                                <img src="<c:url value='/${item.product.image}'/>" alt="${item.product.name}">
                            </a>
                        </td>
                        <td class="cart-name">
                             <a href="detail?pid=${item.product.id}"><c:out value="${item.product.name}"/></a>
                             <%-- Hiển thị cảnh báo nếu số lượng bằng tồn kho --%>
                             <c:if test="${item.quantity >= item.product.stock}">
                                 <br/><small style="color: orange;">(Đã đạt SL tồn kho)</small>
                             </c:if>
                        </td>

                        <%-- Ô chứa nút +/- và số lượng --%>
                        <td class="cart-qty">
                            <div class="qty-controls">
                                <%-- Nút Giảm (-) --%>
                                <c:choose>
                                     <c:when test="${item.quantity > 1}">
                                         <%-- Link gọi action=decrease --%>
                                         <a href="cart?action=decrease&id=${item.product.id}" class="qty-btn decrease-btn" title="Giảm số lượng">-</a>
                                     </c:when>
                                     <c:otherwise>
                                         <%-- Nút bị vô hiệu hóa --%>
                                         <span class="qty-btn disabled" title="Số lượng tối thiểu là 1">-</span>
                                     </c:otherwise>
                                </c:choose>

                                <%-- Hiển thị Số lượng hiện tại --%>
                                <span class="current-qty">${item.quantity}</span>

                                <%-- Nút Tăng (+) --%>
                                <c:choose>
                                    <%-- Chỉ hiển thị nút tăng nếu số lượng hiện tại < tồn kho --%>
                                    <c:when test="${item.quantity < item.product.stock}">
                                         <%-- Link gọi action=increase --%>
                                         <a href="cart?action=increase&id=${item.product.id}" class="qty-btn increase-btn" title="Tăng số lượng">+</a>
                                    </c:when>
                                     <c:otherwise>
                                         <%-- Nút bị vô hiệu hóa khi đạt giới hạn tồn kho --%>
                                         <span class="qty-btn disabled" title="Đã đạt số lượng tồn kho tối đa">+</span>
                                     </c:otherwise>
                                </c:choose>
                            </div>
                        </td>

                        <td class="price"><fmt:formatNumber value="${item.product.price}" type="currency" currencyCode="VND"/></td> <%-- Dùng item.price (giá lúc thêm) --%>
                        <td class="price"><fmt:formatNumber value="${item.totalPrice}" type="currency" currencyCode="VND"/></td>
                        <td class="cart-remove">
                            <%-- Link gọi action=remove --%>
                            <a href="cart?action=remove&id=${item.product.id}" title="Xóa sản phẩm này">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <%-- Tổng tiền và nút hành động --%>
        <div class="cart-summary">
            <h3>Tổng cộng: <span class="total-price"><fmt:formatNumber value="${sessionScope.cart.totalMoney}" type="currency" currencyCode="VND"/></span></h3>
            <div class="cart-actions">
                <a href="<c:url value='/home'/>" class="action-btn continue-shopping-btn">Tiếp tục mua sắm</a>
                <a href="<c:url value='/checkout.jsp'/>" class="submit-btn checkout-btn">Tiến hành thanh toán</a>
            </div>
        </div>
    </c:if>
    </div>
   
</main>

<jsp:include page="footer.jsp" />

