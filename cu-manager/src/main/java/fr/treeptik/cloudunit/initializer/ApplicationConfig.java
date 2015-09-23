package fr.treeptik.cloudunit.initializer;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * @author Nicolas MULLER
 */
public class ApplicationConfig implements WebApplicationInitializer {
    private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationContext.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);
        dispatcher.setAsyncSupported(true);

        FilterRegistration.Dynamic security = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());
        EnumSet<DispatcherType> securityDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/user/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/file/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/logs/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/messages/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/application/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/server/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/snapshot/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/module/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/admin/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/image/*");
        security.addMappingForUrlPatterns(securityDispatcherTypes, false, "/nopublic/*");

        security.setAsyncSupported(true);

        servletContext.addListener(new ContextLoaderListener(rootContext));
    }
}
