package in.org.projecteka.clientregistry.controller;

import in.org.projecteka.clientregistry.model.Organization;
import in.org.projecteka.clientregistry.repository.OrganizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/2.0/organizations")
@AllArgsConstructor
public class OrganizationController {
    private final OrganizationRepository organizationRepository;

    @GetMapping(produces = "application/json")
    public @ResponseBody List<Organization> getOrganizations(@RequestParam(value = "updatedSince", required = false) String updatedSince,
                                               @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                               @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                               @RequestParam(value = "name", required = false) String name) {
        try {
            List<Organization> organizations = organizationRepository.findAll(validString(name));
            return organizations;
        } catch (Exception e) {
            throw new BadRequest(e.getMessage());
        }
    }

    private String validString(String name) {
        return (name != null) ? name : "";
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public @ResponseBody Organization findOrganization(@PathVariable String id) {
        var org = organizationRepository.find(id);
        if (org == null) {
            throw new ResourceNotFoundException();
        }
        return org;
    }
}
