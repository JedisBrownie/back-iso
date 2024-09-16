package alphaciment.base_iso.model.object.iso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name="processus_lie",schema="base_iso")
public class ProcessusLie {
    @Id
    @Column(name="id_processus_lie")
    int idProcessusLie;

    @Column(name="nom")
    String nom;
    


    // public List<ProcessusLie> findProcessusLieByPg(Connection connection,int idProcessusGlobal) throws Exception{
    //     List<ProcessusLie> liste = new ArrayList<>();
    //     String sql = "SELECT * FROM processus_lie WHERE id_processus_global = ?";
        
    //     try(PreparedStatement statement = connection.prepareStatement(sql);){
    //         statement.setInt(1, idProcessusGlobal);
    //         ResultSet rs = statement.executeQuery();
    //         while(rs.next()){
    //             ProcessusLie processus = new ProcessusLie();
    //             processus.setIdProcessusLie(rs.getInt("id_processus_lie"));
    //             processus.setNom(rs.getString("nom"));
    //             liste.add(processus);
    //         }
    //         rs.close();
    //     }catch(Exception e){
    //         throw e;
    //     }
        
    //     return liste;
    // }

}
