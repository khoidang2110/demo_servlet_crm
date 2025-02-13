package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.MysqlConfig;
import entity.RoleEntity;
import entity.UserEntity;

public class UserRepository {
	public boolean saveUser(UserEntity user) {
		String query = "INSERT INTO users ( email, password,fullname, avatar, role_id) VALUES (?, ?, ?,?,?)";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, user.getEmail());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getFullname());

			// Gán avatar là NULL cứng
			statement.setNull(4, java.sql.Types.VARCHAR); // Gán NULL cho avatar

			statement.setInt(5, user.getRoleId()); // Gán role_id
			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateUser(UserEntity user) {
		String query = "UPDATE users SET fullname = ?, email = ?, role_id = ? WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, user.getFullname());
			statement.setString(2, user.getEmail());
			statement.setInt(3, user.getRoleId());
			statement.setInt(4, user.getId());

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteUser(int userId) {
		String query = "DELETE FROM users WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, userId); // Gán ID người dùng cần xóa

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public UserEntity getUserById(int userId) {
		String query = "SELECT * FROM users WHERE id = ?";
		UserEntity user = null;

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, userId); // Gán id vào câu truy vấn

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					user = new UserEntity();
					user.setId(resultSet.getInt("id"));
					user.setEmail(resultSet.getString("email"));
					// user.setPassword(resultSet.getString("password"));
					user.setFullname(resultSet.getString("fullname"));
					// user.setAvatar(resultSet.getString("avatar"));
					// user.setId(resultSet.getInt("role_id"));
					RoleRepository roleRepo = new RoleRepository();
					RoleEntity role = roleRepo.findById(resultSet.getInt("role_id"));
					user.setRole(role); // Gán role cho user
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public List<UserEntity> getAllUsers() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		String query = "SELECT * FROM users u";
		try {

			Connection connection = MysqlConfig.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				UserEntity entity = new UserEntity();
				entity.setId(resultSet.getInt("id"));
				entity.setFullname(resultSet.getString("fullname"));
				entity.setEmail(resultSet.getString("email"));

				list.add(entity);
			}

		} catch (Exception e) {
			System.out.println("findAll: " + e.getLocalizedMessage());
		}
		return list;
	}
}
