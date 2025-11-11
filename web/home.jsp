<%-- 
    Document   : home
    Created on : Oct 17, 2025, 4:23:01 PM
    Author     : THAI
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp" />


<main class="content">
    <%-- Thay thế tiêu đề cứng bằng biến pageTitle --%>
    <h2 class="title">${pageTitle != null ? pageTitle : 'SẢN PHẨM NỔI BẬT'}</h2>
    <div class="product-grid">
        <c:forEach items="${productList}" var="p">
            <div class="product-item">
                <a href="detail?pid=${p.id}">
                    <img src="<c:url value='/${p.image}'/>" alt="<c:out value="${p.name}"/>">
                    <h3 class="product-name"><c:out value="${p.name}"/></h3>
                    <p class="product-price">${p.formattedPrice}</p>
                </a>
                <a href="cart?action=add&id=${p.id}" class="add-to-cart-btn">Thêm vào giỏ</a>
            </div>
        </c:forEach>
    </div>
</main>

<jsp:include page="footer.jsp" />