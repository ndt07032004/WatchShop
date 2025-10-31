package com.watchstore.controlller; // Giữ nguyên package

import com.watchstore.dao.CategoryDAO; // Cần nếu top_menu dùng categoryList
import com.watchstore.dao.ContactDAO;
import com.watchstore.model.Contact;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ContactServlet", urlPatterns = {"/contact"})
public class ContactServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * Chỉ dùng để hiển thị form liên hệ ban đầu.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Nên đặt encoding ở đây nữa

        // Lấy danh sách category cho top_menu (nếu cần)
        CategoryDAO cDao = new CategoryDAO();
        request.setAttribute("categoryList", cDao.getAllCategories());

        // Set trang active cho top_menu
        request.setAttribute("activePage", "contact");

        // Forward đến trang JSP để hiển thị form
        request.getRequestDispatcher("contact.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Xử lý dữ liệu gửi lên từ form liên hệ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // QUAN TRỌNG: Đặt encoding ở đầu doPost

        // 1. Lấy dữ liệu từ form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");

        // 2. Kiểm tra dữ liệu
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            message == null || message.trim().isEmpty()) {
            // Nếu thiếu thông tin -> báo lỗi
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc (*).");
            System.err.println("WARN (ContactServlet): Form submission missing required fields.");
        } else {
            // Nếu đủ thông tin -> gọi DAO để lưu
            ContactDAO dao = new ContactDAO();
            Contact contact = new Contact();
            contact.setName(name.trim()); // Trim() để loại bỏ khoảng trắng thừa
            contact.setEmail(email.trim());
            contact.setMessage(message.trim());

            // Gọi hàm addContact và kiểm tra kết quả
            boolean success = dao.addContact(contact);

            if (success) {
                // Nếu lưu thành công -> báo thành công
                request.setAttribute("success", "Gửi liên hệ thành công! Chúng tôi sẽ phản hồi sớm.");
                System.out.println("INFO (ContactServlet): Contact saved successfully from " + email);
            } else {
                // Nếu lưu thất bại -> báo lỗi CSDL
                request.setAttribute("error", "Đã xảy ra lỗi khi lưu thông tin liên hệ. Vui lòng thử lại.");
                System.err.println("ERROR (ContactServlet): Failed to save contact to database from " + email);
            }
        }

        // 3. Lấy lại danh sách category cho top_menu (vì forward)
        CategoryDAO cDao = new CategoryDAO();
        request.setAttribute("categoryList", cDao.getAllCategories());

        // 4. Set trang active
        request.setAttribute("activePage", "contact");

        // 5. Forward về lại trang contact.jsp để hiển thị thông báo
        request.getRequestDispatcher("contact.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles contact form submissions.";
    }
}