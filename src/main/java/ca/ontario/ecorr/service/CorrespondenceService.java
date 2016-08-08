package ca.ontario.ecorr.service;

import java.util.List;
import java.util.Map;

import ca.ontario.ecorr.payloads.ECorrTask;
import ca.ontario.ecorr.payloads.IncomingCorrespondence;
import ca.ontario.ecorr.payloads.ResponseCorrespondence;
import ca.ontario.ecorr.payloads.TaskQueryFilter;

public interface CorrespondenceService {
	public Map<String, Object> receiveIncoming(IncomingCorrespondence incoming);
	
	public void completeTask(String taskId);
	
	public void completeRespondTask(String taskId);
	
	public ECorrTask getTaskById(String taskId);
	
	public List<ECorrTask> searchTasks(TaskQueryFilter tqf);
	
	public String updateResponse(String taskId, ResponseCorrespondence rc);

	List<ECorrTask> getTaskByCaseId(int caseId);
}
