package org.camunda.bpm.getstarted.correspondence;

import java.util.Map;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SendEmailService implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailService.class);
	
	@Override
	public void execute(DelegateExecution de) throws Exception {
		Map<String, Object> variables = de.getVariables();
		@SuppressWarnings({ "unchecked" })
		Map<String, Object> response = (Map<String, Object>) variables.get("response");
		
		LOGGER.info("Sending email: subject={}, body={}", response.get("subject"), response.get("body"));
	}

}