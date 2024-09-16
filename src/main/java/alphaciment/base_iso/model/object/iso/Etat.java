package alphaciment.base_iso.model.object.iso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name="etat_document",schema="base_iso")
public class Etat {
    @Id
    @Column(name="id_etat")
    int idEtat;

    @Column(name="nom")
    String nom;

    
    
}
