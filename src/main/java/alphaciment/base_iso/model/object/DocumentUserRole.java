package alphaciment.base_iso.model.object;


import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DocumentUserRole {
    /**
     * Fields
     */
    String refDocument;
    int idDocument;
    String matriculeUtilisateur;
    Date date;
}
