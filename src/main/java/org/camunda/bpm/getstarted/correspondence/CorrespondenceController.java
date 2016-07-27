package org.camunda.bpm.getstarted.correspondence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.getstarted.loanapproval.Employee;
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
	Employee employee;
	@Inject
	private RuntimeService runtimeService;

	@RequestMapping(value = "/intake", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> intake(@RequestBody IncomingCorrespondence incoming) {
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

	@RequestMapping(value = "/task/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Task getTaskById(@PathVariable int id) {
		return makeTask(id, "xxxx");
	}

	@RequestMapping(value = "/tasksxxx", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<>();
		List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey("eCorrespondence").list();
		System.out.println("instances count is " + instances.size());
		for (ProcessInstance instance : instances) {
			List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(instance.getId()).list();
			if (executions.size() > 0) {
				Task task  = new Task();
				String executionId = executions.get(0).getId();
				String clientName = (String) runtimeService.getVariable(executionId, "clientName");
				task.setName(clientName);
				int caseId = (int) runtimeService.getVariable(executionId, "caseId");
				task.setCaseId(caseId);
				int status = (int) runtimeService.getVariable(executionId, "status");
				task.setStatus(status);
				String subject = (String) runtimeService.getVariable(executionId, "subject");
				task.setVariable("subject", subject);
				String body = (String) runtimeService.getVariable(executionId, "body");
				task.setVariable("body", body);
				tasks.add(task);
			}
		}
		return tasks;
	}


	@RequestMapping(value = "/tasks", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Task> getTasks2() {
		List<Task> tasks = new ArrayList<>();
		List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey("eCorrespondence").list();
		System.out.println("instances count is " + instances.size());
		for (ProcessInstance instance : instances) {
			ActivityInstance activity = runtimeService.getActivityInstance(instance.getId());
			ActivityInstance activity2 = activity.getChildActivityInstances()[0];
			String activityName = activity2.getActivityName();
			String activityType = activity2.getActivityType();
			int executionCount = activity.getExecutionIds().length;
			String[] executions = activity.getExecutionIds();
			if (executions.length > 0) {
				Task task  = new Task();
				String executionId = executions[0];
				String clientName = (String) runtimeService.getVariable(executionId, "clientName");
				task.setName(clientName);
				String processId = instance.getId();
				task.setVariable("processId", processId);
				int caseId = (int) runtimeService.getVariable(executionId, "caseId");
				task.setCaseId(caseId);
				int status = (int) runtimeService.getVariable(executionId, "status");
				task.setStatus(status);
				String subject = (String) runtimeService.getVariable(executionId, "subject");
				task.setVariable("subject", subject);
				String body = (String) runtimeService.getVariable(executionId, "body");
				task.setVariable("body", body);
				task.setVariable("activityName", activityName);
				task.setVariable("activityType", activityType);
				task.setVariable("executionCount", executionCount);
				task.setVariable("processId", instance.getId());
				task.setVariable("childActivityCount", activity2.getChildActivityInstances().length);
				tasks.add(task);
			}
		}
		return tasks;
	}

	@RequestMapping(value = "/tasksstub/{fromId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Task> getTasksStub(@PathVariable int fromId) {
		ArrayList<Task> tasks = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			tasks.add(makeTask(fromId + i, "taskName"));
		}
		return tasks;
	}

	private Task makeTask(int id, String name) {
		Task task = new Task();
		task.setName("taskName");
		task.setCaseId(id);
		task.setStatus(0);
		long l_id = id;
		task.setVariable("first", Long.valueOf(l_id * l_id));
		task.setVariable("var", "value " + id);
		return task;
	}

}
