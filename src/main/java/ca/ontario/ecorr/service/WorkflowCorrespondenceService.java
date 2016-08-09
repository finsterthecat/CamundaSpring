package ca.ontario.ecorr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

import ca.ontario.ecorr.payloads.ECorrTask;
import ca.ontario.ecorr.payloads.IncomingCorrespondence;
import ca.ontario.ecorr.payloads.ResponseCorrespondence;
import ca.ontario.ecorr.payloads.TaskQueryFilter;

@Named
@Profile("production")
public class WorkflowCorrespondenceService implements CorrespondenceService {
	private RuntimeService runtimeService;
	private TaskService taskService;
	
	@Inject
	public WorkflowCorrespondenceService(RuntimeService runtimeService,
											TaskService taskService) {
		this.runtimeService = runtimeService;
		this.taskService = taskService;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowCorrespondenceService.class);
	
	@Override
	public Map<String, Object> receiveIncoming(IncomingCorrespondence incoming) {
		LOGGER.debug("start receiveIncoming: {}", incoming);
		Map<String, Object> variables = new HashMap<>();
		variables.put("clientName", incoming.getClientName());
		variables.put("caseId", incoming.getCaseId());
		variables.put("status", incoming.getStatus());
		variables.put("subject", incoming.getSubject());
		variables.put("body", incoming.getBody());
		variables.put("official", true);
		variables.put("response", null);

		runtimeService.startProcessInstanceByKey("eCorrespondence", variables);
		LOGGER.debug("end receiveIncoming");
		return variables;
	}

	@Override
	public void completeTask(String taskId) {
		LOGGER.debug("start completeTask, id: {}", taskId);
		Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
		if (task == null) {
			throw new IllegalStateException("ERROR: Task is not active");
		}
		taskService.complete(taskId);
		LOGGER.debug("end completeTask");
	}

	/**
	 * Check that response exists before completing the response task.
	 * @deprecated
	 */
	@Override
	public void completeRespondTask(String id) {
		LOGGER.debug("start completeTask");
		Task task = taskService.createTaskQuery().taskId(id).active().singleResult();
		if (task == null) {
			throw new IllegalStateException("ERROR: Task is not active");
		}
		else if (!task.getTaskDefinitionKey().equals("respond")) {
			throw new IllegalStateException("ERROR: Task must be a respond task. Instead, it is a "
						+ task.getTaskDefinitionKey() + " task");
		}
		
//		String executionId = taskService.createTaskQuery()
//				.taskId(id)
//				.singleResult()
//				.getExecutionId();
//		Map<String, Object> variables = runtimeService.getVariables(executionId);
//
//		@SuppressWarnings("unchecked")
//		Map<String, Object> response  = (Map<String, Object>) variables.get("response");
//		
//		if (response == null) {
//			throw new IllegalStateException("Cannot complete Response task without creating a response!");
//		}
//		
//		LOGGER.info("Sending email. Subject: "
//						+ response.get("subject")
//						+ ", Body: " + response.get("body"));

		taskService.complete(id);
		
		LOGGER.debug("end completeTask");
	}

	@Override
	public ECorrTask getTaskById(String taskId) {
		LOGGER.debug("start getTaskById: {}", taskId);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		LOGGER.debug("end getTaskById");
		return makeEcorrTask(task);
	}

	@Override
	public List<ECorrTask> getTaskByCaseId(int caseId) {
		LOGGER.debug("start getTaskByCaseId: {}", caseId);
		List<Task> tasks = taskService.createTaskQuery().processVariableValueEquals("caseId", caseId).list();
		LOGGER.debug("end getTaskById");
		return makeEcorrTasks(tasks);
	}

	@Override
	public List<ECorrTask> searchTasks(TaskQueryFilter tqf) {
		LOGGER.debug("start searchTasks, filter: {}", tqf);
		List<ECorrTask> eCorrTasks = new ArrayList<>();

		//
		// Query for all active tasks, optionally filtering by definition key
		//
		TaskQuery tq = taskService.createTaskQuery().active();
		if (tqf.getTaskDefinition() != null && !tqf.getTaskDefinition().equals("all")) {
			tq = tq.taskDefinitionKey(tqf.getTaskDefinition());
		}
		List<Task> tasks = tq.list();

		for (Task task : tasks) {
			eCorrTasks.add(makeEcorrTask(task));
		}
		LOGGER.debug("end searchTasks");
		return eCorrTasks;
	}

	@Override
	public String updateResponse(String id, ResponseCorrespondence rc) {
		LOGGER.debug("start searchTasks, id: {}, response: {}", id, rc);

		Map<String, Object> response = new HashMap<>();
		response.put("subject", rc.getSubject());
		response.put("body", rc.getBody());
		
		String executionId = taskService.createTaskQuery()
										.taskId(id)
										.singleResult()
										.getExecutionId();
		runtimeService.setVariable(executionId, "response", response);

		LOGGER.debug("end searchTasks");
		return executionId;
	}

	private List<ECorrTask> makeEcorrTasks(List<Task> tasks) {
		return tasks.stream()
			.map(task -> makeEcorrTask(task))
			.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private ECorrTask makeEcorrTask(Task task) {
		String executionId = task.getExecutionId();
		ECorrTask eCorrTask = new ECorrTask();
		String clientName = (String) runtimeService.getVariable(executionId, "clientName");
		eCorrTask.setName(clientName);
		int caseId = (int) runtimeService.getVariable(executionId, "caseId");
		eCorrTask.setCaseId(caseId);
		int status = (int) runtimeService.getVariable(executionId, "status");
		eCorrTask.setStatus(status);
		String subject = (String) runtimeService.getVariable(executionId, "subject");
		eCorrTask.setVariable("subject", subject);
		String body = (String) runtimeService.getVariable(executionId, "body");
		eCorrTask.setVariable("body", body);
		eCorrTask.setVariable("activityName", task.getName());
		eCorrTask.setVariable("taskId", task.getId());
		eCorrTask.setVariable("businessKey", task.getTaskDefinitionKey());
		Map<String, Object> response = new HashMap<>();
		response = (Map<String, Object>) runtimeService.getVariable(executionId, "response");
		if (response != null) {
			eCorrTask.setVariable("responseSubject", response.get("subject"));
			eCorrTask.setVariable("responseBody", response.get("body"));
		}
		return eCorrTask;
	}

}
