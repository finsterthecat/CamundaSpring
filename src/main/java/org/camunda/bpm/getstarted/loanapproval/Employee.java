package org.camunda.bpm.getstarted.loanapproval;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Employee")
@Named
@RequestScoped
public class Employee {

    String name;
    String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Employee() {

    }
}

