package alphaciment.base_iso.model.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class RhDataSource {
    private static final BasicDataSource rhds = new BasicDataSource();
    
    static{
        rhds.setUrl("jdbc:mysql://localhost/base_rh");
        rhds.setUsername("root");
        rhds.setPassword("password");
        rhds.setMinIdle(5);
        rhds.setMaxIdle(15);
        rhds.setMaxOpenPreparedStatements(200);
    }

    public static Connection getConnection() throws SQLException{
        return rhds.getConnection();
    }

    private RhDataSource(){}
}