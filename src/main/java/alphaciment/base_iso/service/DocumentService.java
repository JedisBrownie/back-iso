package alphaciment.base_iso.service;

import java.sql.*;
import alphaciment.base_iso.model.connection.*;
import alphaciment.base_iso.model.object.Document;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    public void addDocument(String titre,int type,int confidentiel) throws Exception{
        Document doc = new Document();
        int idProcessusGlobal = 5000;
        try( Connection connection = IsoDataSource.getConnection();){
            doc.addDocument(titre, idProcessusGlobal, type, confidentiel, connection);
        } catch (Exception e) {
            throw e;
        }
    }


}
