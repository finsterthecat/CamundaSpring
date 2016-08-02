package org.camunda.bpm.getstarted.service;

import static org.junit.Assert.fail;

import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.getstarted.TestUtil;
import org.camunda.bpm.getstarted.correspondence.payloads.IncomingCorrespondence;
import org.camunda.bpm.getstarted.correspondence.service.CorrespondenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
@ActiveProfiles("production")
public class WorkflowCorrespondenceServiceTest {

	@Inject
	CorrespondenceService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowCorrespondenceServiceTest.class);
	
	@Test
	public void testReceiveIncoming() throws Exception {
		IncomingCorrespondence incoming = new IncomingCorrespondence();
		incoming.setBody("body");
		incoming.setCaseId(123);
		incoming.setClientName("client name");
		incoming.setStatus(3);
		incoming.setSubject("Wuddup?");

		Map<String, Object> response = service.receiveIncoming(incoming);
		String r = new String(TestUtil.convertObjectToJsonBytes(response));
		LOGGER.info("Receive Incoming response:" + r);
	}

	@Test
	public void testCompleteTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompleteRespondTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskById() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateResponse() {
		fail("Not yet implemented");
	}

}
