package alphaciment.base_iso.model.object.iso;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name="processus_global",schema="base_iso")
public class ProcessusGlobal {
    @Id
    @Column(name="id_processus_global")
    int idProcessusGlobal;

    @Column(name="nom")
    String nom;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="id_processus_global")
    List<ProcessusLie> processusLie;


    // public ProcessusGlobal findProcessusGlobalById(int idProcessusGlobal,Connection connection)throws Exception{
    //     ProcessusGlobal pg = new ProcessusGlobal();
    //     String sql ="SELECT * FROM processus_global WHERE id_processus_global = ?";

    //     try (PreparedStatement statement = connection.prepareStatement(sql);) {
    //         statement.setInt(1, idProcessusGlobal);
    //         ResultSet rs = statement.executeQuery();
    //         while (rs.next()) {
    //             pg.setIdProcessusGlobal(rs.getInt("id_processus_global"));  
    //             pg.setNom(rs.getString("nom")); 
    //         }
    //         rs.close();
    //     }catch(Exception e){
    //         throw e;
    //     }
        
    //     pg.setProcessusLie(new ProcessusLie().findProcessusLieByPg(connection,pg.getIdProcessusGlobal()));
    //     return pg;
    // }

    // public List<ProcessusGlobal> getAllProcessusGlobal(Connection connection) throws Exception{
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     String sql ="SELECT * FROM processus_global";

    //     try (Statement statement = connection.createStatement() ; ResultSet rs = statement.executeQuery(sql)) {
    //         while (rs.next()) {
    //             int id = rs.getInt("id_processus_global");
    //             String name = rs.getString("nom");
    //             ProcessusGlobal processus = new ProcessusGlobal(id,name);
    //             liste.add(processus);
    //         }
    //     }catch(Exception e){
    //         throw e;
    //     }

    //     for(ProcessusGlobal pg : liste){
    //         pg.setProcessusLie(new ProcessusLie().findProcessusLieByPg(connection,pg.getIdProcessusGlobal()));
    //     }

    //     return liste;
    // }





    // public List<ProcessusGlobal> findProcessusOfDocument(String reference,int idDocument,Connection connection) throws Exception{
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     List<Integer> listeId = new ArrayList<>();
    //     String sql = "SELECT id_processus_global FROM processus_global_document WHERE ref_document = ? AND id_document = ? ";
        
    //     PreparedStatement statement = null;
    //     ResultSet rs = null;        
    //     try{
    //        statement = connection.prepareStatement(sql);
    //        statement.setString(1, reference);
    //        statement.setInt(2, idDocument);
    //        rs = statement.executeQuery();
           
    //         while(rs.next()){
    //             int idProcessusGlobal = rs.getInt("id_processus_global");
    //             listeId.add(idProcessusGlobal);
    //         }
    //     }catch(Exception e){
    //         throw e;
    //     }finally{
    //         if(statement != null){
    //             statement.close();
    //         }
    //         if(rs != null){
    //             rs.close();
    //         }
    //     }

    //     for(int id : listeId){
    //         liste.add(new ProcessusGlobal().findProcessusGlobalById(id, connection));
    //     }

    //     return liste;
    // }

    

    




}
