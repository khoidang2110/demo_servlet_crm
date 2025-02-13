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
import services.JobServices;

@WebServlet(name = "jobController", urlPatterns = { "/job", "/job-details", "/job-add", "/job-create", "/job-delete",
		"/job-update", "/job-save-update" })
public class JobController extends HttpServlet {

	private JobServices jobServices = new JobServices();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Servlet /job is being accessed");
		String path = req.getServletPath();
		switch (path) {
		case "/job":
			// logic code liên quan đến /job
			getJob(req, resp);
			break;
		case "/job-details":
			getJobDetail(req, resp); // Gọi phương thức lấy thông tin người dùng
			break;
		case "/job-add":

			jobAdd(req, resp);
			break;
		case "/job-update":
			jobUpdate(req, resp); // Xử lý logic cho cập nhật
			break;
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = req.getServletPath();
		switch (path) {

		case "/job-create":

			jobCreate(req, resp);
			break;
		case "/job-delete":
			deleteJob(req, resp);
			break;
		case "/job-save-update":
			jobSaveUpdate(req, resp);
			break;
		}
	}

	private void jobUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idStr = req.getParameter("id");
		System.out.println(idStr);
		if (idStr != null) {
			try {
				int id = Integer.parseInt(idStr);

				JobEntity job = jobServices.getJobById(id);

				if (job != null) {

					req.setAttribute("job", job); // Gửi danh sách roles tới giao diện
					req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
				} else {
					req.setAttribute("error", "Không tìm thấy job.");
					resp.sendRedirect("job");
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID job không hợp lệ.");
				resp.sendRedirect("job");
			}
		} else {
			req.setAttribute("error", "ID  không tìm thấy.");
			resp.sendRedirect("job");
		}
	}

	private void jobAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// List<RoleEntity> roles = userServices.getRole();

		// req.setAttribute("roles", roles);
		req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);

	}

	private void deleteJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String jobIdStr = req.getParameter("id");

		if (jobIdStr != null) {
			try {
				int jobId = Integer.parseInt(jobIdStr);
				boolean success = jobServices.deleteJob(jobId);

				if (success) {
					resp.sendRedirect("job"); // Quay lại danh sách người dùng sau khi xóa thành công
				} else {
					req.setAttribute("error", "Xóa job thất bại.");
					req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID job không hợp lệ.");
				req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID job không tìm thấy.");
			req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
		}
	}

	private void getJob(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<JobEntity> listJob = new ArrayList<>();
		// Fixed SQL query by adding the missing comma between j.start_date and
		// j.end_date
		String query = "SELECT j.id, j.name, j.start_date, j.end_date FROM jobs j";

		// B2: open database connection
		Connection connection = MysqlConfig.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			// Loop through each row of the result and add JobEntity to the list
			while (result.next()) {
				JobEntity jobEntity = new JobEntity();
				// Corrected the method calls to use the object `jobEntity`
				jobEntity.setId(result.getInt("id"));
				jobEntity.setName(result.getString("name"));
				jobEntity.setStart_date(result.getDate("start_date"));
				jobEntity.setEnd_date(result.getDate("end_date"));

				// Corrected: Add the jobEntity to the listJob
				listJob.add(jobEntity);
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
		req.setAttribute("listJob", listJob);
		req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
	}

	private void getJobDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jobIdStr = req.getParameter("id");
		if (jobIdStr != null) {
			try {
				int jobId = Integer.parseInt(jobIdStr);
				// UserEntity user = userServices.getUserById(userId);
				JobEntity job = jobServices.getJobById(jobId);
				if (job != null) {
					req.setAttribute("job", job);
					// System.out.println("User role: " + user);

					req.getRequestDispatcher("groupwork-details.jsp").forward(req, resp);
				} else {
					req.setAttribute("error", "Không tìm thấy job.");
					req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
				}
			} catch (NumberFormatException e) {
				req.setAttribute("error", "ID job không hợp lệ.");
				req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("error", "ID job không tìm thấy.");
			req.getRequestDispatcher("groupwork-table.jsp").forward(req, resp);
		}
	}

	private void jobCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String startDateStr = req.getParameter("start_date");
		String endDateStr = req.getParameter("end_date");
		// Convert strings to java.sql.Date
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

		// Call the service method with the correct Date types
		boolean success = jobServices.jobCreate(name, sqlStartDate, sqlEndDate);

		if (success) {
			resp.sendRedirect("job"); // Quay lại danh sách ROLE sau khi thêm thành công
		} else {
			req.setAttribute("error", "Thêm job thất bại, vui lòng kiểm tra lại.");
			req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
		}

	}

	protected void jobSaveUpdate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String name = req.getParameter("name");
		String startDateStr = req.getParameter("start_date");
		String endDateStr = req.getParameter("end_date");

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
			System.out.println("Error parsing dates: " + e.getMessage()); // Log error message
			return; // Stop execution if dates are invalid
		}

		try {
			// Parse jobId from the request parameter
			int jobId = Integer.parseInt(id);
			System.out.println("Job ID: " + jobId); // Log job ID

			// Call service to check if job exists by jobId
			JobEntity existingJob = jobServices.getJobById(jobId);
			if (existingJob != null) {
				System.out.println("Job found with ID: " + jobId); // Log successful job retrieval

				// Call updateJob method with individual parameters
				boolean success = jobServices.updateJob(jobId, name, sqlStartDate, sqlEndDate);
				if (success) {
					// Redirect to the job list page if update is successful
					System.out.println("Job update successful, redirecting...");
					resp.sendRedirect("job");
				} else {
					// Forward to the same page with an error message if update fails
					req.setAttribute("error", "Failed to update job.");
					req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
					System.out.println("Failed to update job"); // Log failure
				}
			} else {
				// If job not found by ID, show an error
				req.setAttribute("error", "Job not found.");
				req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
				System.out.println("Job not found with ID: " + jobId); // Log job not found
			}
		} catch (NumberFormatException e) {
			// Handle invalid data format error
			req.setAttribute("error", "Invalid data.");
			req.getRequestDispatcher("groupwork-add.jsp").forward(req, resp);
			System.out.println("Error in NumberFormatException: " + e.getMessage()); // Log exception
		}
	}

}
