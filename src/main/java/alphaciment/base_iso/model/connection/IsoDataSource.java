package alphaciment.base_iso.model.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class IsoDataSource {

    // @Value("${spring.datasource.iso.url}")
    // private static String url;

    // @Value("${spring.datasource.iso.username}")
    // private static String username;

    // @Value("${spring.datasource.iso.password}")
    // private static String password;


    private static final BasicDataSource isods = new BasicDataSource();

    static{
        isods.setUrl("jdbc:mysql://localhost/base_iso");
        isods.setUsername("root");
        isods.setPassword("root");
        isods.setMinIdle(10);
        isods.setMaxIdle(25);
        isods.setMaxOpenPreparedStatements(300);
    }

    public static Connection getConnection() throws SQLException{
        return isods.getConnection();
    }
    
    private IsoDataSource(){ }



}
