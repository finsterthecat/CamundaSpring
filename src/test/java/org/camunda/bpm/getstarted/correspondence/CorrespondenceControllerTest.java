package org.camunda.bpm.getstarted.correspondence;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.camunda.bpm.getstarted.correspondence.payloads.IncomingCorrespondence;
import org.camunda.bpm.getstarted.correspondence.service.CorrespondenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {org.camunda.bpm.getstarted.configuration.TestContext.class},
						locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
@ActiveProfiles("dev")
public class CorrespondenceControllerTest {

	private MockMvc mockMvc;
	
	@Inject
	private WebApplicationContext webApplicationContext;
	
	@Inject
	private CorrespondenceService correspondenceServiceMock;
	
	@Test
	public void testReceiveIncoming() {
		fail("Not yet implemented");
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		Mockito.reset(CorrespondenceService.class);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	public void receiveIncomingShouldEchoBlah() throws Exception{
		IncomingCorrespondence ic = new IncomingCorrespondence();
		ic.setBody("Body body");
		ic.setCaseId(123);
		ic.setClientName("Fred Flintstone");
		ic.setStatus(0);
		ic.setSubject("Subject1");
		
		when(correspondenceServiceMock.receiveIncoming(ic)).thenReturn(null);
		
		
	}
}
