package in.org.projecteka.clientregistry.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionRequest {
    String clientId;
    String clientSecret;
    GrantType grantType;
}