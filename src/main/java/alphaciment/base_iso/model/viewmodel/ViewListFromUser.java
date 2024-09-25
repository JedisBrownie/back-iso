package alphaciment.base_iso.model.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewListFromUser {
    int idUtilisateur;
    String nom;
    String prenom;
    List<ViewDocument> listeDocument;

    public ViewListFromUser(int idUtil,String name,String prenom){
        this.idUtilisateur = idUtil;
        this.nom = name;
        this.prenom = prenom;
    }

    public List<ViewListFromUser> getAllDocumentEnCours(Connection connection)throws Exception{
        List<ViewListFromUser> liste = new ArrayList<>();
        String sql = "SELECT matricule,nom,prenom FROM utilisateur WHERE matricule IN (SELECT owner FROM v_document_en_cours_owner) GROUP BY matricule,nom,prenom";
        
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql);){
            while(rs.next()){
                ViewListFromUser ve = new ViewListFromUser();
                ve.setIdUtilisateur(rs.getInt("matricule"));
                ve.setNom(rs.getString("nom"));
                ve.setPrenom(rs.getString("prenom"));
                
                liste.add(ve);
            }
            rs.close();
            statement.close();
        }catch(Exception e){
            throw e;
        }

        List<ViewDocument> listeDoc = new ViewDocument().getAllDocumentEnCours(connection);

        liste.forEach(inListe -> {
        List<ViewDocument> listeDocuments = listeDoc.stream()
                    .filter(doc -> doc.getOwner() == inListe.getIdUtilisateur())
                    .map(doc -> new ViewDocument(doc.getReferenceDocument(),doc.getIdDocument(),doc.getNom(),doc.getStatus(),doc.getDateCreation(),doc.getNombreRevision(),doc.getNomTypeDocument(),doc.confidentiel))
                    .collect(Collectors.toList());
            inListe.setListeDocument(listeDocuments);
        });


        return liste;
    }

    
}
