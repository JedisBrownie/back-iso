package alphaciment.base_iso.model.object;

import java.sql.*;
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
public class ProcessusGlobal {
        
    int idProcessusGlobal;
    String nom;
    
    public List<ProcessusGlobal> getAllProcessusGlobal(Connection connection) throws Exception{
        List<ProcessusGlobal> liste = new ArrayList<ProcessusGlobal>();
        String sql ="SELECT * FROM processus_global";

        try (Statement statement = connection.createStatement() ; ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                ProcessusGlobal processus = new ProcessusGlobal(rs.getInt("id_processus_global"), rs.getString("nom"));
                liste.add(processus);
            }
        }catch(Exception e){
            throw e;
        }
        return liste;
    }






}
