package fr.treeptik.cloudunit.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class StaticSpringApplicationContext implements ApplicationContextAware  {
    
	private static ApplicationContext context;

    @SuppressWarnings("static-access")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
    	this.context = context;
    }

    public static Object getBean(String beanName) {
      return context.getBean(beanName);
    }

}