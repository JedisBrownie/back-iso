package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String sql = "SELECT * FROM processus_lie WHERE id_processus_global = ?";
        
        try(PreparedStatement statement = connection.prepareStatement(sql);){
            statement.setInt(1, idProcessusGlobal);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ProcessusLie processus = new ProcessusLie();
                processus.setIdProcessusLie(rs.getInt("id_processus_lie"));
                processus.setNom(rs.getString("nom"));
                liste.add(processus);
            }
            rs.close();
        }catch(Exception e){
            throw e;
        }
        
        return liste;
    }

}
