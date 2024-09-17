package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Utilisateur {
    int matricule;
    String nom;
    String prenom;
    String fonctionPoste;
    String service;
    String lieuTravail;
    String email;

    private String nomTable = "utilisateur";

    public List<Utilisateur> getAllUtilisateur(Connection connection) throws Exception{
        List<Utilisateur> listeUtil = new ArrayList<>();
        String sql = "SELECT * FROM " + nomTable;
        
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()){
                int matricule = rs.getInt("matricule");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String fonctionPoste = rs.getString("fonction_poste");
                String service = rs.getString("service");
                String lieuTravail = rs.getString("lieu_travail");
                String email = rs.getString("email");

                Utilisateur util = new Utilisateur(matricule, prenom, prenom, fonctionPoste, service, lieuTravail, email, nom);
                listeUtil.add(util);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
        return listeUtil;
    }

    public Utilisateur findUtilisateurByMatricule(Connection connection,int idMatricule) throws Exception{
        Utilisateur utilisateur = new Utilisateur();
        String sql = "SELECT * FROM " + nomTable + " WHERE matricule = '" + idMatricule + "'";
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()){
                utilisateur.setMatricule(rs.getInt("matricule")); 
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setFonctionPoste(rs.getString("fonction_poste"));
                utilisateur.setService(rs.getString("service"));
                utilisateur.setLieuTravail(rs.getString("lieu_travail"));
                utilisateur.setEmail(rs.getString("email"));
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
        return utilisateur;
    }


}
