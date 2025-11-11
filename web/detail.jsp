<%-- 
    Document   : detail
    Created on : Oct 17, 2025, 4:22:33 PM
    Author     : THAI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="top_menu.jsp" />

<main class="content">
    <main class="content2">
        <c:if test="${product != null}">
            <div class="product-detail">
                <div class="product-detail-image">
                    <img src="<c:url value='/${product.image}'/>" alt="<c:out value="${product.name}"/>">
                </div>
                <div class="product-detail-info">
                    <h1 class="title"><c:out value="${product.name}"/></h1>
                    <p class="product-price-detail">${product.formattedPrice}</p>
                    <p class="stock-status">Tình trạng: <strong>Còn hàng (<c:out value="${product.stock}"/> sản phẩm)</strong></p>
                    <hr>
                    <h3>Mô tả sản phẩm</h3>
                    <p><c:out value="${product.description}"/></p>
                    <a href="cart?action=add&id=${product.id}" class="add-to-cart-btn-large">Thêm vào giỏ hàng</a>
                </div>
            </div>
        </c:if>
        <c:if test="${product == null}">
            <h2>Sản phẩm không tồn tại!</h2>
        </c:if>
    </main>
</main>

<jsp:include page="footer.jsp" />
