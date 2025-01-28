package alphaciment.base_iso.model.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;


public class RhDataSource {

    // @Value("${spring.datasource.rh.url}")
    // private static String url;

    // @Value("${spring.datasource.rh.username}")
    // private static String username;

    // @Value("${spring.datasource.rh.password}")
    // private static String password;

    private static final BasicDataSource rhds = new BasicDataSource();

    static{
        rhds.setUrl("jdbc:postgresql://localhost:5432/alpha_ciment_hr");
        // rhds.setUrl("jdbc:postgresql://10.192.193.81:5432/alpha_ciment_hr");
        rhds.setUsername("postgres");
        rhds.setPassword("root");
        rhds.setMinIdle(10);    
        rhds.setMaxIdle(25);
        rhds.setMaxOpenPreparedStatements(300);
    }

    public static Connection getConnection() throws SQLException{
        return rhds.getConnection();
    }

    private RhDataSource(){}






}