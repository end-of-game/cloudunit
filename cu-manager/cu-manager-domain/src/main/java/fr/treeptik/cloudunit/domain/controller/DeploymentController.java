package fr.treeptik.cloudunit.domain.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Deployment;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.resource.DeploymentResource;
import fr.treeptik.cloudunit.domain.service.ApplicationService;

@Controller
@RequestMapping("/applications/{appId}/services/{name}/deployments")
public class DeploymentController {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
    private ApplicationService applicationService;

	private DeploymentResource toResource(Application application, Service service, Deployment deployment) {
		DeploymentResource resource = new DeploymentResource(deployment);

		String appId = application.getId();
		String name = service.getName();
		String contextPath = deployment.getContextPath();

		resource.add(
				linkTo(methodOn(DeploymentController.class).getDeployment(appId, name, contextPath)).withSelfRel());
		resource.add(linkTo(methodOn(ServiceController.class).getService(appId, name)).withRel("cu:service"));
		resource.add(linkTo(methodOn(ApplicationController.class).getApplication(appId)).withRel("cu:application"));

		return resource;
	}

	@GetMapping
	public ResponseEntity<?> getDeployments(@PathVariable String appId, @PathVariable String name) {
		return withService(appId, name, (application, service) -> {
			Resources<DeploymentResource> resources = new Resources<>(service.getDeployments().stream()
					.map(deployment -> toResource(application, service, deployment)).collect(Collectors.toList()));

			resources.add(linkTo(methodOn(DeploymentController.class).getDeployments(appId, name)).withSelfRel());

			return ResponseEntity.ok(resources);
		});
	}

	@GetMapping("/{contextPath}")
	public ResponseEntity<?> getDeployment(@PathVariable String appId, @PathVariable String name,
			@PathVariable String contextPath) {
		return withService(appId, name, (application, service) -> {
			Optional<Deployment> deployment = service.getDeployment(contextPath);
			if(!deployment.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(toResource(application, service, deployment.get()));
		});
	}

	@PutMapping("/{contextPath}")
	public ResponseEntity<?> addDeployment(@PathVariable String appId, @PathVariable String name,
			@PathVariable String contextPath, @RequestPart("file") MultipartFile file) {
		return withService(appId, name, (application, service) -> {
			Deployment deployment = applicationService.addDeployment(application, service, contextPath, file);
			return ResponseEntity.ok(toResource(application, service, deployment));
        });
	}

	private ResponseEntity<?> withService(String appId, String name,
			BiFunction<Application, Service, ResponseEntity<?>> mapper) {
		return applicationRepository.findOne(appId)
				.flatMap(application -> application.getService(name).map(service -> mapper.apply(application, service)))
				.orElse(ResponseEntity.notFound().build());
	}

}
