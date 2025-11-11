package com.watchstore.controlller; // Giữ nguyên package

import com.watchstore.dao.ProductDAO;
import com.watchstore.model.Product;
import com.watchstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream; // Cần cho việc ghi file
import java.util.List;

// Import Apache POI classes
import org.apache.poi.ss.usermodel.*; // Interface chung
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Cụ thể cho .xlsx

@WebServlet(name = "ExportExcelServlet", urlPatterns = {"/admin/export-products"}) // URL riêng cho export
public class ExportExcelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied."); // Báo lỗi 403
            return;
        }

        try {
            // 2. Lấy dữ liệu cần xuất (ví dụ: danh sách sản phẩm)
            ProductDAO productDAO = new ProductDAO();
            List<Product> productList = productDAO.getAllProducts();

            // 3. Tạo Workbook và Sheet Excel
            Workbook workbook = new XSSFWorkbook(); // Tạo workbook cho file .xlsx
            Sheet sheet = workbook.createSheet("Danh sách Sản phẩm"); // Tạo sheet

            // --- Định dạng Style (Tùy chọn) ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0\" VNĐ\"")); // Định dạng tiền tệ
            currencyStyle.setBorderBottom(BorderStyle.THIN);
            currencyStyle.setBorderLeft(BorderStyle.THIN);
            currencyStyle.setBorderRight(BorderStyle.THIN);

            CellStyle defaultStyle = workbook.createCellStyle();
            defaultStyle.setBorderBottom(BorderStyle.THIN);
            defaultStyle.setBorderLeft(BorderStyle.THIN);
            defaultStyle.setBorderRight(BorderStyle.THIN);
            // --- Kết thúc Style ---

            // 4. Tạo Header Row
            String[] headers = {"ID", "Tên Sản Phẩm", "Mô Tả", "Giá Bán", "Giá Nhập", "Tồn Kho", "Danh Mục ", "Đường Dẫn Ảnh"};
            Row headerRow = sheet.createRow(0); // Dòng đầu tiên (index 0)
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle); // Áp dụng style header
            }

            // 5. Ghi dữ liệu sản phẩm vào các dòng tiếp theo
            int rowNum = 1; // Bắt đầu từ dòng thứ 2 (index 1)
            for (Product p : productList) {
                Row row = sheet.createRow(rowNum++);

                Cell cellId = row.createCell(0);
                cellId.setCellValue(p.getId());
                cellId.setCellStyle(defaultStyle);
                Cell cellName = row.createCell(1);
                cellName.setCellValue(p.getName());
                cellName.setCellStyle(defaultStyle);
                Cell cellDesc = row.createCell(2);
                cellDesc.setCellValue(p.getDescription());
                cellDesc.setCellStyle(defaultStyle);
                Cell cellPrice = row.createCell(3);
                cellPrice.setCellValue(p.getPrice());
                cellPrice.setCellStyle(currencyStyle); // Style tiền tệ
                Cell cellCost = row.createCell(4);
                cellCost.setCellValue(p.getCostPrice());
                cellCost.setCellStyle(currencyStyle); // Style tiền tệ
                Cell cellStock = row.createCell(5);
                cellStock.setCellValue(p.getStock());
                cellStock.setCellStyle(defaultStyle);
                Cell cellCat = row.createCell(6);
                cellCat.setCellValue(p.getCategoryName());
                cellCat.setCellStyle(defaultStyle);
                Cell cellImg = row.createCell(7);
                cellImg.setCellValue(p.getImage());
                cellImg.setCellStyle(defaultStyle);
            }

            // Tự động điều chỉnh độ rộng cột (tùy chọn, có thể tốn hiệu năng nếu nhiều dữ liệu)
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 6. Thiết lập Response Headers để trình duyệt tải file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // MIME type cho .xlsx
            response.setHeader("Content-Disposition", "attachment; filename=\"DanhSachSanPham.xlsx\""); // Tên file đề xuất

            // 7. Ghi Workbook vào Response Output Stream
            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }

            // 8. Đóng Workbook (quan trọng để giải phóng tài nguyên)
            workbook.close();
            System.out.println("DEBUG (ExportExcel): Exported product list successfully.");

        } catch (Exception e) {
            System.err.println("ERROR exporting products to Excel: " + e.getMessage());
            e.printStackTrace();
            // Có thể hiển thị trang lỗi hoặc thông báo lỗi
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body><h2>Lỗi khi xuất file Excel</h2><p>" + e.getMessage() + "</p></body></html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet to export product data to an Excel (.xlsx) file using Apache POI.";
    }
}
