<%-- header.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Watch Store - Cửa Hàng Đồng Hồ</title>
        <link href="<c:url value='/css/style.css' />" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <nav class="top-menu">
            <ul>
                <li class="${activePage eq 'home' ? 'active' : ''}"><a href="<c:url value='/home'/>">Trang Chủ</a></li>
                <li class="${activePage eq 'products' || activePage eq 'category' ? 'active' : ''}">
                    <a href="products">Sản Phẩm</a>
                </li>
                <li class="${activePage eq 'cart' ? 'active' : ''}"><a href="<c:url value='/cart'/>">Giỏ Hàng</a></li>
                <li class="${activePage eq 'contact' ? 'active' : ''}"><a href="<c:url value='/contact'/>">Liên Hệ</a></li>

                <%-- LOGIC: Đăng Nhập / Đăng Xuất & Menu Hồ Sơ --%>
                <c:choose>
                    <c:when test="${sessionScope.account != null}">
                        <li class="dropdown ${activePage eq 'profile' ? 'active' : ''}">
                            <a href="javascript:void(0);" style="cursor: default;">
                                Chào, ${sessionScope.account.fullname}
                            </a>
                            <ul class="submenu">
                                <li><a href="<c:url value='/order-history'/>">Đơn hàng của tôi</a></li>
                                <li><a href="<c:url value='/edit-profile'/>">Hồ sơ cá nhân</a></li>
                                <li><a href="<c:url value='/logout'/>">Đăng Xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="${activePage eq 'login' ? 'active' : ''}"><a href="<c:url value='/login.jsp'/>">Đăng Nhập</a></li>
                        <li class="${activePage eq 'register' ? 'active' : ''}"><a href="<c:url value='/register.jsp'/>">Đăng Ký</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
        <div id="wrapper">
            <header>
                <div class="banner-slider">
                    <div class="banner-item active">
                        <img src="<c:url value='/images/banner.jpg'/>" alt="Banner 1" style="width:100%; height: 410px;"/>
                    </div>
                    <!--                    <div class="banner-item">
                                            <img src="<c:url value='/images/banner1.jpg'/>" alt="Banner 2" style="width:100%; height: auto;"/>
                                        </div>
                                        <div class="banner-item">
                                            <img src="<c:url value='/images/banner2.jpg'/>" alt="Banner 2" style="width:100%; height: auto;"/>
                                        </div>
                                        <div class="banner-item">
                                            <img src="<c:url value='/images/banner3.jpg'/>" alt="Banner 3" style="width:100%; height: auto;"/>
                                        </div>
                                        
                                        <a class="prev-btn">&#10094;</a> <a class="next-btn">&#10095;</a> </div>-->
                </div>
            </header>
            <div class="container">