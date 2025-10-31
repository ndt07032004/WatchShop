/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.watchstore.controlller;

import com.watchstore.dao.OrderDAO;
import com.watchstore.dao.ProductDAO;
import com.watchstore.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author THAI
 */
@WebServlet(name = "StatisticsServlet", urlPatterns = {"/admin/statistics"})
public class StatisticsServlet extends HttpServlet {

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
            out.println("<title>Servlet StatisticsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StatisticsServlet at " + request.getContextPath() + "</h1>");
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
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            OrderDAO orderDAO = new OrderDAO();
            ProductDAO productDAO = new ProductDAO(); // ⭐ Khởi tạo ProductDAO

            // 2. Lấy dữ liệu thống kê từ DAO
            double totalRevenue = orderDAO.getTotalRevenue();         // Tổng doanh thu (Giá bán)
            double totalCOGS = orderDAO.getTotalCostOfGoodsSold();    // ⭐ Tổng vốn (Giá nhập hàng đã bán)
            double totalInventoryValue = productDAO.getTotalInventoryValue(); // ⭐ Tổng giá trị tồn kho (theo giá nhập)
            double estimatedProfit = totalRevenue - totalCOGS;        // ⭐ Tính lợi nhuận = Doanh thu - Vốn

            // Lấy số lượng đơn hàng (giữ nguyên)
            int totalOrders = orderDAO.countOrdersByStatus(null);
            int pendingOrders = orderDAO.countOrdersByStatus("Chưa thanh toán");
            int processingOrders = orderDAO.countOrdersByStatus("Đang xử lý");
            int shippingOrders = orderDAO.countOrdersByStatus("Đang giao hàng");
            int completedOrders = orderDAO.countOrdersByStatus("Đã giao thành công");
            int cancelledOrders = orderDAO.countOrdersByStatus("Đã hủy");
            int totalCustomers = orderDAO.countCustomers();

            // 3. Đặt dữ liệu vào request attributes
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("totalCOGS", totalCOGS);             // ⭐ Gửi Tổng vốn
            request.setAttribute("estimatedProfit", estimatedProfit); // ⭐ Gửi Lợi nhuận
            request.setAttribute("totalInventoryValue", totalInventoryValue); // ⭐ Gửi Giá trị tồn kho

            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("shippingOrders", shippingOrders);
            request.setAttribute("completedOrders", completedOrders);
            request.setAttribute("cancelledOrders", cancelledOrders);
            request.setAttribute("totalCustomers", totalCustomers);

            // 4. Set thuộc tính cho header admin
            request.setAttribute("pageTitle", "Thống kê Bán hàng");
            request.setAttribute("activePage", "statistics");

            // 5. Forward đến trang JSP
            System.out.println("DEBUG (StatisticsServlet): Forwarding to /admin/statistics.jsp");
            request.getRequestDispatcher("/admin/statistics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("ERROR in StatisticsServlet doGet:");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu thống kê: " + e.getMessage());
            request.getRequestDispatcher("/admin/error_admin.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet to fetch and display sales statistics including COGS and profit."; // Cập nhật mô tả
    }

}
