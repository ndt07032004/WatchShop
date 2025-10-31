package com.watchstore.controlller;

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.UserDAO;
import com.watchstore.model.Category;
import com.watchstore.model.User;
import com.watchstore.util.PasswordUtil; // ⭐ Import
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EditProfileServlet", urlPatterns = {"/edit-profile"})
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Code doGet đã đúng)
         HttpSession session = request.getSession(); if (session.getAttribute("account") == null) { response.sendRedirect("login.jsp"); return; } CategoryDAO categoryDAO = new CategoryDAO(); List<Category> categoryList = categoryDAO.getAllCategories(); request.setAttribute("categoryList", categoryList); request.setAttribute("activePage", "profile"); request.getRequestDispatcher("edit_profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        String message = "";
        boolean error = false;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        try {
            if ("updateUserProfile".equals(action)) {
                // (Code updateInfo đã đúng)
                 String fullname = request.getParameter("fullname"); String email = request.getParameter("email"); String phone = request.getParameter("phone"); String address = request.getParameter("address"); user.setFullname(fullname.trim()); user.setEmail(email.trim()); user.setPhone(phone.trim()); user.setAddress(address.trim()); if (userDAO.updateUserInfo(user)) { message = "Cập nhật thông tin thành công!"; session.setAttribute("account", user); } else { message = "Cập nhật thông tin thất bại!"; error = true; }
            }
            else if ("updatePassword".equals(action)) {
                String oldPass = request.getParameter("oldPassword");
                String newPass = request.getParameter("newPassword");
                String confirmPass = request.getParameter("confirmPassword");

                // ⭐ Bước 1: Lấy mật khẩu băm hiện tại từ DB
                String currentHashedPassword = userDAO.getCurrentHashedPassword(user.getId());

                // ⭐ Bước 2: Kiểm tra mật khẩu cũ nhập vào
                if (oldPass == null || oldPass.isEmpty()) {
                    message = "Vui lòng nhập mật khẩu cũ."; error = true;
                } else if (currentHashedPassword == null || !PasswordUtil.checkPassword(oldPass, currentHashedPassword)) {
                    message = "Mật khẩu cũ không chính xác!"; error = true;
                    System.out.println("DEBUG (EditProfile): Old password check failed for user ID: " + user.getId());
                }
                // ⭐ Bước 3: Kiểm tra mật khẩu mới
                else if (newPass == null || newPass.isEmpty()) {
                    message = "Vui lòng nhập mật khẩu mới."; error = true;
                } else if (newPass.length() < 6) {
                    message = "Mật khẩu mới phải có ít nhất 6 ký tự."; error = true;
                } else if (confirmPass == null || !newPass.equals(confirmPass)) {
                    message = "Xác nhận mật khẩu mới không khớp."; error = true;
                } else {
                    // ⭐ Bước 4: Băm mật khẩu mới
                    String hashedNewPassword = PasswordUtil.hashPassword(newPass);

                    // ⭐ Bước 5: Gọi DAO để cập nhật mật khẩu ĐÃ BĂM
                    if (userDAO.updatePassword(user.getId(), hashedNewPassword)) { // Gọi hàm updatePassword với ID
                        message = "Đổi mật khẩu thành công!";
                    } else {
                        message = "Đổi mật khẩu thất bại! Có lỗi xảy ra trong CSDL."; error = true;
                    }
                }
            } else {
                 message = "Hành động không hợp lệ!"; error = true;
            }
        } catch (Exception e) {
             message = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."; error = true;
             e.printStackTrace();
        }

        // Gửi thông báo về JSP
        if (error) { request.setAttribute("error", message); }
        else { request.setAttribute("success", message); }

        // Tải lại category và forward
        loadCategoriesAndForward(request, response);
    }

    // Hàm tiện ích load category
    private void loadCategoriesAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Code đã cung cấp ở lượt trước - ĐÚNG)
         CategoryDAO categoryDAO = new CategoryDAO(); List<Category> categoryList = categoryDAO.getAllCategories(); request.setAttribute("categoryList", categoryList); request.setAttribute("activePage", "profile"); request.getRequestDispatcher("edit_profile.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles viewing and updating user profile information and password.";
    }
}