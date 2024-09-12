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
public class ProcessusLie {
    int idProcessusLie;
    String nom;
    

    public List<ProcessusLie> findProcessusLieByPg(Connection connection,int idProcessusGlobal) throws Exception{
        List<ProcessusLie> liste = new ArrayList<>();
        String sql = "SELECT * FROM processus_lie WHERE id_processus_global = " + idProcessusGlobal;
        
        try(Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)){
            while(rs.next()){
                ProcessusLie processus = new ProcessusLie(rs.getInt("id_processus_lie"),rs.getString("nom"));
                liste.add(processus);
            }
        }catch(Exception e){
            throw e;
        }
        
        return liste;
    }

}
