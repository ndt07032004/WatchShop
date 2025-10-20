<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kết quả thanh toán MoMo</title>
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-4">
    <h2>Kết quả thanh toán</h2>
    <c:choose>
        <c:when test="${status == 'success'}">
            <div class="alert alert-success">${message}</div>
            <a href="/" class="btn btn-primary">Về trang chủ</a>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger">${message}</div>
            <a href="/checkout.jsp" class="btn btn-secondary">Quay lại thanh toán</a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>