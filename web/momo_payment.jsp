<%-- 
    Document   : momo_payment
    Created on : Jul 16, 2025, 8:48:43 PM
    Author     : PC
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    String hoTen = (String) session.getAttribute("hoTen");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán chuyển khoản</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background-color: #f9f9f9;
            margin: 0;
        }
        main.content {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .form-container {
            max-width: 550px;
            margin: 40px auto;
            padding: 40px 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            text-align: center;
        }
        h2.title {
            color: #2c3e50;
            font-size: 24px;
            margin-bottom: 10px;
        }
        p {
            color: #555;
            line-height: 1.6;
            margin-bottom: 10px;
        }
        ul {
            text-align: left;
            display: inline-block;
            margin: 15px auto;
            font-size: 16px;
            color: #2c3e50;
        }
        .qr img {
            margin: 20px 0;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .note {
            font-weight: bold;
            margin-top: 20px;
            color: #d63031;
        }
        .submit-btn {
            display: inline-block;
            background-color: #3498db;
            color: white;
            padding: 10px 18px;
            border-radius: 6px;
            text-decoration: none;
            transition: 0.3s;
        }
        .submit-btn:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
    </style>
</head>

<body>
<main class="content">
    <div class="form-container">
        <div style="font-size: 50px; color: #e84393; margin-bottom: 15px;">💳</div>
        <h2 class="title">Thanh toán chuyển khoản qua Ví Momo</h2>

        <p>Xin chào, <strong><%= hoTen != null ? hoTen : "Khách hàng" %></strong></p>
        <p>Vui lòng chuyển khoản đến:</p>

        <ul>
            <li><strong>Số điện thoại:</strong> 097205514*</li>
            <li><strong>Chủ tài khoản:</strong> Nguyên Danh THÁI</li>
        </ul>

        <div class="qr">
            <img src="images/momo_qr.jpg" alt="QR Momo" width="200">
        </div>

        <p class="note">Ghi chú chuyển khoản: <strong>Tên + SĐT</strong></p>
        <p>Sau khi chuyển khoản, shop sẽ liên hệ xác nhận đơn hàng của bạn.</p>

        <div style="margin-top: 25px;">
            <a href="<c:url value='/home'/>" class="submit-btn">🏠 Tiếp tục mua sắm</a>

        </div>
    </div>
</main>
</body>
</html>
