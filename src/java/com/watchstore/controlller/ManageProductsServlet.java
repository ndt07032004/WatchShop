package com.watchstore.controlller;

import com.watchstore.dao.ProductDAO;
import com.watchstore.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * ManageProductsServlet: hiển thị danh sách sản phẩm cho admin và xử lý lưu (thêm/sửa)
 */
@WebServlet(name = "ManageProductsServlet", urlPatterns = {"/admin/manage-products","/admin/saveProduct","/admin/deleteProduct","/admin/add-edit-product"})
@MultipartConfig(fileSizeThreshold = 1024*1024, maxFileSize = 5*1024*1024)
public class ManageProductsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        ProductDAO dao = new ProductDAO();
        if ("/admin/manage-products".equals(path)) {
            List<Product> list = dao.getAllProducts();
            request.setAttribute("productList", list);
            request.getRequestDispatcher("/manage-products.jsp").forward(request, response);
            return;
        }
        if ("/admin/add-edit-product".equals(path)) {
            String id = request.getParameter("id");
            if (id != null && !id.isEmpty()) {
                Product p = dao.getProductById(Integer.parseInt(id));
                request.setAttribute("product", p);
            }
            request.getRequestDispatcher("/add-edit-product.jsp").forward(request, response);
            return;
        }
        if ("/admin/deleteProduct".equals(path)) {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.deleteProduct(id);
            response.sendRedirect(request.getContextPath() + "/admin/manage-products");
            return;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // xử lý saveProduct
        request.setCharacterEncoding("utf-8");
        String name = request.getParameter("name");
        String desc = request.getParameter("description");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        Part imagePart = request.getPart("image");
        String fileName = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            String uploads = request.getServletContext().getRealPath("/images");
            File uploadsDir = new File(uploads);
            if (!uploadsDir.exists()) uploadsDir.mkdirs();
            fileName = System.currentTimeMillis() + "_" + imagePart.getSubmittedFileName();
            imagePart.write(uploads + File.separator + fileName);
        }
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStock(stock);
        if (fileName != null) p.setImage("images/" + fileName);

        ProductDAO dao = new ProductDAO();
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            dao.addProduct(p);
        } else {
            p.setId(Integer.parseInt(idStr));
            dao.updateProduct(p);
        }
        response.sendRedirect(request.getContextPath() + "/admin/manage-products");
    }
}