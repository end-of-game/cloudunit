package fr.treeptik.cloudunit.domain.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public abstract class Service {
	public static Service of(Application application, Image image) {
		ImageType type = image.getType();

		switch (type) {
		case SERVER:
			return new Server(application, image);

		case MODULE:
			return new Module(application, image);
		default:
			throw new IllegalArgumentException();
		}
	}

	private static String containerName(String applicationName, String basename) {
		return String.format("%s-%s", applicationName, basename);
	}

	private String imageName;
	private String name;
	private String containerName;
	private String containerUrl;
	private ContainerState state;
	private List<Deployment> deployments;

	protected Service() {
	}

	public Service(Application application, Image image) {
		this.name = image.getServiceName();
		this.containerName = containerName(application.getName(), image.getServiceName());
		this.imageName = image.getName();
		this.state = ContainerState.STOPPED;
		this.deployments = new ArrayList<>();
	}

	public String getImageName() {
		return imageName;
	}

	public String getName() {
		return name;
	}

	public ContainerState getState() {
		return state;
	}

	public void setState(ContainerState state) {
		this.state = state;
	}

	public String getContainerName() {
		return containerName;
	}

	public String getContainerUrl() {
		return containerUrl;
	}

	public void setContainerUrl(String containerUrl) {
		this.containerUrl = containerUrl;
	}

	public Deployment addDeployment(String contextPath, String fileUri) {
		Deployment deployment = new Deployment(contextPath, fileUri);
		deployments.add(deployment);
		return deployment;
	}

	public Collection<Deployment> getDeployments() {
		return Collections.unmodifiableCollection(deployments);
	}

	public Optional<Deployment> getDeployment(String contextPath) {
		return deployments.stream().filter(s -> s.getContextPath().equals(contextPath)).findAny();
	}

	public void removeDeployment(String contextPath) {
		getDeployment(contextPath).ifPresent(deployments::remove);
	}

	public abstract <R> R accept(ServiceVisitor<R> visitor);
}
