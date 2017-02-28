package fr.treeptik.cloudunit.domain;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.repository.ImageRepository;
import fr.treeptik.cloudunit.domain.service.OrchestratorService;

@Configuration
@EnableConfigurationProperties
public class OrchestratorConfiguration {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    
    @Bean
    public CommandLineRunner syncImages(OrchestratorService service, ImageRepository repository) {
        return args -> { 
            List<Image> images = service.findAllImages();
            
            repository.deleteAll();
            
            images.forEach(image -> {
                repository.save(image);
            });
        };
    }
}
