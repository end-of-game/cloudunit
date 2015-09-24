/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Inject
	private DataSource dataSource;

	@Inject
	private UserAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

	@Inject
	private UserAjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

	@Inject
	private UserAjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

	@Inject
	private Http401EntryPoint authenticationEntryPoint;

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers("/bower_components/*/**", "i18n/**", "css/**",
						"*.css", "*.js")
				.antMatchers("/fonts/**")
				.antMatchers("/resources/**")
				.antMatchers("/images/**")
				.antMatchers("/scripts/**")
				.antMatchers("/api-docs", "/api-docs/*", "/styles/**",
						"/user/signin", "/user/activate/userEmail/**");
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
				.passwordEncoder(passwordEncoder())
				.dataSource(dataSource)
				.usersByUsernameQuery(
						"Select login, password, 'true' as enabled from User where login=? and status!=0")
				.authoritiesByUsernameQuery(
						"Select u.login, r.description From Role r join User u on u.role_id=r.id where u.login=?");

		auth.inMemoryAuthentication().withUser("john").password("doe")
				.roles("ADMIN, USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.formLogin()
				// .loginPage("/resources/index.html")
				.loginProcessingUrl("/user/authentication")
				.successHandler(ajaxAuthenticationSuccessHandler)
				.failureHandler(ajaxAuthenticationFailureHandler)
				.usernameParameter("j_username")
				.passwordParameter("j_password").permitAll();

		http.logout().logoutUrl("/user/logout")
				.logoutSuccessHandler(ajaxLogoutSuccessHandler)
				.deleteCookies("JSESSIONID").invalidateHttpSession(true)
				.permitAll().and().csrf().disable();

		http.authorizeRequests()
				// .antMatchers("/resources/**").permitAll()
				.antMatchers("/application/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/server/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/module/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/file/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/image/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/user/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/logs/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/snapshot/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/monitoring/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/messages/**")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
				.antMatchers("/user/check", "/nopublic/**").permitAll().and()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint);
		// .and().headers().cacheControl().frameOptions();

	}

	@Bean
	public CustomPasswordEncoder passwordEncoder() {
		return new CustomPasswordEncoder();
	}
}
