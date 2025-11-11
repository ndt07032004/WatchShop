<%-- /admin/manage_orders.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set các biến pageTitle và activePage mà admin_header.jsp cần --%>
<% request.setAttribute("pageTitle", "Quản lý Đơn hàng"); %>
<% request.setAttribute("activePage", "orders"); %> <%-- Tên này cần khớp với link trong header --%>

<%-- Include header của trang admin --%>
<jsp:include page="admin_header.jsp" />

<%-- Phần nội dung chính của trang quản lý đơn hàng --%>
<main class="admin-content">
    <h2 class="title">Quản lý Đơn hàng</h2>
      <a href="<c:url value='/admin/export-orders'/>" class="submit-btn" style="display:inline-block; width: auto; margin-bottom: 20px;"> Xuất ra Excel</a>
    <%-- Hiển thị thông báo thành công/lỗi từ session (sau khi cập nhật status) --%>
    <c:if test="${not empty sessionScope.adminOrderSuccess}">
        <p class="admin-message success"> <%-- Dùng class CSS admin --%>
            ${sessionScope.adminOrderSuccess}
        </p>
        <c:remove var="adminOrderSuccess" scope="session" /> <%-- Xóa thông báo sau khi hiển thị --%>
    </c:if>
    <c:if test="${not empty sessionScope.adminOrderError}">
        <p class="admin-message error"> <%-- Dùng class CSS admin --%>
            ${sessionScope.adminOrderError}
        </p>
        <c:remove var="adminOrderError" scope="session" /> <%-- Xóa thông báo sau khi hiển thị --%>
    </c:if>

    <%-- Kiểm tra nếu danh sách đơn hàng rỗng --%>
    <c:if test="${empty requestScope.orderList}">
        <p>Hiện chưa có đơn hàng nào trong hệ thống.</p>
    </c:if>

    <%-- Hiển thị bảng nếu có đơn hàng --%>
    <c:if test="${not empty requestScope.orderList}">
        <table class="admin-table data-table"> <%-- Dùng class CSS admin --%>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ngày Đặt</th>
                    <th>Khách Hàng</th>
                    <th>SĐT</th>
                    <th>Địa chỉ</th>
                    <th>Sản phẩm</th>  <%-- ⭐ THÊM CỘT NÀY ⭐ --%>
                    <th>Tổng Tiền</th>
                    <th>Thanh Toán</th>
                    <th>Trạng Thái</th> <%-- Rút gọn --%>
                    <th>Cập Nhật</th> <%-- Rút gọn --%>
                </tr>
            </thead>
            <tbody>
                <%-- Lặp qua danh sách đơn hàng được gửi từ Servlet --%>
                <c:forEach items="${requestScope.orderList}" var="order">
                    <tr>
                        <td>#${order.id}</td>
                        <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yy HH:mm"/></td>
                        <td>${order.customerName}</td>
                        <td>${order.customerPhone}</td>
                        <td><small>${order.customerAddress}</small></td>
                        
                        <td class="product-details-cell" style="text-align: left; font-size: 0.85em;">
                            <%-- Lấy danh sách chi tiết trực tiếp từ đối tượng order --%>
                            <c:if test="${not empty order.details}">
                                <ul style="margin: 0; padding-left: 15px; list-style: square;">
                                    <c:forEach items="${order.details}" var="detail" varStatus="loop">
                                        <li style="${loop.index > 1 ? 'margin-top: 5px;' : ''}">
                                            <c:out value="${detail.product.name}"/> (SL: <c:out value="${detail.quantity}"/>)
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:if>
                            <c:if test="${empty order.details}">
                                <i style="color: grey;">(Không có sản phẩm)</i>
                            </c:if>
                        </td>
                        <%-- ⭐ KẾT THÚC TD MỚI ⭐ --%>
                        <td class="price"><fmt:formatNumber value="${order.totalMoney}" type="currency" currencyCode="VND"/></td>
                        <td>${order.paymentMethod}</td>
                        <td class="status">
                            <%-- Hiển thị trạng thái với màu sắc (dùng class CSS sẽ tốt hơn) --%>
                            <c:choose>
                                <c:when test="${order.status == 'Đã giao thành công'}"><span class="status-delivered">${order.status}</span></c:when>
                                <c:when test="${order.status == 'Đang giao hàng'}"><span class="status-shipping">${order.status}</span></c:when>
                                <c:when test="${order.status == 'Đang xử lý'}"><span class="status-processing">${order.status}</span></c:when>
                                <c:when test="${order.status == 'Đã hủy'}"><span class="status-cancelled">${order.status}</span></c:when>
                                <c:otherwise><span class="status-pending">${order.status}</span></c:otherwise> <%-- Chưa thanh toán --%>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <%-- ⭐ ĐÂY LÀ PHẦN ADMIN DUYỆT ĐƠN ⭐ --%>
                            <%-- Chỉ hiển thị form cập nhật nếu đơn hàng chưa hoàn thành hoặc chưa hủy --%>
                            <c:if test="${order.status != 'Đã giao thành công' && order.status != 'Đã hủy'}">
                                <%-- Form để gửi yêu cầu cập nhật trạng thái --%>
                                <form action="<c:url value='/admin/manage-orders'/>" method="post" style="margin: 0; display: flex; align-items: center; gap: 5px;">
                                    <%-- Gửi ID đơn hàng ẩn đi --%>
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <%-- Dropdown chọn trạng thái mới --%>
                                    <select name="newStatus" class="status-select">
                                        <%-- Các tùy chọn hợp lệ tiếp theo dựa trên trạng thái hiện tại --%>
                                        <c:if test="${order.status == 'Chưa thanh toán'}">
                                            <c:if test="${order.paymentMethod == 'BANK' || order.paymentMethod == 'MOMO'}">
                                                <%-- ⭐ TÙY CHỌN QUAN TRỌNG: Xác nhận thanh toán chuyển khoản --%>
                                                <option value="Đang xử lý">✅ Xác nhận TT & Xử lý</option>
                                            </c:if>
                                            <c:if test="${order.paymentMethod == 'COD'}">
                                                <option value="Đang xử lý">📦 Xác nhận & Xử lý (COD)</option>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${order.status == 'Đang xử lý'}">
                                            <option value="Đang giao hàng">🚚 Giao hàng</option>
                                        </c:if>
                                        <c:if test="${order.status == 'Đang giao hàng'}">
                                            <option value="Đã giao thành công">🏁 Hoàn thành</option>
                                        </c:if>
                                        <%-- Luôn có tùy chọn Hủy (trừ khi đã hủy/giao) --%>
                                        <option value="Đã hủy">❌ Hủy đơn</option>
                                    </select>
                                    <%-- Nút bấm để gửi form --%>
                                    <button type="submit" class="action-btn update-btn">Cập nhật</button>
                                </form>
                            </c:if>
                             <%-- Thông báo nếu đơn hàng đã kết thúc --%>
                             <c:if test="${order.status == 'Đã giao thành công' || order.status == 'Đã hủy'}">
                                <i style="color: grey;">(Đã ${order.status})</i>
                             </c:if>
                             <%-- ⭐ KẾT THÚC PHẦN DUYỆT ĐƠN ⭐ --%>
                        </td>
                         <%-- <td><a href="<c:url value='/admin/order-detail?id=${order.id}'/>">Xem</a></td> --%> <%-- Bỏ cột chi tiết nếu chưa làm --%>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if> <%-- Kết thúc if not empty orderList --%>

</main>

<%-- Include footer của trang admin --%>
<jsp:include page="admin_footer.jsp" />