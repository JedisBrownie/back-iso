package alphaciment.base_iso.model.object.iso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import alphaciment.base_iso.model.object.rh.Utilisateur;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
@IdClass(DocumentKey.class)
public class Document {
    @Id
    String referenceDocument;

    @Id
    int idDocument;
    
    String titreDocument;
    TypeDocument type;
    int confidentiel;

    List<ProcessusGlobal> processusGlobal;
    List<ProcessusLie> processusLie;
    
    Utilisateur approbateur;
    Utilisateur verificateur;

    List<Utilisateur> redacteur;
    List<Utilisateur> lecteur;
    List<Utilisateur> diffusionEmail;



    public void addDocument(String titre,int idProcessusGlobal,int type,int confidentiel,Connection connection) throws Exception{

        String sql = "INSERT INTO DOCUMENT(titre,id_type,confidentiel,date_creation,id_entete) VALUES (?,?,?,CURDATE(),?)";

        try(PreparedStatement statement = connection.prepareStatement(sql) ){
            statement.setString(1,titre);
            statement.setInt(2, type);
            statement.setInt(3, confidentiel);
            statement.setInt(4,idProcessusGlobal);

            statement.executeUpdate();
            statement.close();

        }catch(Exception e){
            throw e;
        } 
    }


}
