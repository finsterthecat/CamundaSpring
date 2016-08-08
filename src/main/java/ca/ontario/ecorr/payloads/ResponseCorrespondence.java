package ca.ontario.ecorr.payloads;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IncomingCorrespondence")
public class ResponseCorrespondence {
	private String subject;
	private String body;
	
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
