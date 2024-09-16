package alphaciment.base_iso.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.object.ProcessusGlobal;

@Service
public class ProcessusService {
    
    // public List<ProcessusGlobal> getAllProcessusGlobal() throws Exception{
    //     Connection connection = IsoDataSource.getConnection();
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     try{
    //         ProcessusGlobal pr = new ProcessusGlobal();
    //         liste = pr.getAllProcessusGlobal(connection);
    //     }catch(Exception e){
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    //     return liste;
    // }

    // public List<ProcessusGlobal> findProcessusOfDocument(String reference,int id) throws Exception{
    //     Connection connection = IsoDataSource.getConnection();
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     try{
    //         ProcessusGlobal pg = new ProcessusGlobal();
    //         liste = pg.findProcessusOfDocument(reference, id, connection);
    //     }catch(Exception e){
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    //     return liste;
    // }

    
}
