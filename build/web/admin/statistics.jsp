<%-- /admin/statistics.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set các biến pageTitle và activePage mà admin_header.jsp cần --%>
<% request.setAttribute("pageTitle", "Thống kê"); %>
<% request.setAttribute("activePage", "statistics");%> <%-- Tên này cần khớp với link trong header --%>

<%-- Include header của trang admin --%>
<jsp:include page="admin_header.jsp" />

<%-- Phần nội dung chính của trang thống kê --%>
<main class="admin-content">
    <h2 class="title">Thống Kê Bán Hàng</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <p class="admin-message error">${requestScope.errorMessage}</p>
    </c:if>

    <%-- Bảng hiển thị thống kê --%>
    <table class="stats-table data-table"> <%-- Thêm class data-table cho đẹp --%>
        <thead>
             <tr style="background-color:#4e73df; color:white; text-align:center;"> <%-- Màu header --%>
                 <th style="padding: 12px 15px;">Nhóm</th>
                 <th style="padding: 12px 15px;">Chỉ Số</th>
                 <th style="padding: 12px 15px;">Giá Trị</th>
             </tr>
        </thead>
        <tbody>
            <%-- Nhóm: Tài chính --%>
            <tr class="group-header"> <%-- Thêm class cho dễ style --%>
                <td rowspan="3" class="group-name">Tài Chính</td>
                <td>💰 Tổng Doanh Thu (Giá bán hàng đã giao)</td>
                <td class="currency"><fmt:formatNumber value="${requestScope.totalRevenue}" type="currency" currencyCode="VND"/></td>
            </tr>
             <tr>
                <td>💸 Tổng Vốn (Giá nhập hàng đã bán)</td>
                <td class="currency"><fmt:formatNumber value="${requestScope.totalCOGS}" type="currency" currencyCode="VND"/></td>
            </tr>
             <tr class="group-summary"> <%-- Thêm class --%>
                <td>📈 Lợi Nhuận Gộp Ước Tính</td>
                <td class="currency"><fmt:formatNumber value="${requestScope.estimatedProfit}" type="currency" currencyCode="VND"/></td>
            </tr>

            <%-- Nhóm: Tồn Kho --%>
             <tr class="group-header">
                 <td rowspan="1" class="group-name">Tồn Kho</td>
                 <td>🏦 Tổng Giá Trị Tồn Kho (Theo giá nhập)</td>
                 <td class="currency"><fmt:formatNumber value="${requestScope.totalInventoryValue}" type="currency" currencyCode="VND"/></td>
             </tr>

            <%-- Nhóm: Đơn hàng --%>
            <tr class="group-header">
                <td rowspan="4" class="group-name">Đơn Hàng</td>
                <td>📦 Tổng Số Đơn Hàng</td>
                <td class="count">${requestScope.totalOrders}</td>
            </tr>
            <tr>
                <td>✅ Đơn Đã Giao Thành Công</td>
                <td class="count">${requestScope.completedOrders}</td>
            </tr>
            <tr>
                <td>⏳ Đơn Đang Chờ / Xử lý / Giao</td>
                <td class="count">
                    ${requestScope.pendingOrders + requestScope.processingOrders + requestScope.shippingOrders}<br>
                    <small style="font-weight: normal;">(Chờ: ${pendingOrders}, Xử lý: ${processingOrders}, Giao: ${shippingOrders})</small>
                </td>
            </tr>
            <tr>
                <td>❌ Đơn Đã Hủy</td>
                <td class="count">${requestScope.cancelledOrders}</td>
            </tr>

            <%-- Nhóm: Khách hàng --%>
            <tr class="group-header">
                <td rowspan="1" class="group-name">Khách Hàng</td>
                <td>👥 Tổng Số Khách Hàng</td>
                <td class="count">${requestScope.totalCustomers}</td>
            </tr>
        </tbody>
    </table>

    <%-- Phần biểu đồ --%>
    <div class="charts-container" style="margin-top: 40px; display: grid; grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); gap: 30px;">
         <div class="chart-card">
            <h3>Phân bố Trạng thái Đơn hàng</h3>
            <canvas id="orderStatusChart"></canvas>
        </div>
        <div class="chart-card">
            <h3>Tỷ lệ Trạng thái Đơn hàng</h3>
            <canvas id="orderStatusPieChart"></canvas>
        </div>
    </div>

