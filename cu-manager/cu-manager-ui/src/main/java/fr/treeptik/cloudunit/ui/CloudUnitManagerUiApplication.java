package fr.treeptik.cloudunit.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class CloudUnitManagerUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudUnitManagerUiApplication.class, args);
	}
}
