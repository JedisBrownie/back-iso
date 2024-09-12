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
public class ProcessusGlobal {
        
    int idProcessusGlobal;
    String nom;
    List<ProcessusLie> processusLie;

    public ProcessusGlobal(int id,String nom){
        this.idProcessusGlobal = id;
        this.nom = nom;
    }
    
    // public List<ProcessusGlobal> getAllProcessusGlobal(Connection connection) throws Exception{
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     String sql ="SELECT * FROM processus_global";

    //     try (Statement statement = connection.createStatement() ; ResultSet rs = statement.executeQuery(sql)) {
    //         while (rs.next()) {
    //             int id = rs.getInt("id_processus_global");
    //             String name = rs.getString("nom");
    //             List<ProcessusLie> list = new ProcessusLie().findProcessusLieByPg(connection,id);

    //             ProcessusGlobal processus = new ProcessusGlobal(id,name,list);
    //             liste.add(processus);
    //         }
    //     }catch(Exception e){
    //         throw e;
    //     }
    //     return liste;
    // }

    public List<ProcessusGlobal> getAllProcessusGlobal(Connection connection) throws Exception{
        List<ProcessusGlobal> liste = new ArrayList<>();
        String sql ="SELECT * FROM processus_global";

        try (Statement statement = connection.createStatement() ; ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_processus_global");
                String name = rs.getString("nom");
                ProcessusGlobal processus = new ProcessusGlobal(id,name);
                liste.add(processus);
            }
        }catch(Exception e){
            throw e;
        }

        for(ProcessusGlobal pg : liste){
            pg.setProcessusLie(new ProcessusLie().findProcessusLieByPg(connection,pg.getIdProcessusGlobal()));
        }

        return liste;
    }

    

    




}
