package alphaciment.base_iso.model.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories(basePackages = "alphaciment.base_iso.model.connection",
                        entityManagerFactoryRef = "livresEntityManagerFactory",
                        transactionManagerRef = "livresTransactionManager")
public class IsoDataSource {

    @Bean
    @ConfigurationProperties("spring.datasource.iso")
    public DataSource mysqlDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost/testdb");
        dataSourceBuilder.username("dbuser");
        dataSourceBuilder.password("dbpass");
        return dataSourceBuilder.build();
    }



    
    // private static final BasicDataSource isods = new BasicDataSource();
    
    // static{
    //     isods.setUrl("jdbc:mysql://localhost/base_iso");
    //     isods.setUsername("root");
    //     isods.setPassword("root");
    //     isods.setMinIdle(5);
    //     isods.setMaxIdle(15);
    //     isods.setMaxOpenPreparedStatements(200);
    // }

    // public static Connection getConnection() throws SQLException{
    //     return isods.getConnection();
    // }

    // private IsoDataSource(){}
}
