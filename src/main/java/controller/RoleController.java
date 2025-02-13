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
import services.RoleServices;

@WebServlet(name = "roleController", urlPatterns = { "/role", "/role-add", "/role-create", "/role-delete",
		"/role-update", "/role-save-update" })
public class RoleController extends HttpServlet {
	private RoleServices roleServices = new RoleServices();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {
		case "/role":
			// logic code lien quan den /user
			getRole(req, resp);

			break;
		case "/role-add":
			// logic code lien quan den /user
			System.out.println("Requested path: " + path);

			roleAdd(req, resp);
			break;

		case "/role-update":
			roleUpdate(req, resp); // Xử lý logic cho cập nhật
			break;
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {

		case "/role-create":

			roleCreate(req, resp);
			break;
		case "/role-delete":
			deleteRole(req, resp);
			break;
		case "/role-save-update":
			roleSaveUpdate(req, resp);
			break;
		}
	}

	private void roleAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// List<RoleEntity> roles = userServices.getRole();

		// req.setAttribute("roles", roles);
		req.getRequestDispatcher("role-add.jsp").forward(req, resp);

	}

	private void roleCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String description = req.getParameter("description");

		// int roleId = Integer.parseInt(roleIdStr);
		boolean success = roleServices.roleCreate(name, description);

		if (success) {
			resp.sendRedirect("role"); // Quay lại danh sách ROLE sau khi thêm thành công
		} else {
			req.setAttribute("error", "Thêm role thất bại, vui lòng kiểm tra lại.");
			req.getRequestDispatcher("role-add.jsp").forward(req, resp);
		}

	}

	private void getRole(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<RoleEntity> listRole = new ArrayList<>();
		String query = "SELECT r.id, r.name, r.description FROM roles r";

		// B2: open database connection
		Connection connection = MysqlConfig.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			// Loop through each row of the result and add RoleEntity to the list
			while (result.next()) {
				RoleEntity roleEntity = new RoleEntity();
				roleEntity.setId(result.getInt("id"));
				roleEntity.setName(result.getString("name"));
				roleEntity.setDescription(result.getString("description"));

				listRole.add(roleEntity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close(); // Ensure connection is closed after usage
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		req.setAttribute("listRole", listRole);
		req.getRequestDispatcher("role-table.jsp").forward(req, resp);
	}

	private void deleteRole(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Lấy userId từ request
		String roleIdStr = req.getParameter("id");

		if (roleIdStr != null) {
			try {
				int roleId = Integer.parseInt(roleIdStr);
				boolean success = roleServices.deleteRole(roleId);

				if (success) {
					resp.sendRedirect("role"); // Quay lại danh sách người dùng sau khi xóa thành công
				} else {
					req.setAttribute("error", "Xóa role thất bại.");
					req.getRequestDispatcher("role-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID role không hợp lệ.");
				req.getRequestDispatcher("role-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID role không tìm thấy.");
			req.getRequestDispatcher("role-table.jsp").forward(req, resp);
		}
	}

	private void roleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idStr = req.getParameter("id");
		System.out.println(idStr);
		if (idStr != null) {
			try {
				int id = Integer.parseInt(idStr);
				// Role role = roleService.findById(id);
				RoleEntity role = roleServices.getRoleById(id);

				if (role != null) {

					req.setAttribute("role", role); // Gửi danh sách roles tới giao diện
					req.getRequestDispatcher("role-add.jsp").forward(req, resp);
				} else {
					req.setAttribute("error", "Không tìm thấy role.");
					resp.sendRedirect("role");
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID role không hợp lệ.");
				resp.sendRedirect("role");
			}
		} else {
			req.setAttribute("error", "ID  không tìm thấy.");
			resp.sendRedirect("role");
		}
	}

	protected void roleSaveUpdate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String name = req.getParameter("name");
		String description = req.getParameter("description");

		try {
			// Parse roleId from the request parameter
			int roleId = Integer.parseInt(id);

			// Call service to check if role exists by roleId
			RoleEntity existingRole = roleServices.getRoleById(roleId);

			if (existingRole != null) {

				// Call updateRole method with individual parameters
				boolean success = roleServices.updateRole(roleId, name, description);
				if (success) {
					// Redirect to the role list page if update is successful
					resp.sendRedirect("role");
				} else {
					// Forward to the same page with an error message if update fails
					req.setAttribute("error", "Failed to update role.");
					req.getRequestDispatcher("role-add.jsp").forward(req, resp);
				}
			} else {
				// If role not found by ID, show an error
				req.setAttribute("error", "Role not found.");
				req.getRequestDispatcher("role-add.jsp").forward(req, resp);
			}
		} catch (NumberFormatException e) {
			// Handle invalid data format error
			req.setAttribute("error", "Invalid data.");
			req.getRequestDispatcher("role-add.jsp").forward(req, resp);
		}
	}

}
