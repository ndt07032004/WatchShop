package com.watchstore.controlller;

import com.watchstore.payment.MomoPayment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet tạo request thanh toán tới MoMo và redirect người dùng tới MoMo payUrl.
 * Nó nhận các tham số: amount (số tiền), orderId (nếu có) hoặc tạo mới.
 */
@WebServlet(name = "MomoPaymentServlet", urlPatterns = {"/payment/momo"})
public class MomoPaymentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        try {
            String amount = request.getParameter("amount");
            if (amount == null || amount.isEmpty()) amount = "1000"; // fallback
            String orderId = request.getParameter("orderId");
            if (orderId == null || orderId.isEmpty()) orderId = UUID.randomUUID().toString();
            String orderInfo = "Thanh toan don hang " + orderId;

            String redirectUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/payment/momo-return";
            String ipnUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/payment/momo-notify";

            String payUrl = MomoPayment.createPayment(orderId, amount, orderInfo, redirectUrl, ipnUrl);
            // redirect user to MoMo payment page
            response.sendRedirect(payUrl);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi tạo payment: " + e.getMessage());
            request.getRequestDispatcher("/momo_return.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // optional: show a simple page or redirect to checkout
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
}