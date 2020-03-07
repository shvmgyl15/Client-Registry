package in.org.projecteka.clientregistry.controller;

import in.org.projecteka.clientregistry.Client.IdentityService;
import in.org.projecteka.clientregistry.Client.Session;
import in.org.projecteka.clientregistry.model.SessionRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SessionController extends BaseController {

    private final IdentityService identityService;

    @RequestMapping("/api/1.0/sessions")
    public Session with(@RequestBody SessionRequest session) {
        return identityService.getTokenFor(session.getClientId(), session.getClientSecret());
    }
}
