package in.org.projecteka.clientregistry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import in.org.projecteka.clientregistry.Client.IdentityService;
import in.org.projecteka.clientregistry.Client.Session;
import in.org.projecteka.clientregistry.launch.CentralRegistryProperties;
import in.org.projecteka.clientregistry.model.SessionRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SessionController extends BaseController {

    private final IdentityService identityService;

    private final CentralRegistryProperties centralRegistryProperties;

    @RequestMapping("/api/1.0/sessions")
    public Session with(@RequestBody SessionRequest session) {
        return identityService.getTokenFor(session.getClientId(), session.getClientSecret());
    }

    @RequestMapping("/.well-known/openid-configuration")
    public JsonNode configuration() throws JsonProcessingException {
        return identityService.configuration(centralRegistryProperties.getHost());
    }

    @RequestMapping("/certs")
    public JsonNode certs() {
        return identityService.certs();
    }
}
