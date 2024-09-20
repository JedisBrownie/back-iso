package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewListFromType {
    int idProcessusGlobal;
    String nomProcessusGlobal;
    int idProcessusLie;
    String nomProcessusLie;
    List<ViewTypeDocument> listeType;

    
    public ViewListFromType(int idProcessusGlobal, String nomProcessusGlobal, int idProcessusLie, String nomProcessusLie) {
        this.idProcessusGlobal = idProcessusGlobal;
        this.nomProcessusGlobal = nomProcessusGlobal;
        this.idProcessusLie = idProcessusLie;
        this.nomProcessusLie = nomProcessusLie;
    }
    
    public ViewListFromType getViewListApplicable(Connection connection,int idProcessusLie) throws Exception{
        ViewListFromType type = new ViewListFromType();
        PreparedStatement statement = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM v_processus vp WHERE vp.id_processus_lie = ?";
        try{
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idProcessusLie);
            rs = statement.executeQuery();
            
            while(rs.next()){
                int idPg = rs.getInt("id_processus_global");
                String nomPg = rs.getString("nom_processus_global");
                int idPl = rs.getInt("id_processus_lie");
                String nomPl = rs.getString("nom_processus_lie");
                
                type.setIdProcessusGlobal(idPg);
                type.setNomProcessusGlobal(nomPg);
                type.setIdProcessusLie(idPl);
                type.setNomProcessusLie(nomPl);
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

        type.setListeType(new ViewTypeDocument().getViewTypeDocumentApplicable(idProcessusLie, connection));

        return type;
    }
 
}
