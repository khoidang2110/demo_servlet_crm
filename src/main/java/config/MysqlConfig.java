package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConfig {

	// static dua len ram
	public static Connection getConnection() {

		String url = "jdbc:mysql://localhost:3307/crm_app";
		String user = "root";
		String password = "1234";

		Connection connection = null;
		// Cố gắng kết nối
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
//			if (connection != null) {
//				System.out.println("Kết nối cơ sở dữ liệu thành công!");
//
//			}
		} catch (SQLException e) {
			System.out.println("Kết nối thất bại!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

}
