package fr.treeptik.cloudunit.orchestrator.docker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;

@Configuration
@EnableMongoRepositories
public class RepositoryConfiguration {
    
    @Bean
    @Profile("dev")
    public CommandLineRunner populateImages(ImageRepository repository) {
        return args -> {
            repository.deleteAll();
            repository.save(new Image("cloudunit/tomcat-8", ImageType.SERVER));
        };
    }
}
