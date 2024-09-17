package alphaciment.base_iso.service;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.object.Document;

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
