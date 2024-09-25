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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessusGlobal {
    int idProcessusGlobal;
    String nom;
    List<ProcessusLie> processusLie;

    public ProcessusGlobal(int idProcessusGlobal,String nom){
        this.setIdProcessusGlobal(idProcessusGlobal);
        this.setNom(nom);
    }


    //############################################//
    //              Processus Global              //
    // ###########################################//
    
    public ProcessusGlobal findProcessusGlobalById(int idProcessusGlobal,Connection connection)throws Exception{
        ProcessusGlobal pg = new ProcessusGlobal();
        String sql ="SELECT * FROM processus_global WHERE id_processus_global = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setInt(1, idProcessusGlobal);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                pg.setIdProcessusGlobal(rs.getInt("id_processus_global"));  
                pg.setNom(rs.getString("nom")); 
            }
            rs.close();
        }catch(Exception e){
            throw e;
        }
        
        pg.setProcessusLie(new ProcessusLie().findProcessusLieByPg(connection,pg.getIdProcessusGlobal()));
        return pg;
    }

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




    //############################################//
    //                  Document                  //
    // ###########################################//

    public List<ProcessusGlobal> findProcessusOfDocument(String reference,int idDocument,Connection connection) throws Exception{
        List<ProcessusGlobal> liste = new ArrayList<>();
        String sql = "SELECT pg.id_processus_global,pg.nom FROM processus_global_document pgd JOIN processus_global pg ON pg.id_processus_global = pgd.id_processus_global WHERE pgd.ref_document = ? AND pgd.id_document = ? ";
        
        PreparedStatement statement = null;
        ResultSet rs = null;        
        try{
           statement = connection.prepareStatement(sql);
           statement.setString(1, reference);
           statement.setInt(2, idDocument);
           rs = statement.executeQuery();
           
            while(rs.next()){
                int id = rs.getInt("id_processus_global");
                String name = rs.getString("nom");
                liste.add(new ProcessusGlobal(id, name));
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
        return liste;
    }


    public void insertProcessusOfDocument(String reference,int id,String[] processusGlobal,Connection connection) throws Exception{
        String sql = "INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES(?,?,?)";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, reference);
            statement.setInt(2, id);
            for (String processusGlobal1 : processusGlobal) {
                int idPg = Integer.parseInt(processusGlobal1);
                statement.setInt(3, idPg);
                statement.executeUpdate();
            }

            statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    

    

    




}
