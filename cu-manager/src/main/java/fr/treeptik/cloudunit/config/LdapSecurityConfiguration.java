package fr.treeptik.cloudunit.config;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;

@Configuration
@Profile("ldap")
public class LdapSecurityConfiguration extends AbstractSecurityConfiguration {
    @Value("${security.ldap.urls:ldap://localhost:389}")
    private List<String> ldapUrls;
    
    @Value("${security.ldap.basedn:}")
    private String baseDn;

    @Value("${security.ldap.user.dn-patterns}")
    private String[] userDnPatterns;

    @Value("${security.ldap.user.objectclass:*}")
    private String userObjectClass;
    
    @Value("${security.ldap.group.search-base:}")
    private String groupSearchBase;

    @Value("${security.ldap.group.objectclass:*}")
    private String groupObjectClass;

    @Value("${security.ldap.manager.user}")
    private String managerUser;

    @Value("${security.ldap.manager.password}")
    private String managerPassword;

    @Value("${security.ldap.user.login-field:uid}")
    private String userLoginField;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication().userDetailsContextMapper(new InetOrgPersonContextMapper())
        .userSearchFilter(String.format("(&(objectclass=%s)(%s={0}))", userObjectClass, userLoginField))
        .groupSearchBase(groupSearchBase)
        .groupSearchFilter(String.format("(&(objectclass=%s)(member={0}))", groupObjectClass))
        .contextSource(contextSource());
    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrls, baseDn);
        contextSource.setUserDn(managerUser);
        contextSource.setPassword(managerPassword);
        return contextSource;
    }
}
