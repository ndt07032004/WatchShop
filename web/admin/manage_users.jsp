<%-- 
    Document   : manage_users
    Created on : Oct 22, 2025, 6:33:01 PM
    Author     : THAI
--%>

<%-- /admin/manage_users.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set các biến cho admin_header.jsp --%>
<% request.setAttribute("pageTitle", "Quản lý Người dùng"); %>
<% request.setAttribute("activePage", "users"); %> <%-- Tên này cần khớp với link trong header --%>

<jsp:include page="admin_header.jsp" />

<main class="admin-content">
    <h2 class="title">Quản lý Tài khoản Người dùng</h2>

    <%-- Hiển thị thông báo thành công/lỗi từ session --%>
    <c:if test="${not empty sessionScope.adminUserSuccess}">
        <p class="admin-message success">${sessionScope.adminUserSuccess}</p>
        <c:remove var="adminUserSuccess" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.adminUserError}">
        <p class="admin-message error">${sessionScope.adminUserError}</p>
        <c:remove var="adminUserError" scope="session" />
    </c:if>

    <c:if test="${empty requestScope.userList}">
        <p>Không có người dùng nào trong hệ thống.</p>
    </c:if>

    <c:if test="${not empty requestScope.userList}">
        <table class="admin-table data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên đăng nhập</th>
                    <th>Họ và Tên</th>
                    <th>Email</th>
                    <th>SĐT</th>
                    <th>Địa chỉ</th>
                    <th>Vai trò</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${requestScope.userList}" var="user">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.fullname}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                        <td><small>${user.address}</small></td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 1}"> <span style="color: red; font-weight: bold;">Admin</span> </c:when>
                                <c:otherwise> Khách hàng </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <%-- Chỉ hiển thị nút Xóa nếu không phải là user ID 1 và không phải chính admin đang đăng nhập --%>
                            <c:if test="${user.id != 1 && user.id != sessionScope.account.id}">
                                <%-- Form ẩn để gửi yêu cầu xóa --%>
                                <form action="<c:url value='/admin/manage-users'/>" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa người dùng [${user.username}] không? Hành động này không thể hoàn tác!');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="action-btn delete-btn">Xóa</button> <%-- Dùng class CSS admin --%>
                                </form>

                                <%-- (Tùy chọn) Form/Link để đổi vai trò --%>
                             
                                <form action="<c:url value='/admin/manage-users'/>" method="post" style="display: inline; margin-left: 5px;">
                                    <input type="hidden" name="action" value="updateRole">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <select name="newRole" onchange="this.form.submit()">
                                        <option value="0" ${user.role == 0 ? 'selected' : ''}>Khách</option>
                                        <option value="1" ${user.role == 1 ? 'selected' : ''}>Admin</option>
                                    </select>
                                </form>
                               
                            </c:if>
                            <c:if test="${user.id == 1 || user.id == sessionScope.account.id}">
                                <i style="color: grey;">(Không thể xóa)</i>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</main>

<jsp:include page="admin_footer.jsp" />
