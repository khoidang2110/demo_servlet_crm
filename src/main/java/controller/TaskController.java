
package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.MysqlConfig;
import entity.JobEntity;
import entity.StatusEntity;
import entity.TaskEntity;
import entity.UserEntity;
import services.JobServices;
import services.TaskServices;
import services.UserServices;

@WebServlet(name = "taskController", urlPatterns = { "/task", "/task-add", "/task-create", "/task-update",
		"/task-save-update", "/task-delete" })
public class TaskController extends HttpServlet {

	private UserServices userServices = new UserServices();
	private JobServices jobServices = new JobServices();
	private TaskServices taskServices = new TaskServices();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {
		case "/task":
			getTask(req, resp);
			break;

		case "/task-add":

			taskAdd(req, resp);
			break;
		case "/task-update":
			try {
				taskUpdate(req, resp);
			} catch (ServletException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Xử lý logic cho cập nhật
			break;

		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {

		case "/task-create":

			taskCreate(req, resp);
			break;
		case "/task-save-update":
			taskSaveUpdate(req, resp);
			break;
		case "/task-delete":
			deleteTask(req, resp);
			break;
		}
	}

	private void taskUpdate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String idStr = req.getParameter("id");
		System.out.println(idStr);
		if (idStr != null) {
			try {
				int id = Integer.parseInt(idStr);

				TaskEntity task = taskServices.getTaskById(id);
				List<UserEntity> users = userServices.getAllUsers();
				List<JobEntity> jobs = jobServices.getAllJobs();
				if (task != null) {
//					System.out.println("Task Details:");
//					System.out.println("ID: " + task.getId());
//					System.out.println("Name: " + task.getName());
//					System.out.println("Start Date: " + task.getStart_date());
//					System.out.println("End Date: " + task.getEnd_date());
//					if (task.getUser() != null) {
//
//						System.out.println("User Fullname: " + task.getUser().getFullname());
//					}
//					if (task.getJob() != null) {
//						System.out.println("job name: " + task.getJob().getName());
//
//					}
					req.setAttribute("task", task);
					req.setAttribute("currentJobId", task.getJob().getId());
					req.setAttribute("currentUserId", task.getUser().getId());
					req.setAttribute("users", users);
					req.setAttribute("jobs", jobs);
					req.getRequestDispatcher("task-add.jsp").forward(req, resp);
				} else {
					req.setAttribute("error", "Không tìm thấy task.");
					resp.sendRedirect("task");
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID task không hợp lệ.");
				resp.sendRedirect("task");
			}
		} else {
			req.setAttribute("error", "ID  không tìm thấy.");
			resp.sendRedirect("task");
		}
	}

	private void taskCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String name = req.getParameter("name");
		String startDateStr = req.getParameter("start_date");
		String endDateStr = req.getParameter("end_date");
		String userIdStr = req.getParameter("user_id");
		String jobIdStr = req.getParameter("job_id");
		String statusIdStr = req.getParameter("status_id");

		int userId = Integer.parseInt(userIdStr);
		int jobId = Integer.parseInt(jobIdStr);
		int statusId = Integer.parseInt(statusIdStr);

		java.sql.Date sqlStartDate = null;
		java.sql.Date sqlEndDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Change the format if needed
			java.util.Date utilStartDate = dateFormat.parse(startDateStr);
			java.util.Date utilEndDate = dateFormat.parse(endDateStr);

			// Convert java.util.Date to java.sql.Date
			sqlStartDate = new java.sql.Date(utilStartDate.getTime());
			sqlEndDate = new java.sql.Date(utilEndDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			req.setAttribute("error", "Ngày tháng không hợp lệ. Vui lòng nhập đúng định dạng.");
			req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
			return; // Stop execution if dates are invalid
		}

		boolean success = taskServices.createTask(name, sqlStartDate, sqlEndDate, userId, jobId, statusId);

		if (success) {
			resp.sendRedirect("task"); // Quay lại danh sách user sau khi thêm thành công
		} else {
			req.setAttribute("error", "Thêm task thất bại, vui lòng kiểm tra lại.");
			req.getRequestDispatcher("task-add.jsp").forward(req, resp);
		}

	}

