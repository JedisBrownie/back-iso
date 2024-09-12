package alphaciment.base_iso.model.object;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoriqueEtat {
    String idHistorique;
    Document document;
    Etat etat;
    Utilisateur utilisateur;
    Date dateEtat;
    String motif;

    
}
