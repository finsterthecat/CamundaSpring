package org.camunda.bpm.getstarted.loanapproval;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.InitializingBean;

@Named
public class Starter implements InitializingBean {

	@Inject	
	private RuntimeService runtimeService;

	@Override
	public void afterPropertiesSet() throws Exception {
		runtimeService.startProcessInstanceByKey("loanApproval");
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
}
