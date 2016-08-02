package org.camunda.bpm.getstarted.correspondence.payloads;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IncomingCorrespondence")
public class IncomingCorrespondence {
	private String clientName;
	private int  caseId;
	private int status;
	private String subject;
	private String body;
	
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
