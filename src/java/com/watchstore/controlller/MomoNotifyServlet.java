package com.watchstore.controlller;

import com.watchstore.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Endpoint để MoMo POST notify (IPN). Phải xác thực signature trước khi chấp nhận.
 * (Bạn cần triển khai hmac verification theo doc MoMo và đọc secret từ config)
 */
@WebServlet(name="MomoNotifyServlet", urlPatterns = {"/payment/momo-notify"})
public class MomoNotifyServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String body = new BufferedReader(request.getReader()).lines().collect(Collectors.joining(System.lineSeparator()));
        // TODO: parse JSON, verify signature with SECRET_KEY
        // Nếu hợp lệ: tìm order mapping và update status
        // Ví dụ (pseudocode):
        // String orderId = parsed.get("orderId");
        // String resultCode = parsed.get("resultCode");
        // if ("0".equals(resultCode)) orderDAO.updateOrderStatus(dbOrderId, "Đã thanh toán");
        response.setStatus(200);
        response.getWriter().write("{\"status\":\"ok\"}");
    }
}