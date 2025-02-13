package services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import entity.RoleEntity;
import entity.UserEntity;
import repository.RoleRepository;
import repository.UserRepository;

public class UserServices {

	private RoleRepository roleRepository = new RoleRepository();
	private UserRepository userRepository = new UserRepository();

	public List<RoleEntity> getRole() {

		List<RoleEntity> roles = roleRepository.findAll();
		return roles;

	}

	public List<UserEntity> getAllUsers() throws SQLException {
		return userRepository.getAllUsers();
	}

	public boolean userCreate(String fullname, String email, String password, int role_id) {
		if (fullname == null || email == null || password == null) {
			return false; // Xử lý dữ liệu không hợp lệ
		}
		RoleEntity role = new RoleEntity();
		role.setId(role_id); // Gán giá trị role_id vào role

		String passwordEncode = getMd5(password);

		UserEntity user = new UserEntity();
		user.setFullname(fullname);
		user.setEmail(email);
		user.setPassword(passwordEncode);
		user.setRole(role);

		// Trước khi gọi repository, sử dụng getRoleId() để lấy role_id
		int roleId = user.getRoleId();

		return userRepository.saveUser(user); // Gọi phương thức lưu user
	}

	public boolean deleteUser(int userId) {
		UserRepository userRepository = new UserRepository();
		return userRepository.deleteUser(userId);
	}

	public UserEntity getUserById(int userId) {
		UserRepository userRepository = new UserRepository();
		return userRepository.getUserById(userId); // Truy vấn user từ repository
	}

	public boolean updateUser(int userId, String fullname, String email, String password, int role_id) {
		// Validate the inputs
		if (fullname == null || email == null || password == null) {
			return false; // Invalid data
		}

		// Create and set up the RoleEntity object
		RoleEntity role = new RoleEntity();
		role.setId(role_id); // Set the role ID

		// Create and populate the UserEntity object
		UserEntity user = new UserEntity();
		user.setId(userId); // Set the user ID for the update operation
		user.setFullname(fullname); // Set the updated fullname
		user.setEmail(email); // Set the updated email
		user.setPassword(password); // Set the updated password, or skip if not updating
		user.setRole(role); // Associate the role with the user

		// Call the repository to update the user in the database
		return userRepository.updateUser(user); // Assuming userRepository.updateUser(user) returns a boolean
	}

	public String getMd5(String input) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
