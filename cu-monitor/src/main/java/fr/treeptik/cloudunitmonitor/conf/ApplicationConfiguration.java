package fr.treeptik.cloudunitmonitor.conf;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import( value = { DatabaseConfiguration.class } )
@ComponentScan( basePackages = "fr.treeptik" )
@EnableScheduling
public class ApplicationConfiguration
{

}
