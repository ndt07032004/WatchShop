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
@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy Category ID (cid) từ URL
        String cid_raw = request.getParameter("cid");
        int cid = 0; // Mặc định là ID 0 (Tất cả sản phẩm)

        try {
            if (cid_raw != null) {
                cid = Integer.parseInt(cid_raw);
            }
        } catch (NumberFormatException e) {
            // Giữ cid = 0 nếu tham số không hợp lệ
        }

        // 2. Lấy danh sách sản phẩm theo Category ID
        ProductDAO pDao = new ProductDAO();
        List<Product> productList = pDao.getProductsByCategoryId(cid);

        // 3. Lấy danh sách danh mục và Category ID đang active (để hiển thị menu)
        CategoryDAO cDao = new CategoryDAO();
        List<Category> categoryList = cDao.getAllCategories();
        String activeCategoryName = "SẢN PHẨM NỔI BẬT"; // Tên mặc định

// Tìm tên danh mục dựa trên cid
        for (Category cat : categoryList) {
            if (cat.getId() == cid) {
                activeCategoryName = cat.getName().toUpperCase();
                break;
            }
        }

// 4. Set dữ liệu vào request
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("activeCid", cid);
        request.setAttribute("activePage", "category");
        request.setAttribute("pageTitle", activeCategoryName); // <<< THÊM DÒNG NÀY

// 5. Chuyển tiếp đến trang hiển thị sản phẩm
        request.getRequestDispatcher("Products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
