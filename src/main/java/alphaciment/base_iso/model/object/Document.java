package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    public void addDocument(Connection connection, String titre, int type, int idProcessusLie, boolean confidentiel) throws Exception {

        String sql = "INSERT INTO DOCUMENT(titre, id_type, confidentiel, date_creation, id_entete) VALUES (?, ?, ?, CURRENT_DATE, ?)";

        try(PreparedStatement statement = connection.prepareStatement(sql) ){
            statement.setString(1,titre);
            statement.setInt(2, type);
            statement.setBoolean(3, confidentiel);
            statement.setInt(4, idProcessusLie);

            statement.executeUpdate();
            statement.close();
        } catch(Exception e) {
            throw e;
        } finally {
            connection.close();
        }
    }

    public Document addDocument(String titre, int idTypeDocument, int idProcessusLie, boolean confidentiel, int idApprobateur, int idVerificateur, Connection connection) throws Exception {
        String sql = "INSERT INTO DOCUMENT(titre, id_type, id_entete, date_creation, confidentiel, id_approbateur, id_validateur) " +
                    "VALUES(?, ?, ?, CURRENT_DATE, ?, ?, ?) RETURNING ref_document,id_document";
        
        PreparedStatement statement = null;
        ResultSet rs = null;
        Document document = new Document();

        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, titre);
            statement.setInt(2, idTypeDocument);
            statement.setInt(3, idProcessusLie);
            statement.setBoolean(4, confidentiel);
            statement.setInt(5, idApprobateur);
            statement.setInt(6, idVerificateur);

            rs = statement.executeQuery();
            
            while(rs.next()) {
                String ref = rs.getString("ref_document");
                int idDoc = rs.getInt("id_document");
                document.setReferenceDocument(ref);
                document.setIdDocument(idDoc);
            }
        } catch(Exception e) {
            throw e;   
        } finally {
            if(statement != null) {
                statement.close();
            }
            if(rs != null) {
                rs.close();
            }
        }
        return document;
    }


    public Document addEnregistrement(String titre, int idTypeDocument, int idProcessusLie, boolean confidentiel, Connection connection) throws Exception {
        String sql = "INSERT INTO DOCUMENT(titre, id_type, id_entete, date_creation, confidentiel) " +
                    "VALUES(?, ?, ?, CURRENT_DATE, ?) RETURNING ref_document,id_document";
        
        PreparedStatement statement = null;
        ResultSet rs = null;
        Document document = new Document();
        try{
            statement = connection.prepareStatement(sql);

            statement.setString(1, titre);
            statement.setInt(2, idTypeDocument);
            statement.setInt(3, idProcessusLie);
            statement.setBoolean(4, confidentiel);
            
            rs = statement.executeQuery();
            
            while(rs.next()){
                String ref = rs.getString("ref_document");
                int idDoc = rs.getInt("id_document");
                document.setReferenceDocument(ref);
                document.setIdDocument(idDoc);
            }

        }catch(Exception e){
            throw e;   
        }finally{
            if(statement != null){
                statement.close();
            }
            if(rs != null){
                rs.close();
            }
        }
        return document;
    }

    


}
