package in.org.projecteka.clientregistry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import in.org.projecteka.clientregistry.model.Organization;
import in.org.projecteka.clientregistry.model.Resource;
import in.org.projecteka.clientregistry.repository.OrganizationRepository;
import in.org.projecteka.clientregistry.repository.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/api/2.0/providers")
@AllArgsConstructor
public class ProviderController extends BaseController {

    private final ResourceRepository resourceRepository;

    @GetMapping(produces = "application/json")
    public @ResponseBody
    List<Resource> getProviders(@RequestParam(value = "updatedSince", required = false) String updatedSince,
                                @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                @RequestParam(value = "name", required = false) String name) {
        try {
            return resourceRepository.findResources("provider", updatedSince, offset, limit, name);
        } catch (ParseException e) {
            throw new BadRequest(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process request");
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public @ResponseBody
    Resource getProvider(@PathVariable String id) {
        Resource provider = resourceRepository.findResource("provider", id + ".json");
        if (provider == null) {
            throw new ResourceNotFoundException();
        }
        return provider;
    }
}