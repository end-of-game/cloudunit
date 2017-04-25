package fr.treeptik.cloudunit.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableBinding(DomainChannels.class)
public class CloudUnitManagerDomainApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudUnitManagerDomainApplication.class, args);
	}
}
