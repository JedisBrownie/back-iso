package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    int idEtat;
    String status;
    Timestamp dateHeureEtat;


     

    /**
     * Methods
     */
    /**
     * Get Documents For User
     */
    public List<ViewMyDocument> getDocumentsForUser(Connection connection, int part, int state, String userMatricule) throws Exception {
        List<ViewMyDocument> documentStateList = new ArrayList<>();
        String sql = "select v_document_state.*, matricule_utilisateur as redacteur from v_document_state join redacteur_document on v_document_state.ref_document = redacteur_document.ref_document where id_etat = ? and matricule_utilisateur = ?";
        
        switch (part) {
            case 1:
                sql = "select v_document_state.*, matricule_utilisateur as redacteur from v_document_state join redacteur_document on v_document_state.ref_document = redacteur_document.ref_document where id_etat = ? and matricule_utilisateur = ?";
                break;
            case 2:
                sql = "select v_document_state.*, matricule_utilisateur as verificateur from v_document_state join verificateur_document on v_document_state.ref_document = verificateur_document.ref_document where id_etat = ? and matricule_utilisateur = ?";
                break;
            case 3:
                sql = "select v_document_state.*, matricule_utilisateur as approbateur from v_document_state join approbateur_document on v_document_state.ref_document = approbateur_document.ref_document where id_etat = ? and matricule_utilisateur = ?";
                break;
            default:
                throw new AssertionError();
        }

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
                    rs.getInt(5),
                    rs.getString(6),
                    rs.getTimestamp(7)
                ));
            }
        } catch (Exception e) {
            throw e;
        }

        return documentStateList;
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
