package in.org.projecteka.clientregistry.launch;

import in.org.projecteka.clientregistry.Client.IdentityService;
import in.org.projecteka.clientregistry.controller.Authenticator;
import in.org.projecteka.clientregistry.repository.ResourceRepository;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@ComponentScan("in.org.projecteka.clientregistry")
public class ApplicationConfiguration extends WebMvcConfigurationSupport {
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public ResourceRepository getResourceRepository(ApplicationContext context) {
        return new ResourceRepository(context);
    }

    @Bean
    public IdentityService identityService(RestTemplate restTemplate, IdentityServiceProperties properties) {
        return new IdentityService(restTemplate, properties.getUrl(), properties.getRealm());
    }

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory requestFactory,
                                     RestTemplateErrorHandler restTemplateErrorHandler) {
        return new RestTemplateBuilder().requestFactory(() -> requestFactory)
                .errorHandler(restTemplateErrorHandler)
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("poolScheduler");
        scheduler.setPoolSize(50);
        return scheduler;
    }

    @Bean
    public Authenticator authenticator(IdentityService identityService) {
        return new Authenticator(identityService);
    }
}
