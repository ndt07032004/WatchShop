package com.watchstore.controlller;

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.ProductDAO;
import com.watchstore.model.Category;
import com.watchstore.model.Product;
import com.watchstore.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "ManageProductsServlet", urlPatterns = {"/admin/manage-products"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class ManageProductsServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images"; // Thư mục lưu ảnh (tương đối)

    // --- doGet: Xử lý hiển thị danh sách, form sửa, xóa ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        try {
            // --- XỬ LÝ XÓA SẢN PHẨM ---
            if ("delete".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Product productToDelete = productDAO.getProductById(id);
                    if (productToDelete != null && productDAO.deleteProduct(id)) { // Gọi hàm delete DAO
                        deleteImageFile(productToDelete.getImage(), request); // Xóa ảnh
                        session.setAttribute("adminProductSuccess", "Đã xóa sản phẩm #" + id + " thành công!");
                    } else {
                        session.setAttribute("adminProductError", "Lỗi khi xóa sản phẩm #" + id + ". Có thể do sản phẩm không tồn tại hoặc lỗi CSDL.");
                    }
                } else {
                     session.setAttribute("adminProductError", "Thiếu ID sản phẩm để xóa.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/manage-products"); // Redirect lại list
                return;
            }
            // --- XỬ LÝ HIỂN THỊ FORM SỬA ---
            else if ("edit".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Product product = productDAO.getProductById(id);
                    List<Category> categories = categoryDAO.getAllCategories(); // Lấy danh mục
                    if (product != null) {
                        request.setAttribute("product", product); // Gửi sản phẩm sang form
                        request.setAttribute("categoryListForForm", categories); // Gửi danh mục
                        request.getRequestDispatcher("/admin/add-edit-product.jsp").forward(request, response); // Forward tới form
                        return;
                    }
                }
                // Nếu ID lỗi hoặc không tìm thấy
                session.setAttribute("adminProductError", "Không tìm thấy sản phẩm để sửa hoặc ID không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/admin/manage-products");
                return;
            }

            // --- MẶC ĐỊNH: HIỂN THỊ DANH SÁCH SẢN PHẨM ---
            List<Product> productList = productDAO.getAllProducts();
            request.setAttribute("productList", productList);
            request.setAttribute("pageTitle", "Quản lý Sản phẩm");
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/admin/manage-products.jsp").forward(request, response);

        } catch (NumberFormatException e) {
             session.setAttribute("adminProductError", "ID sản phẩm không hợp lệ.");
             response.sendRedirect(request.getContextPath() + "/admin/manage-products");
        } catch (Exception e) {
            System.err.println("ERROR in ManageProductsServlet doGet:");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải dữ liệu sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/admin/error_admin.jsp").forward(request, response);
        }
    }


    // --- doPost: Xử lý THÊM MỚI hoặc LƯU CHỈNH SỬA ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Lấy thông tin từ form
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String costPriceStr = request.getParameter("costPrice");
        String stockStr = request.getParameter("stock");
        String categoryIdStr = request.getParameter("categoryId");
        String currentImage = request.getParameter("currentImage");

        String imagePath = currentImage; // Giữ ảnh cũ mặc định nếu sửa
        boolean isEdit = (idStr != null && !idStr.isEmpty());
        // Đường dẫn tuyệt đối đến thư mục images trong thư mục gốc của webapp đã deploy
        String uploadPath = request.getServletContext().getRealPath("/" + UPLOAD_DIR);

        System.out.println("\n--- ManageProductsServlet POST Start ---");
        System.out.println("   Received data: id=" + idStr + ", name=" + name + ", price=" + priceStr + ", costPrice=" + costPriceStr + ", stock=" + stockStr + ", categoryId=" + categoryIdStr + ", currentImage=" + currentImage);
        System.out.println("   Upload directory real path: " + uploadPath);

        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                 System.err.println("   FATAL: Could not create upload directory: " + uploadPath);
                 session.setAttribute("adminProductError", "Lỗi nghiêm trọng: Không thể tạo thư mục lưu ảnh.");
                 response.sendRedirect(request.getContextPath() + "/admin/manage-products");
                 return;
            } else {
                 System.out.println("   Created upload directory: " + uploadPath);
            }
        }

        try {
            // 4. Xử lý file ảnh upload
            Part filePart = request.getPart("imageFile");
            String originalFileName = getFileName(filePart);

            if (originalFileName != null && !originalFileName.isEmpty()) {
                System.out.println("   Processing uploaded file: " + originalFileName);
                String fileExtension = "";
                int dotIndex = originalFileName.lastIndexOf('.');
                if (dotIndex >= 0) fileExtension = originalFileName.substring(dotIndex);
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + uniqueFileName; // Đường dẫn tuyệt đối để lưu

                try (InputStream fileContent = filePart.getInputStream()) {
                    Files.copy(fileContent, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                    imagePath = UPLOAD_DIR + "/" + uniqueFileName; // Đường dẫn tương đối lưu vào DB
                    System.out.println("   >> Saved new image to: " + filePath);
                    System.out.println("   >> DB image path set to: " + imagePath);
                    if (isEdit && currentImage != null && !currentImage.isEmpty()) {
                        deleteImageFile(currentImage, request); // Xóa ảnh cũ
                    }
                 } catch (IOException e) { throw new ServletException("Lỗi khi lưu file ảnh.", e); }
            } else { // Không có file mới
                 System.out.println("   No new image file uploaded.");
                 if (!isEdit) imagePath = null; // Thêm mới không ảnh -> null (hoặc ảnh mặc định)
                 System.out.println("   >> Keeping/Setting image path: " + imagePath);
            }

            // 5. Chuyển đổi và kiểm tra dữ liệu số
            double price = 0;
            double costPrice = 0;
            int stock = 0;
            int categoryId = 0;
            String cleanedPriceStr, cleanedCostPriceStr, cleanedStockStr, cleanedCategoryIdStr;

            // Xử lý Price
            cleanedPriceStr = (priceStr != null) ? priceStr.trim().replaceAll("[^\\d.]", "") : "";
            if (cleanedPriceStr.isEmpty()) throw new ServletException("Giá bán không được để trống.");
            try { price = Double.parseDouble(cleanedPriceStr); }
            catch (NumberFormatException e) { throw new ServletException("Định dạng Giá bán không hợp lệ."); }

            // Xử lý Cost Price
            cleanedCostPriceStr = (costPriceStr != null) ? costPriceStr.trim().replaceAll("[^\\d.]", "") : "";
            if (cleanedCostPriceStr.isEmpty()) throw new ServletException("Giá nhập không được để trống.");
            try { costPrice = Double.parseDouble(cleanedCostPriceStr); }
            catch (NumberFormatException e) { throw new ServletException("Định dạng Giá nhập không hợp lệ."); }

            // Xử lý Stock
            cleanedStockStr = (stockStr != null) ? stockStr.trim().replaceAll("[^\\d]", "") : "";
             if (cleanedStockStr.isEmpty()) throw new ServletException("Số lượng tồn kho không được để trống.");
             try { stock = Integer.parseInt(cleanedStockStr); }
             catch (NumberFormatException e) { throw new ServletException("Định dạng Số lượng tồn kho không hợp lệ."); }

            // Xử lý Category ID
            cleanedCategoryIdStr = (categoryIdStr != null) ? categoryIdStr.trim().replaceAll("[^\\d]", "") : "";
            if (cleanedCategoryIdStr.isEmpty()) throw new ServletException("Vui lòng chọn danh mục.");
             try { categoryId = Integer.parseInt(cleanedCategoryIdStr); }
             catch (NumberFormatException e) { throw new ServletException("Định dạng ID Danh mục không hợp lệ."); }

            // Kiểm tra giá trị âm
            if (price < 0 || costPrice < 0 || stock < 0) {
                throw new ServletException("Giá và tồn kho không được âm.");
            }

            // 6. Tạo đối tượng Product
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCostPrice(costPrice);
            product.setStock(stock);
            product.setCategoryId(categoryId);
            product.setImage(imagePath);

            // 7. Gọi DAO
            ProductDAO productDAO = new ProductDAO();
            boolean success = false;
            String actionMessage = "";

            if (isEdit) {
                product.setId(Integer.parseInt(idStr));
                success = productDAO.updateProduct(product); // Gọi hàm DAO boolean
                actionMessage = "cập nhật";
            } else {
                success = productDAO.addProduct(product); // Gọi hàm DAO boolean
                actionMessage = "thêm mới";
            }

            // 8. Đặt thông báo
            if (success) {
                session.setAttribute("adminProductSuccess", "Đã " + actionMessage + " sản phẩm '" + name + "' thành công!");
                System.out.println("   >> Product " + actionMessage + " OK.");
            } else {
                // Lỗi DAO (bao gồm cả lỗi khóa ngoại) sẽ vào đây
                session.setAttribute("adminProductError", "Lỗi khi " + actionMessage + " sản phẩm '" + name + "'. Nguyên nhân có thể do ID Danh mục không tồn tại hoặc lỗi CSDL khác. Xem log server.");
                System.err.println("   ERROR: DAO operation (" + actionMessage + ") failed for product: " + name);
            }

        } catch (ServletException | IOException se) { // Bắt lỗi upload file và validation/parse
             System.err.println("   ERROR (Validation/Upload/Parse): " + se.getMessage());
             session.setAttribute("adminProductError", se.getMessage());
        } catch (Exception e) { // Bắt lỗi chung khác
            System.err.println("   ERROR processing product form POST: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("adminProductError", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
        } finally {
             System.out.println("--- ManageProductsServlet POST End ---");
        }

        // 9. Chuyển hướng về trang quản lý
        response.sendRedirect(request.getContextPath() + "/admin/manage-products");
    }

    // --- Hàm tiện ích để lấy tên file từ Part Header ---
    private String getFileName(Part part) {
        if (part == null) return null;
        String contentDisposition = part.getHeader("content-disposition");
        if(contentDisposition == null) return null;
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                 String fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                 // An toàn hơn khi dùng Paths.get().getFileName()
                 return Paths.get(fileName).getFileName().toString();
            }
        }
        return null;
    }

     // --- Hàm tiện ích để xóa file ảnh ---
    private void deleteImageFile(String imagePath, HttpServletRequest request) {
        if (imagePath == null || imagePath.isEmpty()) return;
        try {
            String applicationPath = request.getServletContext().getRealPath("");
            // Tạo đường dẫn tuyệt đối bằng Paths.get để tương thích HĐH
            String fullPath = Paths.get(applicationPath, imagePath.replace('/', File.separatorChar)).toString();
            File imageFile = new File(fullPath);
            if (imageFile.exists() && imageFile.isFile()) {
                if (imageFile.delete()) {
                    System.out.println("   Deleted old image file: " + fullPath);
                } else {
                    System.err.println("   Failed to delete old image file: " + fullPath);
                }
            } else {
                System.out.println("   Old image file not found: " + fullPath);
            }
        } catch (Exception e) {
             System.err.println("   Error deleting old image file ("+imagePath+"): " + e.getMessage());
        }
    }


    @Override
    public String getServletInfo() {
        return "Admin Servlet for managing products (add, edit with file upload, delete, list)";
    }
}