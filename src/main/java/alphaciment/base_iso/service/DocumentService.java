package alphaciment.base_iso.service;

import java.sql.Connection;
import java.sql.Date;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.object.Document;




@Service
public class DocumentService {


    public int addDocumentDraft(String titre, int type, Date miseEnApplication, boolean confidentiel, String userMatricule, String data) throws Exception {
        Document document = new Document();

        try (Connection connection = IsoDataSource.getConnection()) {
            Document lastDoc = document.addDocumentDraft(connection, titre, type, miseEnApplication, confidentiel, userMatricule);
            document.addDocumentFields(lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), data);
        } catch (Exception e) {
            throw e;
        }
        return 3;
    }


    // public void addDocument(String titre, int type, boolean confidentiel, int idProcessusLie) throws Exception {
    //     Document doc = new Document();
    //     // int idProcessusGlobal = 5000;
    //     try(Connection connection = IsoDataSource.getConnection()) {
    //         doc.addDocument(connection, titre, type, idProcessusLie, confidentiel);
    //     } catch (Exception e) {
    //         throw e;
    //     }
    // }

    // public void addDocumentBrouillon(String titre,int idTypeDocument,boolean confidentiel,int idApprobateur,int idVerificateur,String[] processusGlobal,String[] processusLie,int idUtilisateur) throws Exception{
    //     int idProcessusLie = Integer.parseInt(processusLie[0]);
    //     Connection connection = IsoDataSource.getConnection();
    //     connection.setAutoCommit(false);

    //     try{
    //         Document doc = new Document().addDocument(titre,idTypeDocument,idProcessusLie,confidentiel,idApprobateur,idVerificateur,connection);
    //         System.out.println("From service " + doc.getReferenceDocument() + " | " + doc.getIdDocument());
    //         // new ProcessusLie().insertProcessusLieOfDocument(doc.getReferenceDocument(),doc.getIdDocument(),processusLie, connection);
    //         // new ProcessusGlobal().insertProcessusOfDocument(doc.getReferenceDocument(), doc.getIdDocument(), processusGlobal, connection);
    //         // new HistoriqueEtat().saveHistoriqueEtatSansMotif(doc.getReferenceDocument(),doc.getIdDocument(),1,idUtilisateur,connection);
            
    //         connection.commit();
    //     }catch(Exception e){
    //         connection.rollback();
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    // }
    
    // public void addDocumentRedaction(String titre,int idTypeDocument,boolean confidentiel,int idApprobateur,int idVerificateur,String[] processusGlobal,String[] processusLie,String[] lecteur,String[] redacteur,int idUtilisateur) throws Exception{
    //     int idProcessusLie = Integer.parseInt(processusLie[0]);
    //     Connection connection = IsoDataSource.getConnection();
    //     connection.setAutoCommit(false);

    //     try{
    //         Document doc = new Document().addDocument(titre,idTypeDocument,idProcessusLie,confidentiel,idApprobateur,idVerificateur,connection);
    //         new ProcessusLie().insertProcessusLieOfDocument(doc.getReferenceDocument(),doc.getIdDocument(),processusLie, connection);
    //         new ProcessusGlobal().insertProcessusOfDocument(doc.getReferenceDocument(), doc.getIdDocument(), processusGlobal, connection);
    //         new HistoriqueEtat().saveHistoriqueEtatSansMotif(doc.getReferenceDocument(),doc.getIdDocument(),2,idUtilisateur,connection);
    //         System.out.println("Enregistre daoly hatreto");            
            
    //         new Utilisateur().saveRedacteurDocument(doc.getReferenceDocument(), doc.getIdDocument(),redacteur,idUtilisateur,connection);
    //         if(confidentiel == true){
    //             new Utilisateur().saveLecteurDocument(doc.getReferenceDocument(),doc.getIdDocument(),lecteur, connection);
    //         }
            
    //         connection.commit();
    //     }catch(Exception e){
    //         connection.rollback();
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    // }

    // public void addEnregistrementBrouillon(String titre,int idTypeDocument,String[] processusGlobal,String[] processusLie,boolean confidentiel,int idUtilisateur) throws Exception{
    //     int idProcessusLie = Integer.parseInt(processusLie[0]);
    //     Connection connection = IsoDataSource.getConnection();
    //     connection.setAutoCommit(false);

    //     try{
    //         Document doc = new Document().addEnregistrement(titre, idTypeDocument, idProcessusLie,confidentiel,connection);
    //         System.out.println("From service " + doc.getReferenceDocument() + " | " + doc.getIdDocument());
    //         // new ProcessusLie().insertProcessusLieOfDocument(doc.getReferenceDocument(),doc.getIdDocument(),processusLie, connection);
    //         // new ProcessusGlobal().insertProcessusOfDocument(doc.getReferenceDocument(), doc.getIdDocument(), processusGlobal, connection);
    //         // new HistoriqueEtat().saveHistoriqueEtatSansMotif(doc.getReferenceDocument(),doc.getIdDocument(),1,idUtilisateur,connection);
            
    //         connection.commit();
    //     }catch(Exception e){
    //         connection.rollback();
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    // }

    // public void addEnregistrementRedaction(String titre,int idTypeDocument,String[] processusGlobal,String[] processusLie,boolean confidentiel,String[] lecteur,String[] redacteur,int idUtilisateur) throws Exception{
    //     int idProcessusLie = Integer.parseInt(processusLie[0]);
    //     Connection connection = IsoDataSource.getConnection();
    //     connection.setAutoCommit(false);

    //     try{
    //         Document doc = new Document().addEnregistrement(titre, idTypeDocument, idProcessusLie,confidentiel,connection);
    //         new ProcessusLie().insertProcessusLieOfDocument(doc.getReferenceDocument(),doc.getIdDocument(),processusLie, connection);
    //         new ProcessusGlobal().insertProcessusOfDocument(doc.getReferenceDocument(), doc.getIdDocument(), processusGlobal, connection);
    //         new HistoriqueEtat().saveHistoriqueEtatSansMotif(doc.getReferenceDocument(),doc.getIdDocument(),2,idUtilisateur,connection);
    //         System.out.println("Enregistre daoly hatreto");            

    //         new Utilisateur().saveRedacteurDocument(doc.getReferenceDocument(), doc.getIdDocument(),redacteur,idUtilisateur,connection);
    //         if(confidentiel == true){
    //             new Utilisateur().saveLecteurDocument(doc.getReferenceDocument(),doc.getIdDocument(),lecteur, connection);
    //         }

    //         connection.commit();
    //     }catch(Exception e){
    //         connection.rollback();
    //         throw e;
    //     }finally{
    //         connection.close();
    //     }
    // }


    // public boolean verifRedacteurDocument(String reference,int idDocument,int idUtilisateur) throws Exception{
    //     boolean val = false;
        
    //     try(Connection connection = IsoDataSource.getConnection();){
    //         val = new Utilisateur().isRedacteur(reference, idDocument, idUtilisateur, connection);
    //     } catch (Exception e) {
    //         throw e;
    //     }
        
    //     return val;
    // }

}
