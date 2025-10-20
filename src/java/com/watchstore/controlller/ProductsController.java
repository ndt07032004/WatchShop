/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.watchstore.controlller;

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.ProductDAO;
import com.watchstore.model.Category;
import com.watchstore.model.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author THAI
 */
@WebServlet(name = "ProductsServlet", urlPatterns = {"/products"})
public class ProductsController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
        

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        
        // ********************** LOGIC LỌC GIÁ **********************
        String priceRange_raw = request.getParameter("priceRange");
        double minPrice = 0;
        double maxPrice = 0;
        
        if (priceRange_raw != null && !priceRange_raw.isEmpty()) {
            try {
                // Ví dụ: priceRange=1000000-5000000 hoặc priceRange=10000000-0
                String[] parts = priceRange_raw.split("-");
                if (parts.length > 0) {
                     minPrice = Double.parseDouble(parts[0]);
                }
                
                // Kiểm tra phần tử thứ hai cho maxPrice
                if (parts.length > 1) {
                    // Nếu là "0" (ví dụ: 10000000-0), nghĩa là "trên mức" -> maxPrice = 0 (sẽ được xử lý trong DAO)
                    if (!parts[1].equals("0")) { 
                        maxPrice = Double.parseDouble(parts[1]);
                    }
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu giá trị không hợp lệ
            }
        }
        
        List<Product> productList = productDAO.getProductsByPriceRange(0, minPrice, maxPrice);

        // 2. Lấy danh mục (Cần cho left menu)
        List<Category> categoryList = categoryDAO.getAllCategories();

        // 3. Gửi dữ liệu qua trang JSP
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("activePage", "products"); // Sửa lỗi menu: đảm bảo menu Sản phẩm active
        request.setAttribute("activePriceRange", priceRange_raw); // Để đánh dấu active cho menu lọc giá
        request.setAttribute("pageTitle", "TẤT CẢ SẢN PHẨM"); 

       
        request.getRequestDispatcher("Products.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
