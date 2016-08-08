package ca.ontario.ecorr.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.ontario.ecorr.payloads.ECorrTask;
import ca.ontario.ecorr.payloads.IncomingCorrespondence;
import ca.ontario.ecorr.payloads.ResponseCorrespondence;
import ca.ontario.ecorr.payloads.TaskQueryFilter;
import ca.ontario.ecorr.service.CorrespondenceService;

@Controller
@RequestMapping("correspondence")
public class CorrespondenceController {
	@Inject
	private CorrespondenceService service;

	@RequestMapping(value = "/incoming", method = RequestMethod.PUT, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> receiveIncoming(@RequestBody IncomingCorrespondence incoming) {
		return service.receiveIncoming(incoming);
	}

	@RequestMapping(value = "/tasks/{id}/complete", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void completeTask(@PathVariable String id) {
		service.completeTask(id);
	}

	@RequestMapping(value = "/tasks/{id}/completeResponse", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void completeRespondTask(@PathVariable String id) {
		throw new UnsupportedOperationException("completeResponse method is no longer implemented");
	}

	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ECorrTask getTaskById(@PathVariable String id) {
		return service.getTaskById(id);
	}

	@RequestMapping(value = "/tasks/searches", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<ECorrTask> searchTasks(@RequestBody TaskQueryFilter tqf) {
		return service.searchTasks(tqf);
	}

	@RequestMapping(value = "/tasks/{id}/response", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String taskUpdatResponse(@PathVariable String id,
									@RequestBody ResponseCorrespondence rc) {
		return service.updateResponse(id, rc);
	}

}
