package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessusLie {
    int idProcessusLie;
    String nom;
    
    public ProcessusLie(int idProcessusLie){
        this.setIdProcessusLie(idProcessusLie);
    }

    public List<ProcessusLie> findProcessusLieByPg(Connection connection,int idProcessusGlobal) throws Exception{
        List<ProcessusLie> liste = new ArrayList<>();
        String sql = "SELECT id_processus_lie,nom FROM processus_lie WHERE id_processus_global = ? ORDER BY id_processus_lie";
        
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

    //############################################//
    //                  Document                  //
    // ###########################################//

    public List<ProcessusLie> findProcessusLieOfDocument(Connection connection,String reference,int idDocument) throws Exception{
        List<ProcessusLie> liste = new ArrayList<>();
        
        String sql = "SELECT pl.id_processus_lie,pl.nom FROM processus_lie_document pld JOIN processus_lie pl ON pl.id_processus_lie = pld.id_processus_lie WHERE ref_document = ? AND id_document = ?";

        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, reference);
            statement.setInt(2, idDocument);
            rs = statement.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id_processus_lie");
                String name = rs.getString("nom");
                ProcessusLie pl = new ProcessusLie(id, name);
                liste.add(pl);
            }

        } catch (Exception e) {
            throw e;
        }finally{
            if(statement != null){
                statement.close();
            }
            if(rs != null){
                rs.close();
            }
        }
        return liste;
    }

    public void insertProcessusLieOfDocument(String reference,int id,String[] processusLie,Connection connection) throws Exception{
        String sql = "INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES(?,?,?)";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, reference);
            statement.setInt(2, id);
            for (String processusLie1 : processusLie) {
                int idPl = Integer.parseInt(processusLie1);
                statement.setInt(3, idPl);
                statement.executeUpdate();
            }
            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    

}
