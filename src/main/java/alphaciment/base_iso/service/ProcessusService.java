package alphaciment.base_iso.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import alphaciment.base_iso.model.object.*;
import alphaciment.base_iso.model.connection.IsoDataSource;

import org.springframework.stereotype.Service;

@Service
public class ProcessusService {
    
    public List<ProcessusGlobal> getAllProcessusGlobal() throws Exception{
        Connection connection = IsoDataSource.getConnection();
        List<ProcessusGlobal> liste = new ArrayList<>();
        try{
            ProcessusGlobal pr = new ProcessusGlobal();
            liste = pr.getAllProcessusGlobal(connection);
        }catch(Exception e){
            throw e;
        }finally{
            connection.close();
        }
        return liste;
    }

    
}