	private void taskAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// req.getRequestDispatcher("task-add.jsp").forward(req, resp);
		try {
			// Get all jobs and users
			List<JobEntity> jobs = jobServices.getAllJobs();
			List<UserEntity> users = userServices.getAllUsers();

			// Add to request scope for use in JSP
			req.setAttribute("jobs", jobs);
			req.setAttribute("users", users);
			req.setAttribute("currentUserId", null); // Không chọn gì khi thêm mới
			req.setAttribute("currentJobId", null);
			// Forward to the JSP
			req.getRequestDispatcher("task-add.jsp").forward(req, resp);
		} catch (SQLException e) {
			e.printStackTrace();
			req.setAttribute("error", "Error loading data");
			req.getRequestDispatcher("error.jsp").forward(req, resp);
		}
	}

	private void getTask(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<TaskEntity> listTask = new ArrayList<>();
		// Fixed SQL query by adding the missing comma between j.start_date and
		// j.end_date
		String query = "SELECT t.id, t.name AS task_name, t.start_date, t.end_date, "
				+ "u.fullname AS user_name, j.name AS job_name, s.name AS status_name " + "FROM tasks t "
				+ "JOIN users u ON u.id = t.user_id " + "JOIN jobs j ON j.id = t.job_id "
				+ "JOIN status s ON s.id = t.status_id";

		// B2: open database connection
		Connection connection = MysqlConfig.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			// Loop through each row of the result and add JobEntity to the list
			while (result.next()) {
				TaskEntity taskEntity = new TaskEntity();
				// Corrected the method calls to use the object `jobEntity`
				taskEntity.setId(result.getInt("id"));
				taskEntity.setName(result.getString("task_name"));
				taskEntity.setStart_date(result.getDate("start_date"));
				taskEntity.setEnd_date(result.getDate("end_date"));

				UserEntity userEntity = new UserEntity();
				JobEntity jobEntity = new JobEntity();
				StatusEntity statusEntity = new StatusEntity();

				userEntity.setFullname(result.getString("user_name"));
				jobEntity.setName(result.getString("job_name"));
				statusEntity.setName(result.getString("status_name"));

				taskEntity.setUser(userEntity);
				taskEntity.setJob(jobEntity);
				taskEntity.setStatus(statusEntity);

				// Corrected: Add the jobEntity to the listJob
				listTask.add(taskEntity);
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

		// Set the job list as an attribute and forward to the JSP
		req.setAttribute("listTask", listTask);
		req.getRequestDispatcher("task-table.jsp").forward(req, resp);
	}

	private void taskSaveUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idStr = req.getParameter("id");
		String name = req.getParameter("name");
		String startDateStr = req.getParameter("start_date");
		String endDateStr = req.getParameter("end_date");
		String userIdStr = req.getParameter("user_id");
		String jobIdStr = req.getParameter("job_id");
		String statusIdStr = req.getParameter("status_id");

		int id = Integer.parseInt(idStr);
		int userId = Integer.parseInt(userIdStr);
		int jobId = Integer.parseInt(jobIdStr);
		int statusId = Integer.parseInt(statusIdStr);

		java.sql.Date sqlStartDate = null;
		java.sql.Date sqlEndDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utilStartDate = dateFormat.parse(startDateStr);
			java.util.Date utilEndDate = dateFormat.parse(endDateStr);

			// Convert java.util.Date to java.sql.Date
			sqlStartDate = new java.sql.Date(utilStartDate.getTime());
			sqlEndDate = new java.sql.Date(utilEndDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			req.setAttribute("error", "Ngày tháng không hợp lệ. Vui lòng nhập đúng định dạng.");
			req.getRequestDispatcher("task-add.jsp").forward(req, resp);
			return;
		}

		// Gọi service để cập nhật task
		boolean success = taskServices.updateTask(id, name, sqlStartDate, sqlEndDate, userId, jobId, statusId);

		if (success) {
			resp.sendRedirect("task"); // Quay lại danh sách task sau khi cập nhật thành công
		} else {
			req.setAttribute("error", "Cập nhật task thất bại, vui lòng kiểm tra lại.");
			req.getRequestDispatcher("task-add.jsp").forward(req, resp);
		}
	}

	private void deleteTask(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String taskIdStr = req.getParameter("id");

		if (taskIdStr != null) {
			try {
				int taskId = Integer.parseInt(taskIdStr);
				boolean success = taskServices.deleteTask(taskId);

				if (success) {
					resp.sendRedirect("task"); // Quay lại danh sách người dùng sau khi xóa thành công
				} else {
					req.setAttribute("error", "Xóa task thất bại.");
					req.getRequestDispatcher("task-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID task không hợp lệ.");
				req.getRequestDispatcher("task-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID task không tìm thấy.");
			req.getRequestDispatcher("task-table.jsp").forward(req, resp);
		}
	}
}
