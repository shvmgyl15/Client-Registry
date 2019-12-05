package in.org.projecteka.clientregistry.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import in.org.projecteka.clientregistry.model.Resource;
import in.org.projecteka.clientregistry.repository.ResourceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/api/1.0/providers")
public class ProviderController extends BaseController {

    private ResourceRepository resourceRepository;

    public ProviderController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Resource> getProviders(@RequestParam(value = "updatedSince", required = false) String updatedSince,
                                @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                @RequestParam(value = "name", required = false) String name,
                                HttpServletRequest request) {

        checkForValidUserRequest(request);

        try {
            return resourceRepository.findResources("provider", updatedSince, offset, limit, name);
        } catch (ParseException e) {
            throw new BadRequest(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process request");
        }
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Resource getProvider(@PathVariable String id, HttpServletRequest request) {
        checkForValidUserRequest(request);
        Resource provider = resourceRepository.findResource("provider", id + ".json");
        if (provider == null) {
            throw new ResourceNotFoundException();
        }
        return provider;
    }

}
