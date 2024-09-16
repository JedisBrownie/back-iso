package alphaciment.base_iso.model.object.iso;

import java.sql.Date;
import java.sql.Timestamp;

import alphaciment.base_iso.model.object.rh.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name="historique_etat",schema="base_iso")
public class HistoriqueEtat {
    @Id
    @Column(name="id_histo")
    String idHistorique;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ref_document", referencedColumnName = "referenceDocument"),
        @JoinColumn(name = "id_document", referencedColumnName = "idDocument")
    })
    Document document;

    @ManyToOne
    @JoinColumn(name="id_etat")
    Etat etat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur")
    Utilisateur utilisateur;

    @Column(name="date_heure_etat")
    Timestamp dateHeureEtat;

    @Column(name="motif",nullable=true)
    String motif;

    
}
