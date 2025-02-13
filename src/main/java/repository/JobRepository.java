package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.MysqlConfig;
import entity.JobEntity;

public class JobRepository {
	public JobEntity getJobById(int jobId) {
		String query = "SELECT * FROM jobs WHERE id = ?";
		JobEntity job = null;

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, jobId); // Gán id vào câu truy vấn

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					job = new JobEntity();
					job.setId(resultSet.getInt("id"));
					job.setName(resultSet.getString("name"));
					job.setStart_date(resultSet.getDate("start_date"));
					job.setEnd_date(resultSet.getDate("end_date"));

//					RoleRepository roleRepo = new RoleRepository();
//					RoleEntity role = roleRepo.findById(resultSet.getInt("role_id"));
//					job.setRole(role); // Gán role cho user
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return job;
	}

	public boolean createJob(String name, Date start_date, Date end_date) {
		String query = "INSERT INTO jobs ( name, start_date,end_date) VALUES (?, ?,?)";
		System.out.println("Executing SQL: " + query); // Log SQL query

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, name);
			statement.setDate(2, start_date);
			statement.setDate(3, end_date);
			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage()); // Log error
			return false;
		}
	}

	public boolean deleteJob(int jobId) {
		String query = "DELETE FROM jobs WHERE id = ?";
		Connection connection = MysqlConfig.getConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, jobId); // Đặt giá trị roleId vào câu truy vấn

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

	public boolean updateJob(JobEntity job) {
		String query = "UPDATE jobs SET name = ?, start_date = ?, end_date = ?  WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, job.getName());
			statement.setDate(2, job.getStart_date());
			statement.setDate(3, job.getEnd_date());
			statement.setInt(4, job.getId());

			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<JobEntity> getAllJobs() {
		List<JobEntity> list = new ArrayList<JobEntity>();
		String query = "SELECT * FROM jobs j";
		try {

			Connection connection = MysqlConfig.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				JobEntity entity = new JobEntity();
				entity.setId(resultSet.getInt("id"));
				entity.setName(resultSet.getString("name"));
				entity.setStart_date(resultSet.getDate("start_date"));
				entity.setEnd_date(resultSet.getDate("end_date"));

				list.add(entity);
			}

		} catch (Exception e) {
			System.out.println("findAll: " + e.getLocalizedMessage());
		}
		return list;
	}

}
