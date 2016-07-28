package org.camunda.bpm.getstarted.correspondence;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Task")
public class ECorrTask {

	private String name;
	private int caseId;
	private int status;
	private Map<String, Object> variables= new HashMap<>();
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCaseId() {
		return caseId;
	}
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariable(String variable, Object value) {
		variables.put(variable, value);
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

}
