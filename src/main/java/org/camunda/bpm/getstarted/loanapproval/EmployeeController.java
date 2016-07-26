package org.camunda.bpm.getstarted.loanapproval;
 
import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@Controller
public class EmployeeController {
    @Autowired
    Employee employee;
	@Inject	
	private RuntimeService runtimeService;
    
    @RequestMapping(value = "employees/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus( HttpStatus.OK)
    @ResponseBody
    public Employee getEmployeeInJSON(@PathVariable String name) {
        return getEmployee(name);    }
 
    @RequestMapping(value = "employees/{name}.xml", method = RequestMethod.GET, produces = "application/xml")
    @ResponseStatus( HttpStatus.OK)
    @ResponseBody
    public Employee getEmployeeInXML(@PathVariable String name) {
    	runtimeService.startProcessInstanceByKey("loanApproval");
        return getEmployee(name);
    }
    
    private Employee getEmployee(String name) {
        employee.setEmail(name + "@govonca.com");
        employee.setName(name);
        return employee;
    }
 
}
