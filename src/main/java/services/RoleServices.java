package services;

import entity.RoleEntity;
import repository.RoleRepository;

public class RoleServices {
	private RoleRepository roleRepository = new RoleRepository();

	public boolean roleCreate(String name, String description) {
//		if (name == null || description == null) {
//			return false; // Xử lý dữ liệu không hợp lệ
//		}
//		RoleEntity role = new RoleEntity();
//		role.setName(name); // Gán giá trị role_id vào role
//		role.setDescription(description);

		return roleRepository.createRole(name, description);
	}

	public boolean deleteRole(int roleId) {
		return roleRepository.deleteRole(roleId); // Gọi phương thức deleteRole trong RoleRepository
	}

	public RoleEntity getRoleById(int roleId) {
		RoleRepository roleRepository = new RoleRepository();
		return roleRepository.findById(roleId); // Truy vấn user từ repository
	}

	public boolean updateRole(int id, String name, String description) {
		// Validate the inputs
		if (name == null || description == null || name.trim().isEmpty() || description.trim().isEmpty()) {
			return false; // Invalid data
		}

		// Create and set up the RoleEntity object
		RoleEntity role = new RoleEntity();
		role.setId(id); // Set the role ID
		role.setName(name); // Set the updated role name
		role.setDescription(description); // Set the updated role description

		// Call the repository to update the role in the database
		return roleRepository.updateRole(role); // Assuming roleRepository.updateRole(role) returns a boolean
	}

}
