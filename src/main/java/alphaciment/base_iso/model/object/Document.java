package alphaciment.base_iso.model.object;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    /**
     * Add Document Draft
     */
    public Document addDocumentDraft(Connection connection, String titre, int type, String miseEnApplication, boolean confidentiel, String userMatricule) throws Exception {
        String insertDocSql = "INSERT INTO document(titre, id_type, date_creation, date_mise_application, confidentiel) VALUES (?, ?, CURRENT_DATE, ?, ?)";
        
        String lastDocSql = "SELECT ref_document, id_document FROM document WHERE ref_document = ?";
        String docStateSql = "INSERT INTO historique_etat(ref_document, id_document, id_etat, matricule_utilisateur, date_heure_etat) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        Document lastDoc = new Document();
        String lastId = null;
    
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement docStatement = connection.prepareStatement(insertDocSql, Statement.RETURN_GENERATED_KEYS)) {
                docStatement.setString(1, titre);
                docStatement.setInt(2, type);
    
                if (miseEnApplication == null || miseEnApplication.trim().isEmpty() || "".equals(miseEnApplication)) {
                    docStatement.setNull(3, java.sql.Types.DATE);
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date utilDate = format.parse(miseEnApplication);
                    Date sqlDate = new Date(utilDate.getTime());
                    docStatement.setDate(3, sqlDate);
                }
    
                docStatement.setBoolean(4, confidentiel);
                docStatement.executeUpdate();
    
                try (ResultSet generatedKeys = docStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastId = generatedKeys.getString(1);
                    }
                }
            }
    
            try (PreparedStatement lastDocStatement = connection.prepareStatement(lastDocSql)) {
                lastDocStatement.setString(1, lastId);
                ResultSet rs = lastDocStatement.executeQuery();
                while (rs.next()) {
                    lastDoc.setReferenceDocument(rs.getString("ref_document"));
                    lastDoc.setIdDocument(rs.getInt("id_document"));
                }
            }
    
            try (PreparedStatement docStateStatement = connection.prepareStatement(docStateSql)) {
                docStateStatement.setString(1, lastDoc.getReferenceDocument());
                docStateStatement.setInt(2, lastDoc.getIdDocument());
                docStateStatement.setInt(3, 1);
                docStateStatement.setString(4, userMatricule);
                docStateStatement.executeUpdate();
            }
    
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    
        return lastDoc;
    }


    /**
     * Add Document Validation
     */
    public Document addDocumentValidation(Connection connection, String titre, int type, String miseEnApplication, boolean confidentiel, String userMatricule) throws Exception {
        String insertDocSql = "INSERT INTO document(titre, id_type, date_creation, date_mise_application, confidentiel) VALUES (?, ?, CURRENT_DATE, ?, ?)";
        
        String lastDocSql = "SELECT ref_document, id_document FROM document WHERE ref_document = ?";
        String docStateSql = "INSERT INTO historique_etat(ref_document, id_document, id_etat, matricule_utilisateur, date_heure_etat) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        Document lastDoc = new Document();
        String lastId = null;
    
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement docStatement = connection.prepareStatement(insertDocSql, Statement.RETURN_GENERATED_KEYS)) {
                docStatement.setString(1, titre);
                docStatement.setInt(2, type);
    
                if (miseEnApplication == null || miseEnApplication.trim().isEmpty() || "".equals(miseEnApplication)) {
                    docStatement.setNull(3, java.sql.Types.DATE);
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date utilDate = format.parse(miseEnApplication);
                    Date sqlDate = new Date(utilDate.getTime());
                    docStatement.setDate(3, sqlDate);
                }
    
                docStatement.setBoolean(4, confidentiel);
                docStatement.executeUpdate();
    
                try (ResultSet generatedKeys = docStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastId = generatedKeys.getString(1);
                    }
                }
            }
    
            try (PreparedStatement lastDocStatement = connection.prepareStatement(lastDocSql)) {
                lastDocStatement.setString(1, lastId);
                ResultSet rs = lastDocStatement.executeQuery();
                while (rs.next()) {
                    lastDoc.setReferenceDocument(rs.getString("ref_document"));
                    lastDoc.setIdDocument(rs.getInt("id_document"));
                }
            }
    
            try (PreparedStatement docStateStatement = connection.prepareStatement(docStateSql)) {
                docStateStatement.setString(1, lastDoc.getReferenceDocument());
                docStateStatement.setInt(2, lastDoc.getIdDocument());
                docStateStatement.setInt(3, 2);
                docStateStatement.setString(4, userMatricule);
                docStateStatement.executeUpdate();
            }
    
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    
        return lastDoc;
    }


    /**
     * Add Document Fields
     */
    public void addDocumentFields(Connection connection, String refDocument, int idDocument, String fieldRef, String fieldValue) throws Exception {
        String sql = "INSERT INTO champ_document(ref_document, id_document, ref_champ, valeur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setString(3, fieldRef);
            statement.setString(4, fieldValue);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Add Document Process
     */
    public void addDocumentProcess(Connection connection, String refDocument, int idDocument, int processId, String processType) throws Exception {
        String sql = null;
        switch (processType) {
            case "processusGlobal":
                sql = "INSERT INTO processus_global_document(ref_document, id_document, id_processus_global) VALUES (?, ?, ?)";
                break;
            case "processusLie":
                sql = "INSERT INTO processus_lie_document(ref_document, id_document, id_processus_lie) VALUES (?, ?, ?)";
                break;
            default:
                break;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setInt(3, processId);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Add Document User Role
     */
    public void addDocumentUserRole(Connection connection, String refDocument, int idDocument, String userMatricule, int documentState, String userType) throws Exception {
        String sql = null;
        switch (userType) {
            case "redacteur":
                if (documentState != 1 && documentState != 3 && documentState != 5) {
                    sql = "INSERT INTO redacteur_document(ref_document, id_document, matricule_utilisateur, redaction_document_date) VALUES (?, ?, ?, CURRENT_DATE)";
                    break;
                } else {
                    sql = "INSERT INTO redacteur_document(ref_document, id_document, matricule_utilisateur) VALUES (?, ?, ?)";
                    break;
                }
            case "verificateur":
                sql = "INSERT INTO verificateur_document(ref_document, id_document, matricule_utilisateur) VALUES (?, ?, ?)";
                break;
            case "approbateur":
                sql = "INSERT INTO approbateur_document(ref_document, id_document, matricule_utilisateur) VALUES (?, ?, ?)";
                break;
            default:
                break;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setString(3, userMatricule);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Add Mail Diffusion
     */
    public void addEmailDiffusion(Connection connection, String refDocument, int idDocument, String userMatricule, String userEmail) throws Exception {
        String sql = "INSERT INTO diffusion_email(ref_document, id_document, matricule_utilisateur, email_utilisateur) VALUES (?, ?, ? , ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setString(3, userMatricule);
            statement.setString(4, userEmail);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Add Document Attached File
     */
    public void addDocumentAttachedFile(Connection connection, String refDocument, int idDocument, String fileName, String fileExtension, byte[] fileContent) throws Exception {
        String sql = "INSERT INTO fichier_document(ref_document, id_document, nom_fichier, extension_fichier, fichier) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setString(3, fileName);
            statement.setString(4, fileExtension);
            statement.setBytes(5, fileContent);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Fetch Documents Last State
     */


    /**
     * Fetch By User
     */
    // public List<Document> fetchDocsForGivenUser(Connection connection, String userMatricule, int docType) {
    //     String sql = "SELECT * FROM v_doc_state WHERE matricule_utilisateur = ? AND id_etat";
    //     List<Document> documentList = new ArrayList<Document>();

    //     return documentList;
    // }


    /**
     * Fetch By User Role
     */
    // public List<DocumentUserRole> fetchByUserRole(Connection connection, String refDocument, int idDocument, String role) throws Exception {
    //     String sql = null;
    //     List<DocumentUserRole> documentUserRoleList = new ArrayList<DocumentUserRole>();

    //     switch (role) {
    //         case "redacteur":
    //             sql = "select * from redacteur_document where ref_document = ? and id_document = ?";
    //             break;
    //         case "verificateur":
    //             sql = "select * from verificateur_document where ref_document = ? and id_document = ?";
    //             break;
    //         case "approbateur":
    //             sql = "select * from approbateur_document where ref_document = ? and id_document = ?";
    //             break;
    //         default:
    //             break;
    //     }

    //     try (PreparedStatement statement = connection.prepareStatement(sql)) {
    //         statement.setString(1, refDocument);
    //         statement.setInt(2, idDocument);

    //         ResultSet rs = statement.executeQuery();
    //         while (rs.next()) {
    //             documentUserRoleList.add(new DocumentUserRole(
    //                 rs.getString(1),
    //                 rs.getInt(2),
    //                 rs.getString(3),
    //                 rs.getDate(4)
    //             ));
    //         }
    //     } catch (Exception e) {
    //         throw e;
    //     }

    //     return documentUserRoleList;
    // }
}
