package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoriqueEtat {
    String idHistorique;
    Document document;
    Etat etat;
    Utilisateur utilisateur;
    Timestamp dateHeureEtat;
    String motif;


    public void addHistoriqueEtat(String refDocument,int idDocument,int idEtat,int idUtilisateur,String motif,Connection connection) throws Exception{
        String sql = "INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif) VALUES (?,?,?,?,CURRENT_TIMESTAMP(),?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setInt(3,idEtat);
            statement.setInt(4, idUtilisateur);
            statement.setString(5, motif);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }finally{
            if(statement!=null){
                statement.close();
            }
        }
    }

    



    

    
}
