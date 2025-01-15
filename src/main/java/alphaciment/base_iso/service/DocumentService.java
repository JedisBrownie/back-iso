package alphaciment.base_iso.service;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.connection.RhDataSource;
import alphaciment.base_iso.model.object.Document;
import alphaciment.base_iso.model.object.User;




@Service
public class DocumentService {


    /**
     * Add Document Draft
     */
    public void addDocumentDraft(String titre, int type, String miseEnApplication, boolean confidentiel, String userMatricule, String data, List<MultipartFile> files) throws Exception {
        Document document = new Document();
        Connection isoConnection = IsoDataSource.getConnection();
        Connection hrConnection = RhDataSource.getConnection();

        try {
            Document lastDoc = document.addDocumentDraft(isoConnection, titre, type, miseEnApplication, confidentiel, userMatricule);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, ?>> dataList = objectMapper.readValue(data, new TypeReference<List<Map<String, ?>>>() {});
            dataList.forEach(entry -> {
                String reference = (String) entry.get("reference");
                if (!"typeDocument".equals(reference)) {
                    String champ = (String) entry.get("champ");
                    String value = (String) entry.get("valeur");
                    System.out.println(reference + " : " + value);
                    
                    if (!champ.equals("champDocumentDeSupport")) {
                        try {
                            document.addDocumentFields(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), reference, value);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }   
                    }

                    if (champ.equals("processusGlobal") || champ.equals("processusLie")) {
                        List<Integer> processIdList = (List<Integer>) entry.get("tableau_valeur");
                        int[] processIdArray = processIdList.stream().mapToInt(Integer::intValue).toArray();

                        for (int i = 0; i < processIdArray.length; i++) {
                            try {
                                document.addDocumentProcess(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), processIdArray[i], champ);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    User userForMatricule = null;
                    if (champ.equals("redacteur") || champ.equals("verificateur") || champ.equals("approbateur")) {
                        List<Map<String, String>> userList = (List<Map<String, String>>) entry.get("tableau_valeur");

                        for (Map<String, String> user : userList) {
                            String nom = user.get("nom");
                            String prenom = user.get("prenom");
    
                            try {
                                userForMatricule = User.getUserByFullName(hrConnection, prenom, nom);
                                document.addDocumentUserRole(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), userForMatricule.getUserMatricule(), 1, champ);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (champ.equals("diffusionEmail")) {
                        List<Map<String, String>> userList = (List<Map<String, String>>) entry.get("tableau_valeur");

                        for (Map<String, String> user : userList) {
                            String nom = user.get("nom");
                            String prenom = user.get("prenom");

                            try {
                                userForMatricule = User.getUserByFullName(hrConnection, prenom, nom);
                                document.addEmailDiffusion(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), userForMatricule.getUserMatricule(), userForMatricule.getUserEmail());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (champ.equals("documentDeSupport")) {
                        try {
                            for (MultipartFile file : files) {
                                String fileName = file.getOriginalFilename();
                                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
                                byte[] fileContent = file.getBytes();
                    
                                document.addDocumentAttachedFile(
                                    isoConnection,
                                    lastDoc.getReferenceDocument(),
                                    lastDoc.getIdDocument(),
                                    fileName,
                                    fileExtension,
                                    fileContent
                                );
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw e;
        } finally {
            isoConnection.close();
            hrConnection.close();
        }
    }


    /**
     * Add Document Validation
     */
    public void addDocumentValidation(String titre, int type, Date miseEnApplication, boolean confidentiel, String userMatricule, String data, List<MultipartFile> files) throws Exception {
        Document document = new Document();
        Connection isoConnection = IsoDataSource.getConnection();
        Connection hrConnection = RhDataSource.getConnection();

        try {
            Document lastDoc = document.addDocumentValidation(isoConnection, titre, type, miseEnApplication, confidentiel, userMatricule);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, ?>> dataList = objectMapper.readValue(data, new TypeReference<List<Map<String, ?>>>() {});
            dataList.forEach(entry -> {
                String reference = (String) entry.get("reference");
                if (!"typeDocument".equals(reference)) {
                    String champ = (String) entry.get("champ");
                    String value = (String) entry.get("valeur");
                    System.out.println(reference + " : " + value);
                    
                    if (!champ.equals("champDocumentDeSupport")) {
                        try {
                            document.addDocumentFields(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), reference, value);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }   
                    }

                    if (champ.equals("processusGlobal") || champ.equals("processusLie")) {
                        List<Integer> processIdList = (List<Integer>) entry.get("tableau_valeur");
                        int[] processIdArray = processIdList.stream().mapToInt(Integer::intValue).toArray();

                        for (int i = 0; i < processIdArray.length; i++) {
                            try {
                                document.addDocumentProcess(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), processIdArray[i], champ);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    User userForMatricule = null;
                    if (champ.equals("redacteur") || champ.equals("verificateur") || champ.equals("approbateur")) {
                        List<Map<String, String>> userList = (List<Map<String, String>>) entry.get("tableau_valeur");

                        for (Map<String, String> user : userList) {
                            String nom = user.get("nom");
                            String prenom = user.get("prenom");
    
                            try {
                                userForMatricule = User.getUserByFullName(hrConnection, prenom, nom);
                                document.addDocumentUserRole(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), userForMatricule.getUserMatricule(), 2, champ);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (champ.equals("diffusionEmail")) {
                        List<Map<String, String>> userList = (List<Map<String, String>>) entry.get("tableau_valeur");

                        for (Map<String, String> user : userList) {
                            String nom = user.get("nom");
                            String prenom = user.get("prenom");

                            try {
                                userForMatricule = User.getUserByFullName(hrConnection, prenom, nom);
                                document.addEmailDiffusion(isoConnection, lastDoc.getReferenceDocument(), lastDoc.getIdDocument(), userForMatricule.getUserMatricule(), userForMatricule.getUserEmail());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (champ.equals("documentDeSupport")) {
                        try {
                            for (MultipartFile file : files) {
                                String fileName = file.getOriginalFilename();
                                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
                                byte[] fileContent = file.getBytes();
                    
                                document.addDocumentAttachedFile(
                                    isoConnection,
                                    lastDoc.getReferenceDocument(),
                                    lastDoc.getIdDocument(),
                                    fileName,
                                    fileExtension,
                                    fileContent
                                );
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw e;
        } finally {
            isoConnection.close();
            hrConnection.close();
        }
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
