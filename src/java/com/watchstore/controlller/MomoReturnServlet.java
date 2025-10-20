package com.watchstore.controlller;

import com.watchstore.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Simple return handler. MoMo sẽ redirect người dùng về đây sau trả tiền.
 * Query params include: orderId, resultCode, message, requestId, amount, transId
 */
@WebServlet(name = "MomoReturnServlet", urlPatterns = {"/payment/momo-return"})
public class MomoReturnServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        String resultCode = request.getParameter("resultCode");
        String message = request.getParameter("message");

        if ("0".equals(resultCode)) {
            // payment success - bạn có thể cập nhật order status trong DB
            // Nếu bản schema có order id mapping, tìm và cập nhật trạng thái
            try {
                int oid = -1;
                try {
                    oid = Integer.parseInt(orderId);
                } catch (Exception ex) {
                    // orderId có thể là UUID hoặc string - bỏ qua nếu không phải int
                }
                if (oid > 0) {
                    OrderDAO orderDAO = new OrderDAO();
                    orderDAO.updateOrderStatus(oid, "Đã thanh toán");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            request.setAttribute("status", "success");
            request.setAttribute("message", "Thanh toán thành công. " + message);
        } else {
            request.setAttribute("status", "fail");
            request.setAttribute("message", "Thanh toán không thành công. " + message);
        }
        request.getRequestDispatcher("/momo_return.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}