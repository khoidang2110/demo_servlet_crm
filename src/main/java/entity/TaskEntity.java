package entity;

import java.sql.Date;

public class TaskEntity {
	private int id;
	private String name;
	private Date start_date;
	private Date end_date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public JobEntity getJob() {
		return job;
	}

	public void setJob(JobEntity job) {
		this.job = job;
	}

	public StatusEntity getStatus() {
		return status;
	}

	public void setStatus(StatusEntity status) {
		this.status = status;
	}

	private UserEntity user;
	private JobEntity job;
	private StatusEntity status;

	public int getUserId() {
		return user != null ? user.getId() : 0;
	}

	public int getJobId() {
		return job != null ? job.getId() : 0;
	}

	public int getStatusId() {
		return status != null ? status.getId() : 0;
	}

}
