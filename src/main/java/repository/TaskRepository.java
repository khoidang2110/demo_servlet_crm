package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.MysqlConfig;
import entity.JobEntity;
import entity.StatusEntity;
import entity.TaskEntity;
import entity.UserEntity;

public class TaskRepository {

	public boolean updateTask(int id, String name, Date startDate, Date endDate, int userId, int jobId, int statusId) {
		String query = "UPDATE tasks SET name = ?, start_date = ?, end_date = ?, user_id = ?, job_id = ?, status_id = ? WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, name);
			statement.setDate(2, startDate);
			statement.setDate(3, endDate);
			statement.setInt(4, userId);
			statement.setInt(5, jobId);
			statement.setInt(6, statusId);
			statement.setInt(7, id); // ID của task cần cập nhật

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0; // Trả về true nếu cập nhật thành công
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage());
			return false;
		}
	}

	public boolean createTask(String name, Date start_date, Date end_date, int user_id, int job_id, int status_id) {
		String query = "INSERT INTO tasks ( name, start_date,end_date,user_id,job_id,status_id) VALUES (?, ?,?,?, ?,?)";
		System.out.println("Executing SQL: " + query); // Log SQL query

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, name);
			statement.setDate(2, start_date);
			statement.setDate(3, end_date);
			statement.setInt(4, user_id);
			statement.setInt(5, job_id);
			statement.setInt(6, status_id);

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage()); // Log error
			return false;
		}
	}

	public TaskEntity getTaskById(int taskId) {

		String query = "SELECT * FROM tasks WHERE id = ?";
		TaskEntity task = null;

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, taskId); // Gán id vào câu truy vấn

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					task = new TaskEntity();
					task.setId(resultSet.getInt("id"));
					task.setName(resultSet.getString("name"));
					task.setStart_date(resultSet.getDate("start_date"));
					task.setEnd_date(resultSet.getDate("end_date"));

					UserRepository userRepo = new UserRepository();
					JobRepository jobRepo = new JobRepository();
					StatusRepository statusRepo = new StatusRepository();

					UserEntity user = userRepo.getUserById(resultSet.getInt("user_id"));
					StatusEntity status = statusRepo.findById(resultSet.getInt("status_id"));
					JobEntity job = jobRepo.getJobById(resultSet.getInt("job_id"));

					task.setUser(user);
					task.setJob(job);
					task.setStatus(status);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return task;
	}

	public boolean deleteTask(int taskId) {
		String query = "DELETE FROM tasks WHERE id = ?";
		Connection connection = MysqlConfig.getConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, taskId); // Đặt giá trị roleId vào câu truy vấn

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
}
