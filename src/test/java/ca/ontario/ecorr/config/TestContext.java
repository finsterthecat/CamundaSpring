package ca.ontario.ecorr.config;

import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ca.ontario.ecorr.service.CorrespondenceService;

@Configuration
@Profile("dev")
@ComponentScan({"ca.ontario.ecorr.controller"
					, "ca.ontario.ecorr.service"})
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
