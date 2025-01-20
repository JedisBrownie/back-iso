package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.Date;
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
public class ViewEtatDocument {
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
     * Constructor
     */


    /**
     * Methods
     */
    /**
     * Get Documents State For User
     */
    public List<ViewEtatDocument> getDocumentsStateForUser(Connection connection, int state, String userMatricule) throws Exception {
        List<ViewEtatDocument> documentStateList = new ArrayList<ViewEtatDocument>();
        String sql = "select * from v_document_state where id_etat = ? and matricule_utilisateur = ?";
        
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, state);
            statement.setString(2, userMatricule);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                documentStateList.add(new ViewEtatDocument(
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
}
