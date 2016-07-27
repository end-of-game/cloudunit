package fr.treeptik.cloudunitmonitor.conf;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource( { "classpath:/config.properties" } )
@EnableJpaRepositories( "fr.treeptik.cloudunitmonitor.dao" )
@EnableTransactionManagement
public class DatabaseConfiguration
{

    @Inject
    private Environment env;

    private Logger logger = LoggerFactory.getLogger( DatabaseConfiguration.class );

    @Bean
    public DataSource dataSource()
    {
        String mysqlURLPrefix = env.getProperty( "mysql.url.prefix" );
        String mysqlIP = ApplicationEntryPoint.IP_MYSQL;
        String mysqlURLSuffix = env.getProperty( "mysql.url.suffix" );
        String dbURL = mysqlURLPrefix + mysqlIP + mysqlURLSuffix;

        logger.debug( "Configuring Datasource" );
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName( "com.mysql.jdbc.jdbc2.optional.MysqlDataSource" );
        config.addDataSourceProperty( "databaseName", "cloudunit" );
        config.addDataSourceProperty( "url", dbURL );
        config.addDataSourceProperty( "user", env.getProperty( "db.user" ) );
        config.addDataSourceProperty( "password", ApplicationEntryPoint.MYSQL_PASSWORD );
        return new HikariDataSource( config );
    }

    @Bean
    public EntityManagerFactory entityManagerFactory()
    {
        logger.debug( "Configuring EntityManager" );
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPersistenceProvider( new HibernatePersistence() );
        lcemfb.setPersistenceUnitName( "persistenceUnit" );
        lcemfb.setDataSource( dataSource() );
        lcemfb.setJpaDialect( new HibernateJpaDialect() );
        lcemfb.setJpaVendorAdapter( jpaVendorAdapter() );

        Properties jpaProperties = new Properties();
        jpaProperties.put( "hibernate.cache.use_second_level_cache", true );
        jpaProperties.put( "hibernate.cache.use_query_cache", false );
        jpaProperties.put( "hibernate.generate_statistics", false );

        lcemfb.setJpaProperties( jpaProperties );

        lcemfb.setPackagesToScan( "fr.treeptik.cloudunit.model" );
        lcemfb.afterPropertiesSet();
        return lcemfb.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter()
    {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql( false );
        jpaVendorAdapter.setGenerateDdl( true );
        jpaVendorAdapter.setDatabasePlatform( "org.hibernate.dialect.MySQLDialect" );
        return jpaVendorAdapter;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator()
    {
        return new HibernateExceptionTranslator();
    }

    @Bean( name = "transactionManager" )
    public PlatformTransactionManager annotationDrivenTransactionManager()
    {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory( entityManagerFactory() );
        return jpaTransactionManager;
    }
}
