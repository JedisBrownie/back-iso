package alphaciment.base_iso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;

import alphaciment.base_iso.model.object.iso.ProcessusGlobal;
import alphaciment.base_iso.repository.iso.ProcessusGlobalRepository;
import jakarta.transaction.Transactional;

@Service
public class ProcessusService {

    @Autowired
    ProcessusGlobalRepository processusGlobalRepository;

    // @Qualifier("transactionManagerIso")
    // private PlatformTransactionManager transactionManager;

    public List<ProcessusGlobal> getAllProcessusGlobal()throws Exception{
        List<ProcessusGlobal> liste = new ArrayList<>();
        try{
            liste =  processusGlobalRepository.findAll();
        }catch(Exception e){
            throw e;
        }
        return liste;
    } 


    
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
