package fr.treeptik.cloudunit.config;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;

@Component
public class UserAuthenticationSuccess implements
		ApplicationListener<AuthenticationSuccessEvent> {

	private Logger logger = LoggerFactory
			.getLogger(UserAuthenticationSuccess.class);

	@Inject
	private UserService userService;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		try {
			User user = userService.findByLogin(((UserDetails) event
					.getAuthentication().getPrincipal()).getUsername());
			user.setLastConnection(new Date());
			System.out.println("****************************");
			System.out.println(new CustomPasswordEncoder().encode(user
					.getPassword()));
			System.out.println("****************************");

			System.out.println(new CustomPasswordEncoder()
					.decode(new CustomPasswordEncoder().encode(user
							.getPassword())));
			System.out.println("****************************");

			userService.update(user);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
