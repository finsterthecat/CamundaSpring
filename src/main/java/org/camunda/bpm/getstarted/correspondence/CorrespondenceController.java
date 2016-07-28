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
import org.camunda.bpm.getstarted.loanapproval.Employee;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
	Employee employee;
	@Inject
	private RuntimeService runtimeService;
	@Inject
	private TaskService taskService;

	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation") // 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void conflict() {
		// Nothing to do
	}

	@RequestMapping(value = "/incoming", method = RequestMethod.PUT, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> receiveIncoming(@RequestBody IncomingCorrespondence incoming) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("clientName", incoming.getClientName());
		variables.put("caseId", incoming.getCaseId());
		variables.put("status", incoming.getStatus());
		variables.put("subject", incoming.getSubject());
		variables.put("body", incoming.getBody());
		variables.put("official", true);

		runtimeService.startProcessInstanceByKey("eCorrespondence", variables);
		return variables;
	}

	@RequestMapping(value = "/tasks/{id}/complete", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void completeTask(@PathVariable String id) {
		Task task = taskService.createTaskQuery().taskId(id).active().singleResult();
		if (task == null) {
			throw new DataIntegrityViolationException("Task is not active");
		}
		taskService.complete(id);
	}

	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ECorrTask getTaskById(@PathVariable String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		return makeEcorrTask(task);
	}

	@RequestMapping(value = "/tasks/searches", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<ECorrTask> searchTasks(@RequestBody TaskQueryFilter tqf) {
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
		return eCorrTasks;
	}

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
		return eCorrTask;
	}

}
