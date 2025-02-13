package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "logoutController", urlPatterns = { "/logout" })
public class LogoutController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Lấy tất cả cookies từ request
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// Kiểm tra cookie có tên là "role"
				if (cookie.getName().equals("role")) {
					cookie.setValue("logout");

					// Thêm cookie mới vào response
					resp.addCookie(cookie);

					break; // Không cần tiếp tục vòng lặp vì đã xử lý xong cookie "role"
				}
			}
		}
		resp.sendRedirect("login.jsp"); // Điều hướng về trang login
	}
}
