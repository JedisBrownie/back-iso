package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Document {
    /**
     * Fields
     */
    String referenceDocument;
    int idDocument;
    String titreDocument;
    TypeDocument type;
    boolean confidentiel;

    Utilisateur approbateur;
    Utilisateur verificateur;

    List<ProcessusGlobal> processusGlobal;
    List<ProcessusLie> processusLie;
    
    List<Utilisateur> redacteur;
    List<Utilisateur> lecteur;
    List<Utilisateur> diffusionEmail;



    /**
     * Methods
     */
    public Document addDocumentDraft(Connection connection, String titre, int type, Date miseEnApplication, boolean confidentiel, String userMatricule) throws Exception {
        String insertDocSql = "INSERT INTO document(titre, id_type, date_creation, date_mise_application, confidentiel) VALUES (?, ?, CURRENT_DATE, ?, ?)";
        String lastDocSql = "SELECT ref_document, id_document FROM document WHERE ref_document = ?";
        String docStateSql = "INSERT INTO historique_etat(ref_document, id_document, id_etat, matricule_utilisateur, date_heure_etat) VALUES (?, ?, 1, ?, CURRENT_TIMESTAMP)";
        Document lastDoc = new Document();
        String lastId = null;

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement docStatement = connection.prepareStatement(insertDocSql, Statement.RETURN_GENERATED_KEYS)) {
                docStatement.setString(1, titre);
                docStatement.setInt(2, type);
                docStatement.setDate(3, miseEnApplication);
                docStatement.setBoolean(4, confidentiel);
    
                docStatement.executeUpdate();
    
                try (ResultSet generatedKeys = docStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastId = generatedKeys.getString(1);
                        // System.out.println("Generated ID: " + lastId);
                    }
                }
                docStatement.close();
            }

            try (PreparedStatement lastDocStatement = connection.prepareStatement(lastDocSql)) {
                lastDocStatement.setString(1, lastId);
                ResultSet rs = lastDocStatement.executeQuery();

                while (rs.next()) {
                    lastDoc.setReferenceDocument(rs.getString("ref_document"));
                    lastDoc.setIdDocument(rs.getInt("id_document"));
                }

                lastDocStatement.close();
            }


            try (PreparedStatement docStateStatement = connection.prepareStatement(docStateSql)) {
                docStateStatement.setString(1, lastDoc.getReferenceDocument());
                docStateStatement.setInt(2, lastDoc.getIdDocument());
                docStateStatement.setString(3, userMatricule);

                docStateStatement.executeUpdate();
                docStateStatement.close();
            }

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }

        return lastDoc;
    }


    public void addDocumentFields(String refDocument, int idDocument, String data) {
        System.out.println(data);
    }


    // public void addDocument(Connection connection, String titre, int type, int idProcessusLie, boolean confidentiel) throws Exception {

    //     String sql = "INSERT INTO DOCUMENT(titre, id_type, confidentiel, date_creation, id_entete) VALUES (?, ?, ?, CURRENT_DATE, ?)";

    //     try(PreparedStatement statement = connection.prepareStatement(sql) ){
    //         statement.setString(1,titre);
    //         statement.setInt(2, type);
    //         statement.setBoolean(3, confidentiel);
    //         statement.setInt(4, idProcessusLie);

    //         statement.executeUpdate();
    //         statement.close();
    //     } catch(Exception e) {
    //         throw e;
    //     } finally {
    //         connection.close();
    //     }
    // }

    // public Document addDocument(String titre, int idTypeDocument, int idProcessusLie, boolean confidentiel, int idApprobateur, int idVerificateur, Connection connection) throws Exception {
    //     String sql = "INSERT INTO DOCUMENT(titre, id_type, id_entete, date_creation, confidentiel, id_approbateur, id_validateur) " +
    //                 "VALUES(?, ?, ?, CURRENT_DATE, ?, ?, ?) RETURNING ref_document,id_document";
        
    //     PreparedStatement statement = null;
    //     ResultSet rs = null;
    //     Document document = new Document();

    //     try {
    //         statement = connection.prepareStatement(sql);

    //         statement.setString(1, titre);
    //         statement.setInt(2, idTypeDocument);
    //         statement.setInt(3, idProcessusLie);
    //         statement.setBoolean(4, confidentiel);
    //         statement.setInt(5, idApprobateur);
    //         statement.setInt(6, idVerificateur);

    //         rs = statement.executeQuery();
            
    //         while(rs.next()) {
    //             String ref = rs.getString("ref_document");
    //             int idDoc = rs.getInt("id_document");
    //             document.setReferenceDocument(ref);
    //             document.setIdDocument(idDoc);
    //         }
    //     } catch(Exception e) {
    //         throw e;   
    //     } finally {
    //         if(statement != null) {
    //             statement.close();
    //         }
    //         if(rs != null) {
    //             rs.close();
    //         }
    //     }
    //     return document;
    // }


    // public Document addEnregistrement(String titre, int idTypeDocument, int idProcessusLie, boolean confidentiel, Connection connection) throws Exception {
    //     String sql = "INSERT INTO DOCUMENT(titre, id_type, id_entete, date_creation, confidentiel) " +
    //                 "VALUES(?, ?, ?, CURRENT_DATE, ?) RETURNING ref_document,id_document";
        
    //     PreparedStatement statement = null;
    //     ResultSet rs = null;
    //     Document document = new Document();
    //     try{
    //         statement = connection.prepareStatement(sql);

    //         statement.setString(1, titre);
    //         statement.setInt(2, idTypeDocument);
    //         statement.setInt(3, idProcessusLie);
    //         statement.setBoolean(4, confidentiel);
            
    //         rs = statement.executeQuery();
            
    //         while(rs.next()){
    //             String ref = rs.getString("ref_document");
    //             int idDoc = rs.getInt("id_document");
    //             document.setReferenceDocument(ref);
    //             document.setIdDocument(idDoc);
    //         }

    //     }catch(Exception e){
    //         throw e;   
    //     }finally{
    //         if(statement != null){
    //             statement.close();
    //         }
    //         if(rs != null){
    //             rs.close();
    //         }
    //     }
    //     return document;
    // }
}
