package ca.ontario.ecorr.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.ontario.ecorr.payloads.ECorrTask;
import ca.ontario.ecorr.payloads.IncomingCorrespondence;
import ca.ontario.ecorr.payloads.ResponseCorrespondence;
import ca.ontario.ecorr.service.CorrespondenceService;
import ca.ontario.ecorr.util.TestUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
@WebAppConfiguration
@ActiveProfiles("production")
public class WorkflowCorrespondenceServiceTest {

	@Inject
	CorrespondenceService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowCorrespondenceServiceTest.class);
	
	@Test
	public void testSimpleWorkflowLifecycle() throws Exception {
		
		//Received a new incoming correspondence!
		IncomingCorrespondence incoming = new IncomingCorrespondence();
		incoming.setBody("body");
		incoming.setCaseId(123);
		incoming.setClientName("client name");
		incoming.setStatus(3);
		incoming.setSubject("Wuddup?");

		//Let's tell the workflow!
		Map<String, Object> response = service.receiveIncoming(incoming);
		String r = new String(TestUtil.convertObjectToJsonBytes(response));
		LOGGER.info("Receive Incoming response: {}", r);
		
		//Did the workflow receive the incoming correctly?
		assertThat(response.get("body"), is(incoming.getBody()));
		assertThat(response.get("clientName"), is(incoming.getClientName()));
		assertThat(response.get("status"), is(incoming.getStatus()));
		assertThat(response.get("subject"), is(incoming.getSubject()));
		assertThat(response.get("caseId"), is(incoming.getCaseId()));
		
		//Query for the incoming based on the caseId and make sure it matches what was sent
		List<ECorrTask> tasks = service.getTaskByCaseId(incoming.getCaseId());
		assertThat(tasks.size(), is(1));
		ECorrTask task = tasks.get(0);
		assertThat(task.getCaseId(), is(incoming.getCaseId()));
		assertThat(task.getStatus(), is(incoming.getStatus()));
		assertThat(task.getVariables().get("subject"), is(incoming.getSubject()));
		assertThat(task.getVariables().get("body"), is(incoming.getBody()));
		//First step is triage
		assertThat(task.getVariables().get("businessKey"), is("triage"));
		
		//The taskId must be not null
		String taskId = (String) task.getVariables().get("taskId");
		LOGGER.info("TaskId: {}", taskId);
		assertThat(taskId, is(not(nullValue())));
		
		//Let's complete the triage task
		service.completeTask(taskId);
		
		//Should be one task, a respond task now.
		tasks = service.getTaskByCaseId(incoming.getCaseId());
		assertThat(tasks.size(), is(1));
		ECorrTask newTask = tasks.get(0);
		
		assertThat(newTask.getVariables().get("businessKey"), is("respond"));
		
		//Try to complete the task without creating a response - expect that the current task will remain respond.
		//Also, look for a note with a message regarding why the respond task did not advance
		service.completeTask((String)newTask.getVariables().get("taskId"));
		
		//Should be one task, a respond task still.
		tasks = service.getTaskByCaseId(incoming.getCaseId());
		assertThat(tasks.size(), is(1));
		ECorrTask newnewTask = tasks.get(0);
		String newnewTaskId = (String) newnewTask.getVariables().get("taskId");
		Map<String, Object> vars = newnewTask.getVariables();
		
		assertThat(vars.get("businessKey"), is("respond"));

		//Expecting 1 note re forgetting about the response.
		@SuppressWarnings({ "unchecked" })
		List<String> notes = (List<String>) vars.get("notes");
		assertThat(notes.size(), is(1));
		assertThat(notes.get(0), is("Hey! You forgot to create a response!"));	
		
		//Now create the response
		ResponseCorrespondence rc = new ResponseCorrespondence();
		rc.setSubject("We Feel Your Pain");
		rc.setBody("We empathize with your situation, but unfortunately are not in a position to help.\nSuggest you call a plumber.\n");
		service.updateResponse(newnewTaskId, rc);
		
		//Now we can complete the task and the workflow is complete!
		service.completeTask(newnewTaskId);
		
		//Should be no active tasks, as the response is sent and the workflow is closed!.
		tasks = service.getTaskByCaseId(incoming.getCaseId());
		assertThat(tasks.size(), is(0));
		
		//TODO check some history?
		
	}
}
