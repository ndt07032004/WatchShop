package com.watchstore.controlller;

import com.watchstore.dao.OrderDAO;
import com.watchstore.model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/orders","/admin/changeOrderStatus"})
public class AdminOrderServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        OrderDAO orderDAO = new OrderDAO();
        if ("/admin/orders".equals(path)) {
            List<Order> orders = orderDAO.getAllOrders();
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/admin_order.jsp").forward(request, response);
            return;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("changeStatus".equals(action)) {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = request.getParameter("status");
            OrderDAO orderDAO = new OrderDAO();
            orderDAO.updateOrderStatus(orderId, status);
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } else {
            doGet(request, response);
        }
    }
}