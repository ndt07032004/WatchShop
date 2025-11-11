package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.UserDAO;
import com.watchstore.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/manage-users"}) // URL trong thư mục admin
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 2. Lấy danh sách user từ DAO
            UserDAO userDAO = new UserDAO();
            List<User> userList = userDAO.getAllUsers(); // Dùng hàm mới

            // 3. Gửi danh sách sang JSP
            request.setAttribute("userList", userList);
            request.setAttribute("pageTitle", "Quản lý Người dùng");
            request.setAttribute("activePage", "users"); // Đánh dấu menu active

            // 4. Forward
            request.getRequestDispatcher("/admin/manage_users.jsp").forward(request, response); // Trỏ đến JSP trong /admin/

        } catch (Exception e) {
            System.err.println("ERROR in AdminUserServlet doGet:");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải danh sách người dùng: " + e.getMessage());
            request.getRequestDispatcher("/admin/error_admin.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Lấy action và userId
        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");
        String message = "";
        boolean error = false;

        System.out.println("DEBUG (Admin User POST): Action=" + action + ", UserID=" + userIdStr); // Log

        if (action != null && userIdStr != null) {
            try {
                int userId = Integer.parseInt(userIdStr);
                UserDAO userDAO = new UserDAO();

                // --- Xử lý Xóa User ---
                if ("delete".equals(action)) {
                    // Ngăn chặn xóa chính mình hoặc admin gốc (ví dụ: ID 1)
                    if (userId == adminUser.getId()) {
                        message = "Lỗi: Không thể xóa chính tài khoản đang đăng nhập!";
                        error = true;
                        System.err.println("WARN: Admin tried to delete self (ID: " + userId + ")");
                    } else if (userId == 1) { // Giả sử ID 1 là super admin không thể xóa
                        message = "Lỗi: Không thể xóa tài khoản Super Admin (ID: 1)!";
                        error = true;
                         System.err.println("WARN: Attempt to delete Super Admin (ID: 1) blocked.");
                    }
                    else if (userDAO.deleteUser(userId)) { // Gọi hàm deleteUser
                        message = "Xóa người dùng #" + userId + " thành công!";
                    } else {
                        message = "Xóa người dùng #" + userId + " thất bại! (Có thể do lỗi CSDL hoặc khóa ngoại)";
                        error = true;
                    }
                }
                // --- Xử lý Cập nhật Role (Nếu có nút/form cho việc này) ---
                else if ("updateRole".equals(action)) {
                    String newRoleStr = request.getParameter("newRole");
                    if (newRoleStr != null) {
                         try {
                              int newRole = Integer.parseInt(newRoleStr);
                              // Thêm kiểm tra tương tự như khi xóa
                              if (userId == adminUser.getId() || userId == 1) {
                                   message = "Lỗi: Không thể thay đổi vai trò của tài khoản này!";
                                   error = true;
                              }
                              else if(userDAO.updateUserRole(userId, newRole)){
                                   message = "Cập nhật vai trò cho người dùng #" + userId + " thành công!";
                              } else {
                                   message = "Cập nhật vai trò thất bại!";
                                   error = true;
                              }
                         } catch (NumberFormatException ne) {
                              message = "Lỗi: Vai trò mới không hợp lệ.";
                              error = true;
                         }
                    } else {
                         message = "Lỗi: Thiếu thông tin vai trò mới.";
                         error = true;
                    }
                }
                 else {
                    message = "Lỗi: Hành động không được hỗ trợ.";
                    error = true;
                }

            } catch (NumberFormatException e) {
                message = "Lỗi: ID người dùng không hợp lệ (" + userIdStr + ").";
                error = true;
            } catch (Exception e) {
                message = "Lỗi hệ thống khi xử lý. Xem log server.";
                error = true;
                e.printStackTrace();
            }
        } else {
            message = "Lỗi: Yêu cầu không hợp lệ (thiếu action hoặc userId).";
            error = true;
        }

        // 3. Đặt thông báo vào session
        if (error) {
            session.setAttribute("adminUserError", message);
        } else {
            session.setAttribute("adminUserSuccess", message);
        }

        // 4. Redirect lại trang quản lý user
        response.sendRedirect(request.getContextPath() + "/admin/manage-users");
    }

    @Override
    public String getServletInfo() {
        return "Admin servlet for managing user accounts (view list, delete, update role).";
    }
}