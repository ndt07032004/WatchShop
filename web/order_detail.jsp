<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Đặt activePage TRƯỚC khi include top_menu --%>
<c:set var="activePage" value="profile" scope="request" />
<jsp:include page="top_menu.jsp" />

<%-- Phần nội dung chính --%>
<%-- <jsp:include page="left_menu.jsp" /> --%>

<main class="content">
    <div style="max-width: 900px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <h2 class="title">Chi Tiết Đơn Hàng #${requestScope.orderInfo.id}</h2>

        <c:if test="${empty requestScope.orderInfo || empty requestScope.orderDetails}">
            <p style="color: red; text-align: center;">Không tìm thấy thông tin chi tiết cho đơn hàng này.</p>
            <p style="text-align: center;"><a href="order-history">Quay lại Lịch sử Đơn hàng</a></p>
        </c:if>

        <c:if test="${not empty requestScope.orderInfo && not empty requestScope.orderDetails}">
            <%-- Thông tin chung của đơn hàng --%>
            <div style="margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #eee;">
                <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${orderInfo.orderDate}" pattern="dd-MM-yyyy HH:mm"/> </p>
                <p><strong>Người nhận:</strong> ${orderInfo.customerName}</p>
                <p><strong>Địa chỉ giao hàng:</strong> ${orderInfo.customerAddress}</p>
                <p><strong>Số điện thoại:</strong> ${orderInfo.customerPhone}</p>
                <p><strong>Phương thức thanh toán:</strong> ${orderInfo.paymentMethod}</p>
                <p><strong>Trạng thái đơn hàng:</strong>
                    <span style="font-weight: bold;">
                        <c:choose>
                            <c:when test="${orderInfo.status == 'Đã giao thành công'}"><span style="color: green;">${orderInfo.status}</span></c:when>
                            <c:when test="${orderInfo.status == 'Đang giao hàng'}"><span style="color: blue;">${orderInfo.status}</span></c:when>
                            <c:when test="${orderInfo.status == 'Đã hủy'}"><span style="color: grey; text-decoration: line-through;">${orderInfo.status}</span></c:when>
                            <c:otherwise><span style="color: orange;">${orderInfo.status}</span></c:otherwise>
                        </c:choose>
                    </span>
                </p>

                <%-- ⭐ BẮT ĐẦU CODE MỚI: Nút Chuyển Khoản / Thông báo COD ⭐ --%>
                <div style="margin-top: 15px;">
                    <c:choose>
                        <c:when test="${orderInfo.paymentMethod == 'BANK'}">
                            <%-- Chỉ hiển thị nút nếu đơn hàng chưa thanh toán hoặc đang xử lý --%>
                            <c:if test="${orderInfo.status == 'Chưa thanh toán' || orderInfo.status == 'Đang xử lý'}">
                                <a href="bank_payment.jsp" class="submit-btn" style="display: inline-block; width: auto; background-color: #e67e22;">
                                    🏦 Chuyển khoản ngay
                                </a>
                            </c:if>
                        </c:when>
                        <c:when test="${orderInfo.paymentMethod == 'MOMO'}">
                             <%-- Chỉ hiển thị nút nếu đơn hàng chưa thanh toán hoặc đang xử lý --%>
                            <c:if test="${orderInfo.status == 'Chưa thanh toán' || orderInfo.status == 'Đang xử lý'}">
                                <a href="momo_payment.jsp" class="submit-btn" style="display: inline-block; width: auto; background-color: #a42c94;">
                                    <img src="<c:url value='/images/momo-icon.png'/>" alt="Momo" style="width: 16px; height: 16px; vertical-align: middle; margin-right: 5px;"> Thanh toán Momo
                                </a>
                                <%-- Giả sử bạn có icon Momo trong /images/momo-icon.png --%>
                            </c:if>
                        </c:when>
                        <c:when test="${orderInfo.paymentMethod == 'COD'}">
                            <p style="font-style: italic; color: #555;">
                                Đơn hàng COD đang chờ xử lý/giao hàng. Bạn sẽ thanh toán khi nhận được sản phẩm.
                            </p>
                        </c:when>
                    </c:choose>
                </div>
                <%-- ⭐ KẾT THÚC CODE MỚI ⭐ --%>

            </div>

            <%-- Bảng chi tiết sản phẩm --%>
            <h3>Các sản phẩm đã mua</h3>
            <table border="1" width="100%" style="border-collapse: collapse; text-align: left; margin-top: 10px; font-size: 0.9em;">
                <thead>
                    <tr style="background-color: #f2f2f2;">
                        <th style="padding: 10px;" colspan="2">Sản phẩm</th>
                        <th style="padding: 10px; text-align: center;">Số lượng</th>
                        <th style="padding: 10px; text-align: right;">Đơn giá (tại lúc mua)</th>
                        <th style="padding: 10px; text-align: right;">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${requestScope.orderDetails}" var="detail">
                        <tr>
                            <td style="padding: 8px; width: 60px;">
                                <img src="<c:url value='/${detail.product.image}'/>" alt="${detail.product.name}" style="width: 50px; height: 50px; object-fit: cover;">
                            </td>
                            <td style="padding: 8px;">${detail.product.name}</td>
                            <td style="padding: 8px; text-align: center;">${detail.quantity}</td>
                            <td style="padding: 8px; text-align: right;">
                                <fmt:formatNumber value="${detail.price}" type="currency" currencyCode="VND"/>
                            </td>
                            <td style="padding: 8px; text-align: right;">
                                <fmt:formatNumber value="${detail.price * detail.quantity}" type="currency" currencyCode="VND"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                     <tr style="font-weight: bold;">
                         <td colspan="4" style="padding: 10px; text-align: right;">Tổng cộng:</td>
                         <td style="padding: 10px; text-align: right; color: red;">
                             <fmt:formatNumber value="${orderInfo.totalMoney}" type="currency" currencyCode="VND"/>
                         </td>
                     </tr>
                </tfoot>
            </table>

             <div style="text-align: center; margin-top: 25px;">
                  <a href="order-history" style="text-decoration: none; color: #007bff;">&larr; Quay lại Lịch sử Đơn hàng</a>
                  
             </div>
        </c:if> <%-- Kết thúc if not empty --%>
    </div>
</main>
<%-- Phần nội dung chính kết thúc --%>

<jsp:include page="footer.jsp" />