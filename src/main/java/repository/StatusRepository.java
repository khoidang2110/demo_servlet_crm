package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import config.MysqlConfig;
import entity.StatusEntity;

public class StatusRepository {
	public StatusEntity findById(int id) {
		StatusEntity status = null;
		String query = "SELECT * FROM status WHERE id = ?";

		try (Connection connection = MysqlConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					status = new StatusEntity();
					status.setId(resultSet.getInt("id"));
					status.setName(resultSet.getString("name"));

				}
			}

		} catch (Exception e) {
			System.out.println("findById: " + e.getLocalizedMessage());
		}

		return status;
	}
}
