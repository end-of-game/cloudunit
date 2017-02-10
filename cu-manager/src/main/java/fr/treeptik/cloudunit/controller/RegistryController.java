package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.dto.RegistryResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Registry;
import fr.treeptik.cloudunit.service.RegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registry")
public class RegistryController {

    @Inject
    private RegistryService registryService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<List<RegistryResource>> listAllRegistry()
            throws ServiceException {
        List<Registry> registries = registryService.findAll();
        List<RegistryResource> registryList = registries.stream().map(RegistryResource::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(registryList);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public
    @ResponseBody
    void postRegistry(@RequestBody RegistryResource registry)
            throws ServiceException {
        registryService.createNewRegistry(registry.getEndpoint(), registry.getUsername(), registry.getPassword(), registry.getEmail());
    }
}
