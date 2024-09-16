package alphaciment.base_iso.model.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "EntityMangerFactoryBeanRh",
        basePackages = "alphaciment.base_iso.repository.rh",
        transactionManagerRef = "TransactionManagerRh"
)
public class RhDataSource {

    @Value("${spring.datasource.rh.url}")
    private String url;

    @Value("${spring.datasource.rh.username}")
    private String username;

    @Value("${spring.datasource.rh.password}")
    private String password;

    @Bean
    public DataSource mysqlRhDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    @Bean(name = "EntityMangerFactoryBeanRh")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanRh(){
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(mysqlRhDataSource());
        bean.setPackagesToScan("alphaciment.base_iso.model.object.iso");

        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        bean.setJpaVendorAdapter(adapter);

        return bean;
    }

    @Bean(name = "TransactionManagerRh")
    public PlatformTransactionManager transactionManagerRh(){
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(entityManagerFactoryBeanRh().getObject());
        return manager;
    }







}