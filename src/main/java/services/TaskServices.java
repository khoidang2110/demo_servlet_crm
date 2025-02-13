package services;

import java.sql.Date;

import entity.TaskEntity;
import repository.TaskRepository;

public class TaskServices {

	private TaskRepository taskRepository = new TaskRepository();

	public boolean createTask(String name, Date start_date, Date end_date, int user_id, int job_id, int status_id) {

		return taskRepository.createTask(name, start_date, end_date, user_id, job_id, status_id);
	}

	public TaskEntity getTaskById(int taskId) {
		// JobRepository jobRepository = new JobRepository();
		return taskRepository.getTaskById(taskId); // Truy vấn user từ repository
	}

	public boolean updateTask(int id, String name, Date startDate, Date endDate, int userId, int jobId, int statusId) {
		return taskRepository.updateTask(id, name, startDate, endDate, userId, jobId, statusId);
	}

	public boolean deleteTask(int taskId) {
		return taskRepository.deleteTask(taskId); // Gọi phương thức deleteRole trong RoleRepository
	}
}
