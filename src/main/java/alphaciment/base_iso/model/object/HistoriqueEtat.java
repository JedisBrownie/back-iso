package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void saveHistoriqueEtatSansMotif(String refDocument,int idDocument,int idEtat,int idUtilisateur,Connection connection) throws Exception{
        String sql = "INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";
        
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setInt(3,idEtat);
            statement.setInt(4, idUtilisateur);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public void saveHistoriqueEtat(String refDocument,int idDocument,int idEtat,int idUtilisateur,String motif,Connection connection) throws Exception{
        String sql = "INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif) VALUES (?,?,?,?,CURRENT_TIMESTAMP,?)";
        
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setInt(3,idEtat);
            statement.setInt(4, idUtilisateur);
            statement.setString(5, motif);

            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public int findUtilisateurValiderRedaction(String ref_document,int id_document,Connection connection) throws Exception{
        int idUtilisateur = 0;

        String sql = "SELECT id_utilisateur FROM historique_etat WHERE id_etat = 2 AND ref_document = ? AND id_document = ? ORDER BY date_heure_etat DESC LIMIT 1";
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, ref_document);
            statement.setInt(2, id_document);
            rs = statement.executeQuery();
            while(rs.next()){
                idUtilisateur = rs.getInt("id_utilisateur");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            if(statement != null){
                statement.close();
            }
            if(rs != null){
                rs.close();
            }
        }

        return idUtilisateur;
    }

    


    

    
}
