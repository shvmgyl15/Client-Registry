package in.org.projecteka.clientregistry.controller;


import in.org.projecteka.clientregistry.model.Resource;
import in.org.projecteka.clientregistry.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/api/1.0/providers")
public class ProviderController extends BaseController {

    @Autowired
    private ResourceRepository providers;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Resource getFacility(@PathVariable String id, HttpServletRequest request) {
        checkForValidUserRequest(request);
        Resource facility = providers.findResource("facility", id);
        if (facility == null) {
            throw new ResourceNotFoundException();
        }
        return facility;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Resource> listproviders(@RequestParam(value = "updatedSince", required = false) String updatedSince,
                                  @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                  @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                  HttpServletRequest request) {

        checkForValidUserRequest(request);

        try {
             return providers.findResources("facility", updatedSince, offset, limit);
        } catch (ParseException e) {
            throw new BadRequest(e.getMessage());
        }

    }


}
