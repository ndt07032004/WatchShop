<%-- 
    Document   : left_menu
    Created on : Oct 17, 2025, 4:23:10 PM
    Author     : THAI
--%>

<%-- left_menu.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="left-menu">
    <h3 class="title">DANH MỤC SẢN PHẨM</h3>
    <ul>
        <%-- Lặp qua danh sách category --%>
        <c:forEach items="${categoryList}" var="cat">
            <li class="${activeCid eq cat.id ? 'active' : ''}"><a href="category?cid=${cat.id}">${cat.name}</a></li>
        </c:forEach>
    </ul>
    
    <%-- >>> THÊM LỌC GIÁ Ở ĐÂY <<< --%>
    <h3 class="title" style="margin-top: 20px;">LỌC THEO GIÁ</h3>
    <ul class="price-filter">
        <li><a href="products">Tất cả</a></li>
        <li><a href="products?priceRange=0-1000000">Dưới 1 triệu</a></li>
        <li><a href="products?priceRange=1000000-5000000">1 triệu - 5 triệu</a></li>
        <li><a href="products?priceRange=5000000-10000000">5 triệu - 10 triệu</a></li>
        <li><a href="products?priceRange=10000000-0">Trên 10 triệu</a></li>
    </ul>
    
</aside>
