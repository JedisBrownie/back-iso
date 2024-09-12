package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TypeDocument {
    int idType;
    String nom;

    public TypeDocument findTypeDocumentById(Connection connection,int id) throws Exception{
        TypeDocument type = new TypeDocument();
        String sql = "SELECT * FROM type_document WHERE id_type = " + id ;

        try(Statement statement = connection.createStatement() ; ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()){
                int idTp = rs.getInt("id_type");
                String name = rs.getString("nom");

                type.setIdType(idTp);
                type.setNom(name);
            }
        } catch (Exception e) {
            throw e;
        }
        return type;
    }

    
}
