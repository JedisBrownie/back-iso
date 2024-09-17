package alphaciment.base_iso.model.viewmodel;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewDocument {

    String referenceDocument;
    int idDocument;
    String nom;
    Date dateApplication;
    int nombreRevision;
    String status;
    Boolean confidentiel;
    Boolean modification;

    

}
