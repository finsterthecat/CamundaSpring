package org.camunda.bpm.getstarted.correspondence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("correspondence")
public class CorrespondenceController {
	@Inject
	private RuntimeService runtimeService;
	@Inject
	private TaskService taskService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondenceController.class);

	@RequestMapping(value = "/incoming", method = RequestMethod.PUT, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> receiveIncoming(@RequestBody IncomingCorrespondence incoming) {
		LOGGER.debug("start receiveIncoming");
		Map<String, Object> variables = new HashMap<>();
		variables.put("clientName", incoming.getClientName());
		variables.put("caseId", incoming.getCaseId());
		variables.put("status", incoming.getStatus());
		variables.put("subject", incoming.getSubject());
		variables.put("body", incoming.getBody());
		variables.put("official", true);

		runtimeService.startProcessInstanceByKey("eCorrespondence", variables);
		LOGGER.debug("end receiveIncoming");
		return variables;
	}

	@RequestMapping(value = "/tasks/{id}/complete", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void completeTask(@PathVariable String id) {
		LOGGER.debug("start completeTask");
		Task task = taskService.createTaskQuery().taskId(id).active().singleResult();
		if (task == null) {
			throw new IllegalStateException("ERROR: Task is not active");
		}
		taskService.complete(id);
		LOGGER.debug("end completeTask");
	}

	@RequestMapping(value = "/tasks/{id}/completeResponse", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void completeRespondTask(@PathVariable String id) {
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

	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ECorrTask getTaskById(@PathVariable String id) {
		LOGGER.debug("start getTaskById");
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		LOGGER.debug("end getTaskById");
		return makeEcorrTask(task);
	}

	@RequestMapping(value = "/tasks/searches", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<ECorrTask> searchTasks(@RequestBody TaskQueryFilter tqf) {
		LOGGER.debug("start searchTasks");
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

	@RequestMapping(value = "/tasks/{id}/response", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String taskUpdatResponse(@PathVariable String id,
									@RequestBody ResponseCorrespondence rc) {
		LOGGER.debug("start searchTasks");

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
