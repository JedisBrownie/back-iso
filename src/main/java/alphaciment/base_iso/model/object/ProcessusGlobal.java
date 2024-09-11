package alphaciment.base_iso.model.object;

import java.sql.Connection;
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
    
    public List<ProcessusGlobal> getAllProcessusGlobal(Connection connection){
        List<ProcessusGlobal> liste = new ArrayList<ProcessusGlobal>();

        String sql ="SELECT * FROM processus_global";

        



        return liste;
    }

}
