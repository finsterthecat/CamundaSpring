package org.camunda.bpm.getstarted.configuration;

import org.camunda.bpm.getstarted.correspondence.service.CorrespondenceService;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Profile("dev")
@ComponentScan({"org.camunda.bpm.getstarted.correspondence.controller"
					, "org.camunda.bpm.getstarted.correspondence.service"})
@EnableWebMvc
public class TestContext {
    private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    @Bean
    public CorrespondenceService correspondenceService() {
        return Mockito.mock(CorrespondenceService.class);
    }

}
