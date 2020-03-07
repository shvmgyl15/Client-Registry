package in.org.projecteka.clientregistry.controller;

import in.org.projecteka.clientregistry.Client.Caller;
import in.org.projecteka.clientregistry.Client.IdentityService;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class Authenticator {

    private IdentityService identityService;

    public boolean isValidServiceRequest(String token) {
        Optional<Caller> verify = identityService.verify(token);
        return verify.isPresent() && verify.map(Caller::getIsServiceAccount).orElse(false);
    }
}
