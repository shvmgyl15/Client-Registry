package in.org.projecteka.clientregistry.Client;

import in.org.projecteka.clientregistry.controller.BadRequest;
import lombok.AllArgsConstructor;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AllArgsConstructor
public class IdentityService {
    private final Logger logger = LoggerFactory.getLogger(IdentityService.class);
    private final RestTemplate client;
    private final String url;
    private final String realmName;

    public Session getTokenFor(String clientId, String clientSecret) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = requestWith(clientId, clientSecret);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<Session> response = client.exchange(format("%s/realms/%s/protocol/openid-connect/token",
                    url,
                    realmName),
                    HttpMethod.POST,
                    entity,
                    Session.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            logger.error(format("Something went wrong: %d", response.getStatusCodeValue()));
        } catch (RestClientException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
        // TODO: Use optional
        throw new BadRequest("Something went wrong");
    }

    public Optional<Caller> verify(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(APPLICATION_JSON);
            headers.setAccept(mediaTypes);
            headers.set(HttpHeaders.AUTHORIZATION, token);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
            var response = client.exchange(format("%s/realms/%s/protocol/openid-connect/userinfo",
                    url,
                    realmName),
                    HttpMethod.GET,
                    entity,
                    Properties.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.of(to(Objects.requireNonNull(response.getBody())));
            }
        } catch (RestClientException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
        return Optional.empty();
    }

    private MultiValueMap<String, String> requestWith(String clientId, String clientSecret) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "openid");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        return formData;
    }

    private static Caller to(Properties properties) {
        final String serviceAccountPrefix = "service-account-";
        var preferredUsername = properties.getProperty("preferred_username");
        var serviceAccount = preferredUsername.startsWith(serviceAccountPrefix);
        var userName = serviceAccount ? preferredUsername.substring(serviceAccountPrefix.length()) : preferredUsername;
        return new Caller(userName, serviceAccount);
    }
}
