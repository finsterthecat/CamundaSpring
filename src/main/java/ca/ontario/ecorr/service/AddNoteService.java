package ca.ontario.ecorr.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class AddNoteService implements JavaDelegate {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AddNoteService.class);
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String noteText = (String) execution.getVariable("noteText");
		LOGGER.info("Note: {}", noteText);
		
		@SuppressWarnings("unchecked")
		List<String> notes = (List<String>) execution.getVariable("notes");
		if (notes == null) {
			notes = new ArrayList<>();
		}
		notes.add(noteText);
		
		execution.setVariable("notes", notes);
		LOGGER.info("Notes: {}", notes);
	}

}
