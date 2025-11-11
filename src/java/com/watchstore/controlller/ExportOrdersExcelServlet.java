package com.watchstore.controlller;

import com.watchstore.dao.OrderDAO;
import com.watchstore.model.Order;
import com.watchstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet(name = "ExportOrdersExcelServlet", urlPatterns = {"/admin/export-orders"})
public class ExportOrdersExcelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied.");
            return;
        }

        try {
            OrderDAO orderDAO = new OrderDAO();
            // Lấy dữ liệu theo cách đã tối ưu hóa N+1
            List<Order> orderList = orderDAO.getAllOrdersWithDetails();

            // Tự xử lý tạo chuỗi tóm tắt sản phẩm trong Java
            for (Order order : orderList) {
                StringBuilder summary = new StringBuilder();
                if (order.getDetails() != null) {
                    for (int i = 0; i < order.getDetails().size(); i++) {
                        com.watchstore.model.OrderDetail detail = order.getDetails().get(i);
                        summary.append(detail.getProduct().getName())
                               .append(" (SL: ").append(detail.getQuantity()).append(")");
                        if (i < order.getDetails().size() - 1) {
                            summary.append(", \n"); // Thêm xuống dòng cho dễ đọc trong Excel
                        }
                    }
                }
                order.setProductSummary(summary.toString());
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Danh sách Đơn hàng");

            // ===== STYLE =====
            // 1️⃣ Tiêu đề lớn
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 2️⃣ Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // 3️⃣ Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
            dataStyle.setWrapText(true);

            // 4️⃣ Currency style
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            currencyStyle.cloneStyleFrom(dataStyle);
            currencyStyle.setDataFormat(df.getFormat("#,##0 VNĐ"));
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);

            // ===== DÒNG TIÊU ĐỀ LỚN =====
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DANH SÁCH ĐƠN HÀNG - WATCHSTORE");
            titleCell.setCellStyle(titleStyle);

            // Gộp ô từ A1 -> I1 (9 cột)
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

            // ===== HEADER =====
            String[] headers = {
                "ID", "Ngày Đặt", "Khách Hàng", "SĐT", "Địa chỉ",
                "Sản phẩm", "Tổng Tiền", "Thanh Toán", "Trạng Thái"
            };

            Row headerRow = sheet.createRow(2); // hàng thứ 3 (sau dòng tiêu đề)
            headerRow.setHeightInPoints(25);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== DỮ LIỆU =====
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            int rowNum = 3; // bắt đầu từ hàng 4

            for (Order order : orderList) {
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(22);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(order.getId());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : "");
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(order.getCustomerName());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(order.getCustomerPhone());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(order.getCustomerAddress());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(order.getProductSummary());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(order.getTotalMoney());
                cell6.setCellStyle(currencyStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(order.getPaymentMethod());
                cell7.setCellStyle(dataStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(order.getStatus());
                cell8.setCellStyle(dataStyle);
            }

            // ===== AUTO SIZE COLUMNS =====
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(currentWidth + 1000, 15000));
            }

            // ===== EXPORT =====
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"BaoCao_DonHang_WatchStore.xlsx\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }

            workbook.close();
            System.out.println("✅ Xuất Excel danh sách đơn hàng thành công.");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h2>Lỗi khi xuất Excel</h2><p>" + e.getMessage() + "</p>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Xuất báo cáo đơn hàng ra Excel (có tiêu đề lớn, viền và style)";
    }
}
