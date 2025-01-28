package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewMyDocument {
    /**
     * Fields
     */
    String refDocument;
    String titre;
    int idType;
    String nom;
    String matriculeUtilisateur;
    int idEtat;
    String status;
    Timestamp dateHeureEtat;


     
    /**
     * Methods
     */
    /**
     * Get Documents For User
     */
    public List<ViewMyDocument> getDocumentsForUser(Connection connection, int state, String userMatricule) throws Exception {
        List<ViewMyDocument> documentStateList = new ArrayList<>();
        String sql = "select v_document_state.*, redacteur_document.matricule_utilisateur as redacteur from v_document_state join redacteur_document on v_document_state.ref_document = redacteur_document.ref_document where id_etat = ? and redacteur_document.matricule_utilisateur = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, state);
            statement.setString(2, userMatricule);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                documentStateList.add(new ViewMyDocument(
                    rs.getString(1), 
                    rs.getString(2), 
                    rs.getInt(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getString(7),
                    rs.getTimestamp(8)
                ));
            }
        } catch (Exception e) {
            throw e;
        }

        return documentStateList;
    }


    /**
     * Get Document Where Document Ref
     */
    public ViewMyDocument getDocumentWhereDocumentRef(Connection connection, String refDocument) throws Exception {
        ViewMyDocument viewMyDocument = new ViewMyDocument();
        String sql = "select * from v_document_state where ref_document = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                viewMyDocument.setRefDocument(rs.getString("ref_document"));
                viewMyDocument.setTitre(rs.getString("titre"));
                viewMyDocument.setIdType(rs.getInt("id_type"));
                viewMyDocument.setNom(rs.getString("nom"));
                viewMyDocument.setIdEtat(rs.getInt("id_etat"));
                viewMyDocument.setStatus(rs.getString("status"));
                viewMyDocument.setDateHeureEtat(rs.getTimestamp("date_heure_etat"));
            }
        } catch (Exception e) {
            throw e;
        }

        return viewMyDocument;
    }


    /**
     * Get Documents Where User Is Checker
     */
    public List<Map<String, Object>> getDocumentsWhereUserIsChecker(Connection connection, String userMatricule) throws Exception {
        List<Map<String, Object>> documentList = new ArrayList<>();
        String sql = "select redacteur, STRING_AGG(ref_document, ', ') as documents from (select ref_document, UNNEST(STRING_TO_ARRAY(redacteurs, ', ')) as redacteur from v_document_concerned_users where id_etat = 2 and ? = ANY(STRING_TO_ARRAY(verificateurs, ', '))) as expanded group by redacteur order by redacteur";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userMatricule);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String redacteur = rs.getString("redacteur");
                    String documents = rs.getString("documents");

                    Map<String, Object> documentToCheck = new HashMap<>();
                    documentToCheck.put("matricule", redacteur);
                    documentToCheck.put("documents", documents != null ? documents.split(", ") : new String[]{});

                    documentList.add(documentToCheck);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return documentList;
    }


    /**
     * Get Documents Where User Is Approver
     */
    public List<Map<String, Object>> getDocumentsWhereUserIsApprover(Connection connection, String userMatricule) throws Exception {
        List<Map<String, Object>> documentList = new ArrayList<>();
        String sql = "select redacteur, STRING_AGG(ref_document, ', ') as documents from (select ref_document, UNNEST(STRING_TO_ARRAY(redacteurs, ', ')) as redacteur from v_document_concerned_users where id_etat = 4 and ? = ANY(STRING_TO_ARRAY(approbateurs, ', '))) as expanded group by redacteur order by redacteur";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userMatricule);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String redacteur = rs.getString("redacteur");
                    String documents = rs.getString("documents");

                    Map<String, Object> documentToCheck = new HashMap<>();
                    documentToCheck.put("matricule", redacteur);
                    documentToCheck.put("documents", documents != null ? documents.split(", ") : new String[]{});

                    documentList.add(documentToCheck);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return documentList;
    }


    /**
     * Get Documents To Be Handled By User
     */
    // public List<ViewMyDocument> getDocumentsToBeHandledByUser(Connection connection, int state, String userMatricule) throws Exception {
    //     List<ViewMyDocument> documentStateList = new ArrayList<>();
    //     String sql = "select v_document_state.*, matricule_utilisateur as verificateur from v_document_state join verificateur_document on v_document_state.ref_document = verificateur_document.ref_document where id_etat = ? and matricule_utilisateur = ?";

    //     try(PreparedStatement statement = connection.prepareStatement(sql)) {
    //         statement.setInt(1, state);
    //         statement.setString(2, userMatricule);

    //         ResultSet rs = statement.executeQuery();
    //         while (rs.next()) {
    //             documentStateList.add(new ViewMyDocument(
    //                 rs.getString(1), 
    //                 rs.getString(2), 
    //                 rs.getInt(3),
    //                 rs.getString(4),
    //                 rs.getInt(5),
    //                 rs.getString(6),
    //                 rs.getTimestamp(7)
    //             ));
    //         }
    //     } catch (Exception e) {
    //         throw e;
    //     }
        
    //     return  documentStateList;
    // }
}
