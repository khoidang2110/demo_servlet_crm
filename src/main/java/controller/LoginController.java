package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.MysqlConfig;
import entity.RoleEntity;
import entity.UserEntity;
import services.UserServices;

@WebServlet(name = "loginController", urlPatterns = { "/login" })
public class LoginController extends HttpServlet {
	private UserServices userServices = new UserServices();
	private List<UserEntity> listUser = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String path = req.getServletPath();
//
//		switch (path) {
//		case "/login":
//			System.out.println("login ");
//			req.getRequestDispatcher("login.jsp").forward(req, resp);
//			break;
//		}

		Cookie[] cookies = req.getCookies();
		String email = "";
		String password = "";

		req.setAttribute("email", email);
		req.setAttribute("password", password);

		req.getRequestDispatcher("login.jsp").forward(req, resp);
		// resp.sendRedirect("login.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String remember = req.getParameter("remember-me");

		System.out.println("Email nhập vào: " + email);
		System.out.println("Password nhập vào: " + password);

		String passwordEncoded = userServices.getMd5(password);
		System.out.println("Password mã hóa MD5: " + passwordEncoded);

		// Câu lệnh truy vấn tìm user
		String query = "SELECT r.name, u.id, u.email FROM users u " + "JOIN roles r ON u.role_id = r.id "
				+ "WHERE u.email = ? AND u.password = ?";

		UserEntity loggedInUser = null;

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, email);
			statement.setString(2, passwordEncoded);

			// System.out.println("Thực thi truy vấn: " + query);
			ResultSet result = statement.executeQuery();


			if (result.next()) {
				loggedInUser = new UserEntity();
				loggedInUser.setEmail(result.getString("email"));
				loggedInUser.setId(result.getInt("id"));

				RoleEntity roleEntity = new RoleEntity();
				roleEntity.setName(result.getString("name"));
				System.out.println(roleEntity);
				loggedInUser.setRole(roleEntity);
			}

		} catch (SQLException e) {
			System.out.println("Lỗi SQL:");
			e.printStackTrace();
		}


		if (loggedInUser != null) {
			// Lưu thông tin người dùng vào cookies nếu chọn "remember me"
			if (remember != null) {
				Cookie ckEmail = new Cookie("email", email);
				Cookie ckPassword = new Cookie("password", password);
				resp.addCookie(ckEmail);
				resp.addCookie(ckPassword);
			}

			// Lưu thông tin role vào cookie
			Cookie ckRole = new Cookie("role", loggedInUser.getRole().getName());
			resp.addCookie(ckRole);
			//System.out.println("id of user" + loggedInUser. toString());
			Cookie ckUserId = new Cookie("user_id", String.valueOf(loggedInUser.getId()));
			resp.addCookie(ckUserId);

			// Chuyển hướng đến trang chính
			req.getRequestDispatcher("index.html").forward(req, resp);

		} else {
			req.setAttribute("message", "dang nhap that bai");
			req.getRequestDispatcher("login.jsp").forward(req, resp);

		}
	}

}
