package services;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import entity.JobEntity;
import repository.JobRepository;

public class JobServices {
	private JobRepository jobRepository = new JobRepository();

	public JobEntity getJobById(int jobId) {
		// JobRepository jobRepository = new JobRepository();
		return jobRepository.getJobById(jobId); // Truy vấn user từ repository
	}

	public List<JobEntity> getAllJobs() throws SQLException {
		return jobRepository.getAllJobs();
	}

	public boolean jobCreate(String name, Date start_date, Date end_date) {
//		if (name == null || description == null) {
//			return false; // Xử lý dữ liệu không hợp lệ
//		}
//		RoleEntity role = new RoleEntity();
//		role.setName(name); // Gán giá trị role_id vào role
//		role.setDescription(description);

		return jobRepository.createJob(name, start_date, end_date);
	}

	public boolean deleteJob(int jobId) {
		return jobRepository.deleteJob(jobId); // Gọi phương thức deleteRole trong RoleRepository
	}

	public boolean updateJob(int jobId, String name, java.sql.Date startDate, java.sql.Date endDate) {
		// Validate the inputs
		if (name.trim().isEmpty() || startDate == null || endDate == null) {
			return false; // Invalid data
		}

		// Retrieve the job entity by jobId
		JobEntity job = jobRepository.getJobById(jobId); // Assuming this method exists in the repository

		if (job == null) {
			return false; // Job not found
		}

		// Update the job properties with the new values
		job.setName(name);
		job.setStart_date(startDate);
		job.setEnd_date(endDate);

		// Call the repository to update the job in the database
		return jobRepository.updateJob(job); // Assuming jobRepository.updateJob(job) returns a boolean
	}

}
