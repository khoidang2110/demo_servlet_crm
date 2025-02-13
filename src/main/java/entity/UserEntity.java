package entity;

public class UserEntity {
	private int id;
	private String fullname;
	private String email;
	private String password;

	private RoleEntity role;

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", fullname=" + fullname + ", email=" + email + ", password=" + password
				+ ", role=" + role + "]";
	}

	public int getRoleId() {
		return role != null ? role.getId() : 0;
	}

}
