package alphaciment.base_iso.model.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class IsoDataSource {
    private static final BasicDataSource isods = new BasicDataSource();
    
    static{
        isods.setUrl("jdbc:mysql://localhost/base_iso");
        isods.setUsername("root");
        isods.setPassword("root");
        isods.setMinIdle(5);
        isods.setMaxIdle(15);
        isods.setMaxOpenPreparedStatements(200);
    }

    public static Connection getConnection() throws SQLException{
        return isods.getConnection();
    }

    private IsoDataSource(){}
}
