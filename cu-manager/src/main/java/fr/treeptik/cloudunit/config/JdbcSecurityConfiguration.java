package fr.treeptik.cloudunit.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import fr.treeptik.cloudunit.utils.CustomPasswordEncoder;

@Configuration
@Profile("!ldap")
public class JdbcSecurityConfiguration extends AbstractSecurityConfiguration {
    private static final String USERS_QUERY =
        "select login, password, 'true' as enabled "
        + "from User "
        + "where login=? and status!=0";
    private static final String AUTHORITIES_QUERY =
        "select u.login, r.description "
        + "from Role r "
        + "join User u on u.role_id=r.id "
        + "where u.login=?";
    
    @Inject
    private DataSource dataSource;
    
    @Bean
    public CustomPasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
        .passwordEncoder(passwordEncoder())
        .dataSource(dataSource)
        .usersByUsernameQuery(USERS_QUERY)
        .authoritiesByUsernameQuery(AUTHORITIES_QUERY);
    }
}
