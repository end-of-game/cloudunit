package fr.treeptik.cloudunit.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by guillaume on 08/10/16.
 */

@Configuration
public class MailConfiguration {

    @Bean
    @Conditional(value = EmailActiveCondition.class)
    public JavaMailSender mailSender(@Value("${email.host}") String host,
                                     @Value("${email.port}") Integer port,
                                     @Value("${email.protocol}") String protocol,
                                     @Value("${email.username}") String username,
                                     @Value("${email.password}") String password) throws IOException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(javaMailProperties());
        return mailSender;
    }


    private Properties javaMailProperties() throws IOException {
        Properties properties = new Properties();
        return properties;
    }



}
