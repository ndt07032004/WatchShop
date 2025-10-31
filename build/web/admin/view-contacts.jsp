<%-- 
    Document   : view-contacts
    Created on : Oct 23, 2025, 9:40:10 PM
    Author     : THAI
--%>

<%-- /admin/view-contacts.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set attributes needed by admin_header.jsp --%>
<% request.setAttribute("pageTitle", "Xem Liên Hệ Khách Hàng"); %>
<% request.setAttribute("activePage", "contacts"); %> <%-- Match the name set in servlet --%>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Danh Sách Liên Hệ Đã Nhận</h2>

    <%-- Display error message if any --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="admin-message error">${requestScope.errorMessage}</p>
    </c:if>

    <%-- Table to display contacts --%>
    <table class="admin-table data-table"> <%-- Use existing table styles --%>
        <thead>
            <tr>
                <th>ID</th>
                <th>Ngày Gửi</th>
                <th>Người Gửi</th>
                <th>Email</th>
                <th>Nội Dung Tin Nhắn</th>
                <%-- Optional: Add Action column if needed (e.g., Mark as Read, Delete) --%>
            </tr>
        </thead>
        <tbody>
            <%-- Check if list is empty --%>
            <c:if test="${empty contactList}">
                <tr>
                    <td colspan="5" style="text-align: center; padding: 20px;">Chưa có liên hệ nào được gửi.</td>
                </tr>
            </c:if>

            <%-- Loop through contacts --%>
            <c:forEach items="${contactList}" var="contact">
                <tr>
                    <td>${contact.id}</td>
                    <td>
                        <c:if test="${not empty contact.createdAt}">
                            <fmt:formatDate value="${contact.createdAt}" pattern="dd-MM-yyyy HH:mm"/>
                        </c:if>
                        <c:if test="${empty contact.createdAt}">N/A</c:if> <%-- Fallback if no date --%>
                    </td>
                    <td>${contact.name}</td>
                    <td><a href="mailto:${contact.email}">${contact.email}</a></td> <%-- Mailto link --%>
                    <td style="white-space: pre-wrap; word-break: break-word;">${contact.message}</td> <%-- Allow line breaks --%>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</main>

<jsp:include page="admin_footer.jsp" />

<%-- Optional specific styles --%>
<style>
    .data-table td:last-child {
        /* Adjust style for message column if needed */
        max-width: 400px; /* Limit width */
    }
</style>