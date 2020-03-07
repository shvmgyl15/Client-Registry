package in.org.projecteka.clientregistry.launch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "clientregistry.identity")
@Data
class IdentityServiceProperties {
    private String url;
    private String realm;
}
