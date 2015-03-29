package com.my.dhreelife.util;

public abstract class StringVolleyResponseListener {
	private int taskId = -1;
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public abstract void onStringResponse(String response);
}
