package in.org.projecteka.clientregistry.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionRefreshRequest {
    String clientId;
    String clientSecret;
    GrantType grantType;
    String refreshToken;
}