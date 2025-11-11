<%-- 
    Document   : contact
    Created on : Oct 17, 2025, 4:22:25 PM
    Author     : THAI
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<jsp:include page="top_menu.jsp" />
<main class="content">
    <div class="form-container">
        <h2 class="title1">Liên Hệ Với Chúng Tôi</h2>
        <a style="color:red; text-align: center;">${requestScope.error}</a>
        <a style="color:green; text-align: center;">${requestScope.success}</a>
        <form action="<c:url value='/contact'/>" method="post"> 
            <div class="form-group"><label>Họ và Tên (*):</label><input type="text" name="name" required></div>
            <div class="form-group"><label>Email (*):</label><input type="email" name="email" required></div>
            <div class="form-group"><label>Nội dung (*):</label><textarea name="message" rows="5" required></textarea></div>
            <button type="submit" class="submit-btn">Gửi Liên Hệ</button>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />