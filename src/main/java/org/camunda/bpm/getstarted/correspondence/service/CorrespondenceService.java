package org.camunda.bpm.getstarted.correspondence.service;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.getstarted.correspondence.payloads.ECorrTask;
import org.camunda.bpm.getstarted.correspondence.payloads.IncomingCorrespondence;
import org.camunda.bpm.getstarted.correspondence.payloads.ResponseCorrespondence;
import org.camunda.bpm.getstarted.correspondence.payloads.TaskQueryFilter;

public interface CorrespondenceService {
	public Map<String, Object> receiveIncoming(IncomingCorrespondence incoming);
	
	public void completeTask(String taskId);
	
	public void completeRespondTask(String taskId);
	
	public ECorrTask getTaskById(String taskId);
	
	public List<ECorrTask> searchTasks(TaskQueryFilter tqf);
	
	public String updateResponse(String taskId, ResponseCorrespondence rc);

	List<ECorrTask> getTaskByCaseId(int caseId);
}
