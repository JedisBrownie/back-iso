package alphaciment.base_iso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.object.iso.Etat;
import alphaciment.base_iso.repository.iso.EtatRepository;

@Service
public class DocumentService {

    // public void addDocument(String titre,int type,int confidentiel) throws Exception{
    //     Document doc = new Document();
    //     int idProcessusGlobal = 5000;
    //     try( Connection connection = IsoDataSource.getConnection();){
    //         doc.addDocument(titre, idProcessusGlobal, type, confidentiel, connection);
    //     } catch (Exception e) {
    //         throw e;
    //     }
    // }

    @Autowired
    EtatRepository etatRepository;

    public List<Etat> getAllEtats() {
        return etatRepository.findAll();
    }
    





}
