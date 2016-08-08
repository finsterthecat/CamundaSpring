package ca.ontario.ecorr.correspondence;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.ontario.ecorr.payloads.IncomingCorrespondence;
import ca.ontario.ecorr.service.CorrespondenceService;
import ca.ontario.ecorr.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ca.ontario.ecorr.config.TestContext.class })
@WebAppConfiguration
@ActiveProfiles("dev")
public class CorrespondenceControllerTest {

	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private CorrespondenceService correspondenceServiceMock;

	@Before
	public void setup() {
		Mockito.reset(correspondenceServiceMock);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * Test that receiveIncoming will call the service and all parameters come through correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void receiveIncomingShouldReceiveIncomingAndReturnReceivedValue() throws Exception {
		IncomingCorrespondence ic = new IncomingCorrespondence();
		ic.setBody("Body body");
		ic.setCaseId(123);
		ic.setClientName("Fred Flintstone");
		ic.setStatus(0);
		ic.setSubject("Subject1");

		Map<String, Object> returnValue = new HashMap<>();
		returnValue.put("caseId", 123);
		returnValue.put("subject", "Pick a card");
		
		when(correspondenceServiceMock.receiveIncoming(any(IncomingCorrespondence.class)))
				.thenReturn(returnValue);

		mockMvc.perform(put("/correspondence/incoming")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(ic)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(TestUtil.APPLICATION_JSON_UTF8))			//Comes back APPLICATION_JSON - so compatible
				.andExpect(jsonPath("$.caseId", is(123)))
				.andExpect(jsonPath("$.subject", is("Pick a card")));

		//Expect receiveIncoming on the service is called once.
		ArgumentCaptor<IncomingCorrespondence> captor = ArgumentCaptor.forClass(IncomingCorrespondence.class);
		verify(correspondenceServiceMock, times(1)).receiveIncoming(captor.capture());
		verifyNoMoreInteractions(correspondenceServiceMock);
		
		//Incoming Correspondence is what was passed in.
		IncomingCorrespondence m = captor.getValue();
		assertThat(m.getBody(), is("Body body"));
		assertThat(m.getSubject(), is("Subject1"));
		assertThat(m.getStatus(), is(0));
		assertThat(m.getCaseId(), is(123));
	}
}
