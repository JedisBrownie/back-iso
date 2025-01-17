package alphaciment.base_iso.model.viewmodel;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewEtatDocument {
    /**
     * Fields
     */
    String refDocument;
    int idDocument;
    String titre;
    int idType;
    Date dateCreation;
    Date dateMiseApplication;
    Date dateArchive;
    boolean confidentiel;
    Date dateModification;
    String matriculeUtilisateur;
    int id_etat;
    Timestamp dateHeureEtat;


    /**
     * Constructor
     */


    /**
     * Methods
     */
    /**
     * Get all Documents Last State
     */
}
