package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.MysqlConfig;
import entity.RoleEntity;

public class RoleRepository {
	// Nguyên tắc đặt hàm sao cho dễ hình dung tới câu truy vấn
	// select: tên hàm có chữ find
	// where: by

	public List<RoleEntity> findAll() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		String query = "SELECT * FROM roles r";
		try {

			Connection connection = MysqlConfig.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				RoleEntity entity = new RoleEntity();
				entity.setId(resultSet.getInt("id"));
				entity.setName(resultSet.getString("name"));
				entity.setDescription(resultSet.getString("description"));

				list.add(entity);
			}

		} catch (Exception e) {
			System.out.println("findAll: " + e.getLocalizedMessage());
		}
		return list;
	}

	public RoleEntity findById(int id) {
		RoleEntity role = null;
		String query = "SELECT * FROM roles WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					role = new RoleEntity();
					role.setId(resultSet.getInt("id"));
					role.setName(resultSet.getString("name"));
					role.setDescription(resultSet.getString("description"));
				}
			}

		} catch (Exception e) {
			System.out.println("findById: " + e.getLocalizedMessage());
		}

		return role;
	}

	public boolean createRole(String name, String description) {
		String query = "INSERT INTO roles ( name, description) VALUES (?, ?)";
		System.out.println("Executing SQL: " + query); // Log SQL query

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, name);
			statement.setString(2, description);

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage()); // Log error
			return false;
		}
	}

	public boolean deleteRole(int roleId) {
		String query = "DELETE FROM roles WHERE id = ?";
		Connection connection = MysqlConfig.getConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, roleId); // Đặt giá trị roleId vào câu truy vấn

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0; // Nếu có ít nhất 1 dòng bị ảnh hưởng, trả về true
		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Nếu có lỗi, trả về false
		} finally {
			try {
				connection.close(); // Đảm bảo đóng kết nối sau khi sử dụng
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean updateRole(RoleEntity role) {
		String query = "UPDATE roles SET name = ?, description = ? WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, role.getName());
			statement.setString(2, role.getDescription());
			statement.setInt(3, role.getId());

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}