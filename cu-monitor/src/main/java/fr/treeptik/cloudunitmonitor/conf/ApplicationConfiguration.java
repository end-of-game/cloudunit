package fr.treeptik.cloudunitmonitor.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import( value = { DatabaseConfiguration.class } )
@ComponentScan( basePackages = "fr.treeptik" )
@EnableScheduling
public class ApplicationConfiguration
{

}
