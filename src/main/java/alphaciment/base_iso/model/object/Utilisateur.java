package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static String nomTable = "v_utilisateur";

    public List<Utilisateur> getAllUtilisateur(Connection connection) throws Exception{
        List<Utilisateur> listeUtil = new ArrayList<>();
        String sql = "SELECT * FROM " + nomTable;
        
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()){
                Utilisateur utilisateur = new Utilisateur();
                
                utilisateur.setMatricule(rs.getInt("matricule")); 
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setFonctionPoste(rs.getString("fonction_poste"));
                utilisateur.setService(rs.getString("service"));
                utilisateur.setLieuTravail(rs.getString("lieu_travail"));
                utilisateur.setEmail(rs.getString("email"));
                
                listeUtil.add(utilisateur);
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
        String sql = "SELECT * FROM " + nomTable + " WHERE matricule = ?";
        PreparedStatement statement = null;
        ResultSet rs = null;
        try{
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idMatricule);
            rs = statement.executeQuery();

            while(rs.next()){
                utilisateur.setMatricule(rs.getInt("matricule")); 
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setFonctionPoste(rs.getString("fonction_poste"));
                utilisateur.setService(rs.getString("service"));
                utilisateur.setLieuTravail(rs.getString("lieu_travail"));
                utilisateur.setEmail(rs.getString("email"));
            }
        } catch (Exception e) {
            throw e;
        }
        finally{
            if(rs != null){
                rs.close();
            }
            if(statement != null){
                statement.close();
            }
        }

        return utilisateur;
    }

    // public List<Utilisateur> findRedacteurDocument(Connection connection,String ref_document,int idDocument) throws Exception{
    //     List<Utilisateur> liste = new ArrayList<>();

    //     return liste;
    // }

    // public List<Utilisateur> findLecteurAutoriseDocument(Connection connection,String ref_document,int idDocument) throws Exception{
    //     List<Utilisateur> liste = new ArrayList<>();

    //     return liste;
    // }


    public void saveRedacteurDocument(String refDocument,int idDocument,String[] redacteur,int idUtilisateur,Connection connection) throws Exception{
        String sql = "INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES (?,?,?)";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            
            if(redacteur != null && redacteur.length > 0){  
                List<String> maListe = new ArrayList<>(Arrays.asList(redacteur));
                maListe.add(String.valueOf(idUtilisateur));
                String[] listeRedacteur = maListe.toArray(new String[0]);

                statement.setString(1, refDocument);
                statement.setInt(2, idDocument);
    
                for(String idUtil : listeRedacteur){
                    statement.setInt(3, Integer.parseInt(idUtil));
                    statement.executeUpdate();
                    System.out.println("Rédacteur : " + idUtil);
                }
            }else if (redacteur == null) {
                statement.setString(1, refDocument);
                statement.setInt(2, idDocument);
                statement.setInt(3, idUtilisateur);
                statement.executeUpdate();
                System.out.println("Rédacteur : " + idUtilisateur);
            }

            statement.close();
            
        }catch(Exception e){
            throw e;
        }
    }

    public void saveLecteurDocument(String refDocument,int idDocument,String[] lecteur,Connection connection) throws Exception{
        String sql = "INSERT INTO lecteur_document(ref_document,id_document,id_utilisateur) VALUES (?,?,?)";

        if(lecteur != null){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, refDocument);
                statement.setInt(2, idDocument);
    
                for(String idUtilisateur : lecteur){
                    statement.setInt(3, Integer.parseInt(idUtilisateur));
                    statement.executeUpdate();
                    System.out.println("Lecteur enregistré : " + idUtilisateur);
                }
                statement.close();
            }catch(Exception e){
                throw e;
            }
        }
    }


    public boolean isRedacteur(String refDocument,int idDocument,int idUtilisateur,Connection connection)throws Exception{
        boolean val = false;
        String sql = "SELECT * FROM redacteur_document WHERE ref_document = ? AND id_document = ? AND id_utilisateur = ?";
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {

            statement = connection.prepareStatement(sql);
            statement.setString(1, refDocument);
            statement.setInt(2, idDocument);
            statement.setInt(3, idUtilisateur);

            rs = statement.executeQuery();
            int nbRow = 0;

            while(rs.next()){
                nbRow++;
            }

            if(nbRow == 1){
                val = true;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if(rs != null){
                rs.close();
            }
            if(statement != null){
                statement.close();
            }
        }

        return val;
    }


    



}