</main>

<%-- Include footer của trang admin --%>
<jsp:include page="admin_footer.jsp" />

<%-- ⭐ SCRIPT ĐỂ VẼ BIỂU ĐỒ (Đặt sau footer hoặc trong <head>) ⭐ --%>
<%-- 1. Include thư viện Chart.js --%>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<%-- 2. Code JavaScript để lấy dữ liệu và vẽ --%>
<script>
    // Lấy dữ liệu số lượng đơn hàng từ requestScope (do Servlet gửi sang)
    const pendingOrders = parseInt('${requestScope.pendingOrders}');
    const processingOrders = parseInt('${requestScope.processingOrders}');
    const shippingOrders = parseInt('${requestScope.shippingOrders}');
    const completedOrders = parseInt('${requestScope.completedOrders}');
    const cancelledOrders = parseInt('${requestScope.cancelledOrders}');

    // --- Biểu đồ cột (Bar Chart) ---
    const ctxBar = document.getElementById('orderStatusChart'); // Lấy thẻ canvas
    if (ctxBar) { // Kiểm tra canvas tồn tại
        new Chart(ctxBar, {
            type: 'bar', // Loại biểu đồ
            data: {
                labels: ['Chưa TT', 'Đang xử lý', 'Đang giao', 'Hoàn thành', 'Đã hủy'], // Nhãn các cột
                datasets: [{
                        label: 'Số lượng đơn hàng', // Chú thích
                        data: [pendingOrders, processingOrders, shippingOrders, completedOrders, cancelledOrders], // Dữ liệu tương ứng
                        backgroundColor: [// Màu cho từng cột
                            'rgba(243, 156, 18, 0.7)', // Cam
                            'rgba(142, 68, 173, 0.7)', // Tím
                            'rgba(52, 152, 219, 0.7)', // Xanh dương
                            'rgba(46, 204, 113, 0.7)', // Xanh lá
                            'rgba(149, 165, 166, 0.7)'  // Xám
                        ],
                        borderColor: [
                            'rgba(243, 156, 18, 1)',
                            'rgba(142, 68, 173, 1)',
                            'rgba(52, 152, 219, 1)',
                            'rgba(46, 204, 113, 1)',
                            'rgba(149, 165, 166, 1)'
                        ],
                        borderWidth: 1
                    }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true, // Bắt đầu trục Y từ 0
                        ticks: {
                            stepSize: 1 // Bước nhảy là 1 nếu số lượng ít
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false // Ẩn chú thích mặc định (vì chỉ có 1 dataset)
                    }
                }
            }
        });
    } else {
        console.error("Canvas with ID 'orderStatusChart' not found.");
    }

    // --- Biểu đồ tròn (Doughnut Chart) ---
    const ctxPie = document.getElementById('orderStatusPieChart'); // Lấy canvas thứ 2
    if (ctxPie) {
        new Chart(ctxPie, {
            type: 'doughnut', // Loại biểu đồ tròn (có lỗ ở giữa)
            data: {
                labels: ['Chưa TT', 'Đang xử lý', 'Đang giao', 'Hoàn thành', 'Đã hủy'],
                datasets: [{
                        label: 'Tỷ lệ đơn hàng',
                        data: [pendingOrders, processingOrders, shippingOrders, completedOrders, cancelledOrders],
                        backgroundColor: [// Màu cho từng phần
                            'rgba(243, 156, 18, 0.8)', // Cam
                            'rgba(142, 68, 173, 0.8)', // Tím
                            'rgba(52, 152, 219, 0.8)', // Xanh dương
                            'rgba(46, 204, 113, 0.8)', // Xanh lá
                            'rgba(149, 165, 166, 0.8)'  // Xám
                        ],
                        hoverOffset: 4 // Hiệu ứng khi di chuột
                    }]
            },
            options: {
                responsive: true, // Tự co giãn theo kích thước container
                plugins: {
                    legend: {
                        position: 'top', // Hiển thị chú thích ở trên
                    },
                    tooltip: {// Hiển thị tooltip khi di chuột
                        enabled: true
                    }
                }
            }
        });
    } else {
        console.error("Canvas with ID 'orderStatusPieChart' not found.");
    }

</script>
<%-- ⭐ KẾT THÚC SCRIPT ⭐ --%>