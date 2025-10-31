<%-- 
    Document   : bank_payment
    Created on : Jul 16, 2025, 8:45:00 PM
    Author     : PC
--%>

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
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .container {
            max-width: 600px;
            padding: 30px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        h2 {
            color: #2d3436;
            margin-bottom: 20px;
        }

        p {
            font-size: 16px;
            color: #2d3436;
        }

        ul {
            text-align: left;
            display: inline-block;
            margin: 10px auto;
            font-size: 16px;
        }

        .qr {
            margin: 20px 0;
        }

        .note {
            font-weight: bold;
            margin-top: 20px;
            color: #d63031;
        }

        a {
            display: block;
            margin-top: 25px;
            text-decoration: none;
            color: #0984e3;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>💳 Thanh toán chuyển khoản ngân hàng</h2>
    <p>Xin chào, <strong><%= hoTen != null ? hoTen : "Khách hàng" %></strong></p>
    <p>Vui lòng chuyển khoản đến:</p>
    <ul>
        <li><strong>Ngân hàng:</strong> Vietcombank</li>
        <li><strong>Số tài khoản:</strong> 10207032004</li>
        <li><strong>Chủ tài khoản:</strong> NGUYEN DANH THAI</li>
    </ul>

    <div class="qr">
        <img src="media/vcb_qr.jpg" alt="QR Vietcombank" width="200">
    </div>

    <p class="note">Ghi chú chuyển khoản: <strong>Tên + SĐT</strong></p>
    <p>Sau khi chuyển khoản, shop sẽ liên hệ xác nhận!</p>

    <a href="user.jsp">← Quay về cửa hàng</a>
</div>
</body>
</html>