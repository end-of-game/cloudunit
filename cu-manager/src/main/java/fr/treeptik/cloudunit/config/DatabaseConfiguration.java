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

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
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
@EnableJpaRepositories("fr.treeptik.cloudunit.dao")
@EnableTransactionManagement
@Profile({"production", "integration", "vagrant"})
public class DatabaseConfiguration {

    private Logger logger = LoggerFactory
        .getLogger(DatabaseConfiguration.class);

    @Value("${database.hostname}")
    private String databaseHostname;

    @Value("${database.port}")
    private String databasePort;

    @Value("${database.schema}")
    private String databaseSchema;

    @Value("${database.options}")
    private String databaseOptions;

    @Value("${database.user}")
    private String databaseUser;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.showSQL}")
    private String databaseShowSQL;

    @Value("classpath:/${database.script}")
    private Resource dataScript;

    @Bean
    public DataSource dataSource() {
        logger.debug("Configuring Datasource");
        String databaseUrl = String.format("jdbc:mysql://%s:%s/%s?%s",
                databaseHostname, databasePort, databaseSchema, databaseOptions);
        logger.debug("database.url:" + databaseUrl);
        logger.debug("database.user:" + databaseUser);
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("url", databaseUrl);
        config.addDataSourceProperty("user", databaseUser);
        config.setInitializationFailFast(false);
        config.setIdleTimeout(60000);
        String forcePassword = System.getenv("MYSQL_ROOT_PASSWORD");
        // coming from environnment host
        if (forcePassword != null) {
            logger.info("Force the mysql password from host env");
            databasePassword = forcePassword;
        }
        logger.info("URL : " + databaseUrl + " password : " + databasePassword);
        config.addDataSourceProperty("password", databasePassword);
        return new HikariDataSource(config);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(
        final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("utf-8");
        populator.addScript(dataScript);
        return populator;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        logger.debug("Configuring EntityManager");
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPersistenceProvider(new HibernatePersistenceProvider());
        lcemfb.setPersistenceUnitName("persistenceUnit");
        lcemfb.setDataSource(dataSource());
        lcemfb.setJpaDialect(new HibernateJpaDialect());
        lcemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lcemfb.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.generate_statistics", false);
        jpaProperties.put("hibernate.show_sql", Boolean.parseBoolean(databaseShowSQL));
        lcemfb.setJpaProperties(jpaProperties);
        lcemfb.setPackagesToScan("fr.treeptik.cloudunit.model");
        lcemfb.afterPropertiesSet();
        return lcemfb.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(Boolean.parseBoolean(databaseShowSQL));
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return jpaVendorAdapter;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
        return jpaTransactionManager;
    }
}
