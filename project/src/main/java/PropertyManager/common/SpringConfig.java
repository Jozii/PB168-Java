/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.common;

import PropertyManager.manager.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jozef
 */

@Configuration
@EnableTransactionManagement
@PropertySource(value = { "classpath:conf.properties" })
public class SpringConfig {
    @Autowired
    private Environment environment;
    
    @Bean
    public DataSource dataSource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(environment.getRequiredProperty("jdbc.url"));
        ds.setUsername(environment.getRequiredProperty("jdbc.user"));
        ds.setPassword(environment.getRequiredProperty("jdbc.password"));
        return ds;
    }
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public OwnerManager ownerManager() {
        return new OwnerManagerImpl(dataSource());
    }

    @Bean
    public PropertyManager propertyManager() {
        return new PropertyManagerImpl(dataSource());
    }

    @Bean
    public TitleDeedManager titleDeedManager() {
        return new TitleDeedManagerImpl(dataSource());
    }
}
