package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
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



    public void addDocument(String titre,int idProcessusGlobal,int type,boolean confidentiel,Connection connection) throws Exception{

        String sql = "INSERT INTO DOCUMENT(titre,id_type,confidentiel,date_creation,id_entete) VALUES (?,?,?,CURRENT_DATE,?)";

        try(PreparedStatement statement = connection.prepareStatement(sql) ){
            statement.setString(1,titre);
            statement.setInt(2, type);
            statement.setBoolean(3, confidentiel);
            statement.setInt(4,idProcessusGlobal);

            statement.executeUpdate();
            statement.close();

        }catch(Exception e){
            throw e;
        } 
    }

    


}
