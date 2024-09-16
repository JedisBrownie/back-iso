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
        entityManagerFactoryRef = "EntityMangerFactoryBeanIso",
        basePackages = "alphaciment.base_iso.repository.iso",
        transactionManagerRef = "TransactionManagerIso"
)
public class IsoDataSource {

    @Value("${spring.datasource.iso.url}")
    private String url;

    @Value("${spring.datasource.iso.username}")
    private String username;

    @Value("${spring.datasource.iso.password}")
    private String password;


    @Bean
    public DataSource mysqlIsoDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    @Primary
    @Bean(name = "EntityMangerFactoryBeanIso")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanIso(){
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(mysqlIsoDataSource());
        bean.setPackagesToScan("alphaciment.base_iso.model.object.iso");

        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        bean.setJpaVendorAdapter(adapter);

        return bean;
    }

    @Bean(name = "TransactionManagerIso")
    @Primary
    public PlatformTransactionManager transactionManagerIso(){
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(entityManagerFactoryBeanIso().getObject());
        return manager;
    }




}
