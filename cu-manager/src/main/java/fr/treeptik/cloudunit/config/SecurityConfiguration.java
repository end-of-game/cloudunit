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

package fr.treeptik.cloudunit.config;

import fr.treeptik.cloudunit.utils.CustomPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Class for Security Rules with
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration
    extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

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
    public AuthenticationManager authenticationManager()
        throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(WebSecurity web)
        throws Exception {
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
    public void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.jdbcAuthentication()
            .passwordEncoder(passwordEncoder())
            .dataSource(dataSource)
            .usersByUsernameQuery(
                "Select login, password, 'true' as enabled from User where login=? and status!=0")
            .authoritiesByUsernameQuery(
                "Select u.login, r.description From Role r join User u on u.role_id=r.id where u.login=?");

        //auth.inMemoryAuthentication().withUser("john").password("doe").roles("ADMIN, USER");
    }

    @Override
    protected void configure(HttpSecurity http)
        throws Exception {

        // Login Form
        http.formLogin()
            .loginProcessingUrl("/user/authentication")
            .successHandler(ajaxAuthenticationSuccessHandler)
            .failureHandler(ajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password").permitAll();

        // Logout
        http.logout()
            .logoutUrl("/user/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .deleteCookies("JSESSIONID", "XSRF-TOKEN", "isLogged").invalidateHttpSession(true).permitAll();

        // CSRF protection
        // enable for any profils
        activateProtectionCRSF(http);
        // enable for any profils
        disableProtectionCRSF(http);

        // Routes security
        http.authorizeRequests()
            .antMatchers("/application/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/server/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/module/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/file/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/image/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/volume/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/user/get-cloudunit-instance").permitAll()
            .antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/logs/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/snapshot/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/monitoring/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/messages/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/scripting/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/gitlab/**").permitAll()
            .antMatchers("/nopublic/**").permitAll().and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        if ("true".equals(System.getProperty("httpsOnly"))) {
            logger.info("launching the application in HTTPS-only mode");
            http.requiresChannel().anyRequest().requiresSecure();
        }
    }

    /**
     * Protection CSRF is critical for production env only
     *
     * @param http
     * @throws Exception
     */

    @Profile({"production"})
    private void activateProtectionCRSF(HttpSecurity http)
        throws Exception {
        // CSRF protection
        http.csrf()
            .csrfTokenRepository(csrfTokenRepository()).and()
            .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
    }

    /**
     * Protection CSRF is not necessary to CI
     *
     * @param http
     * @throws Exception
     */
    @Profile("test")
    private void disableProtectionCRSF(HttpSecurity http)
        throws Exception {
        http.csrf().disable();
    }

    /**
     * Filter CRSF to add XSFR-TOKEN between exchange
     *
     * @return
     */
    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                        && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    /**
     * Bijectiv Custome encoder
     *
     * @return
     */
    @Bean
    public CustomPasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }
}
