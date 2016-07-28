package org.camunda.bpm.getstarted.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IncomingCorrespondence")
public class TaskQueryFilter {
	private String taskDefinition;

	public String getTaskDefinition() {
		return taskDefinition;
	}

	public void setTaskDefinition(String taskDefinition) {
		this.taskDefinition = taskDefinition;
	}

}
