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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.MysqlConfig;
import entity.RoleEntity;
import entity.UserEntity;
import services.UserServices;

@WebServlet(name = "userController", urlPatterns = { "/user", "/user-add", "/user-create", "/user-delete",
		"/user-detail", "/user-update", "/user-save-update" })
public class UserController extends HttpServlet {

	private UserServices userServices = new UserServices();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {
		case "/user":
			// logic code lien quan den /user
			getUser(req, resp);

			break;
		case "/user-add":
			// logic code lien quan den /user

			userAdd(req, resp);
			break;

		case "/user-delete":
			deleteUser(req, resp); // Gọi phương thức xóa ở đây
			break;
		case "/user-detail":
			getUserDetail(req, resp); // Gọi phương thức lấy thông tin người dùng
			break;
		case "/user-update":
			userUpdate(req, resp); // Xử lý logic cho cập nhật
			break;
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {

		case "/user-create":

			userCreate(req, resp);
			break;
		case "/user-save-update":
			userSaveUpdate(req, resp);
			break;
		}
	}

	private void getUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<UserEntity> listUser = new ArrayList<UserEntity>();
		String query = "SELECT u.id, u.fullname, u.email, r.name, r.description\n" + "FROM users u\n"
				+ "JOIN roles r ON r.id = u.role_id";

		// B2: mở kết nối csdl
		Connection connection = MysqlConfig.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement(query);

			// excuteQuery: SELECT
			// excuteUpdate: ko phai cau SELECT
			ResultSet result = statement.executeQuery();

			// Duyệt từng dòng dữ liệu truy vấn được và gán vào ListUser

			while (result.next()) {

				UserEntity entity = new UserEntity();
				entity.setEmail(result.getString("email"));
				entity.setId(result.getInt("id"));
				entity.setFullname(result.getString("fullname"));

				RoleEntity roleEntity = new RoleEntity();
				roleEntity.setName(result.getString("name"));
				roleEntity.setDescription(result.getString("description"));

				entity.setRole(roleEntity);

				listUser.add(entity);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		req.setAttribute("listUser", listUser);
		req.getRequestDispatcher("user-table.jsp").forward(req, resp);

	}

	private void userAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<RoleEntity> roles = userServices.getRole();

		req.setAttribute("roles", roles);
		req.setAttribute("currentRoleId", null);
		req.getRequestDispatcher("user-add.jsp").forward(req, resp);

	}

	private void userCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fullname = req.getParameter("fullname");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String roleIdStr = req.getParameter("role_id");
		System.out.println("Full Name: " + fullname);
		System.out.println("Email: " + email);
		System.out.println("Password: " + password);
		System.out.println("Role ID: " + roleIdStr);
		int roleId = Integer.parseInt(roleIdStr); // Chuyển đổi role_id sang kiểu số

		// int roleId = Integer.parseInt(roleIdStr);
		boolean success = userServices.userCreate(fullname, email, password, roleId);

		if (success) {
			resp.sendRedirect("user"); // Quay lại danh sách user sau khi thêm thành công
		} else {
			req.setAttribute("error", "Thêm user thất bại, vui lòng kiểm tra lại.");
			req.getRequestDispatcher("user-add.jsp").forward(req, resp);
		}

	}

	private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Lấy userId từ request
		String userIdStr = req.getParameter("id");

		if (userIdStr != null) {
			try {
				int userId = Integer.parseInt(userIdStr);
				boolean success = userServices.deleteUser(userId);

				if (success) {
					resp.sendRedirect("user"); // Quay lại danh sách người dùng sau khi xóa thành công
				} else {
					req.setAttribute("error", "Xóa người dùng thất bại.");
					req.getRequestDispatcher("user-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID người dùng không hợp lệ.");
				req.getRequestDispatcher("user-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID người dùng không tìm thấy.");
			req.getRequestDispatcher("user-table.jsp").forward(req, resp);
		}
	}

	private void getUserDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userIdStr = req.getParameter("id");
		if (userIdStr != null) {
			try {
				int userId = Integer.parseInt(userIdStr);
				UserEntity user = userServices.getUserById(userId);
				if (user != null) {
					req.setAttribute("user", user);
					System.out.println("User role: " + user);

					req.getRequestDispatcher("user-details.jsp").forward(req, resp); // Gửi thông tin user đến JSP
				} else {
					req.setAttribute("error", "Không tìm thấy người dùng.");
					req.getRequestDispatcher("user-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID người dùng không hợp lệ.");
				req.getRequestDispatcher("user-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID người dùng không tìm thấy.");
			req.getRequestDispatcher("user-table.jsp").forward(req, resp);
		}
	}

	private void userUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userIdStr = req.getParameter("id");
		if (userIdStr != null) {
			try {
				int userId = Integer.parseInt(userIdStr);
				UserEntity user = userServices.getUserById(userId);
				List<RoleEntity> roles = userServices.getRole();

				if (user != null) {
					req.setAttribute("user", user);
					req.setAttribute("roles", roles);
					req.setAttribute("currentRoleid", user.getRole().getId());
					System.out.print("id cua role:" + user.getRole().getId());
					// System.out.print("id cua user - role:" + user.getRole().getId());
					// Gửi danh sách roles tới giao diện
					req.getRequestDispatcher("user-add.jsp").forward(req, resp);
				} else {
					req.setAttribute("error", "Không tìm thấy người dùng.");
					resp.sendRedirect("user");
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID người dùng không hợp lệ.");
				resp.sendRedirect("user");
			}
		} else {
			req.setAttribute("error", "ID người dùng không tìm thấy.");
			resp.sendRedirect("user");
		}
	}

	protected void userSaveUpdate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String idStr = req.getParameter("id"); // Get the user ID from the hidden field
		String fullname = req.getParameter("fullname");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String roleIdStr = req.getParameter("role_id");

		try {
			// Parse roleId from the request parameter
			int roleId = Integer.parseInt(roleIdStr);

			if (idStr == null || idStr.isEmpty()) {
				req.setAttribute("error", "User ID is required for update.");
				req.getRequestDispatcher("user-add.jsp").forward(req, resp);
				return;
			}

			int userId = Integer.parseInt(idStr);
			UserEntity existingUser = userServices.getUserById(userId); // Retrieve user by ID

			if (existingUser != null) {

				// Call updateUser method with individual parameters
				boolean success = userServices.updateUser(userId, fullname, email, password, roleId);
				if (success) {
					// Redirect to the user list page if update is successful
					resp.sendRedirect("user");
				} else {
					// Forward to the same page with an error message if update fails
					req.setAttribute("error", "Failed to update user.");
					req.getRequestDispatcher("user-add.jsp").forward(req, resp);
				}
			} else {
				// If user not found by ID, show an error
				req.setAttribute("error", "User not found.");
				req.getRequestDispatcher("user-add.jsp").forward(req, resp);
			}
		} catch (NumberFormatException e) {
			// Handle invalid data format error
			req.setAttribute("error", "Invalid data.");
			req.getRequestDispatcher("user-add.jsp").forward(req, resp);
		}
	}

}
