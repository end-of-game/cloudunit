package fr.treeptik.springhibernate.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by guillaume on 18/01/17.
 */
@EnableAutoConfiguration
@ComponentScan("fr.treeptik")
public class Starter {

    public static void main(String[] args){
        SpringApplication.run(Starter.class, args);
    }

}
