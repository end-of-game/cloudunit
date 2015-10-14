/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@EnableAspectJAutoProxy
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"fr.treeptik.cloudunit.controller",
    "fr.treeptik.cloudunit.dao", "fr.treeptik.cloudunit.docker",
    "fr.treeptik.cloudunit.docker.model", "fr.treeptik.cloudunit.config",
    "fr.treeptik.cloudunit.exception", "fr.treeptik.cloudunit.model",
    "fr.treeptik.cloudunit.service", "fr.treeptik.cloudunit.service.impl",
    "fr.treeptik.cloudunit.utils", "fr.treeptik.cloudunit.aspects",
    "fr.treeptik.cloudunit.manager", "fr.treeptik.cloudunit.manager.impl",
    "fr.treeptik.cloudunit.validator"
})
@PropertySource({"classpath:/application.properties"})
@PropertySource({"classpath:/maven.properties"})
public class CloudUnitApplicationContext
    extends WebMvcConfigurerAdapter {

    // Max file size
    private static final int MAX_UPLOAD_SIZE = 300 * 1000 * 1000;

    private final static Logger logger = LoggerFactory.getLogger(CloudUnitApplicationContext.class);

    /*
    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addConverter(new StringToJsonInputConverter());
    }
    */

    @Bean
    @Profile("vagrant")
    public static PropertySourcesPlaceholderConfigurer properties()
        throws Exception {
        PropertySourcesPlaceholderConfigurer pspc =
            new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new Resource[]
            {new ClassPathResource("application-vagrant.properties")};
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        pspc.setLocalOverride(true);
        return pspc;
    }

    @Bean
    @Profile("production")
    public static PropertySourcesPlaceholderConfigurer propertiesForProduction()
        throws Exception {
        PropertySourcesPlaceholderConfigurer pspc =
            new PropertySourcesPlaceholderConfigurer();
        File customFile = new File(System.getProperty("user.home") + "/.cloudunit/configuration.properties");
        Resource[] resources = null;
        if (customFile.exists()) {
            resources =
                new Resource[]
                    {
                        new ClassPathResource("application-production.properties"),
                        new FileSystemResource(new File(System.getProperty("user.home") + "/.cloudunit/configuration.properties"))
                    };
        } else {
            logger.error(customFile.getAbsolutePath() + " is missing. It could generate configuration error");
            resources =
                new Resource[]
                    {
                        new ClassPathResource("application-production.properties"),
                    };
        }
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        pspc.setLocalOverride(true);
        return pspc;
    }

    @Bean
    @Profile("integration")
    public static PropertySourcesPlaceholderConfigurer propertiesForIntegration()
        throws Exception {
        PropertySourcesPlaceholderConfigurer pspc =
            new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new Resource[]
            {new ClassPathResource("application-integration.properties")};
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        pspc.setLocalOverride(true);
        return pspc;
    }

    @Bean
    @Profile("test")
    public static PropertySourcesPlaceholderConfigurer propertiesForTest()
        throws Exception {
        PropertySourcesPlaceholderConfigurer pspc =
            new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new Resource[]
            {new ClassPathResource("application-test.properties")};
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        pspc.setLocalOverride(true);
        return pspc;
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver() {
        logger.debug("Configuring the ContentNegotiatingViewResolver");
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

        UrlBasedViewResolver urlBasedViewResolver = new UrlBasedViewResolver();
        urlBasedViewResolver.setViewClass(JstlView.class);
        urlBasedViewResolver.setSuffix(".html");
        viewResolvers.add(urlBasedViewResolver);

        viewResolver.setViewResolvers(viewResolvers);

        List<View> defaultViews = new ArrayList<View>();
        defaultViews.add(new MappingJackson2JsonView());
        viewResolver.setDefaultViews(defaultViews);

        return viewResolver;
    }

    @Override
    public void configureDefaultServletHandling(
        DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    private MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate4Module().enable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION));
        messageConverter.setObjectMapper(mapper);
        return messageConverter;
    }

    @Override
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        return messageSource;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
        return multipartResolver;
    }

}
