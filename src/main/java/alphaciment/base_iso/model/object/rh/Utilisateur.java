package alphaciment.base_iso.model.object.rh;

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
@Table(name="utilisateur",schema="base_rh")
public class Utilisateur {
    @Id
    @Column(name="matricule")
    int matricule;

    @Column(name="nom")
    String nom;

    @Column(name="prenom")
    String prenom;

    @Column(name="fonction_poste")
    String fonctionPoste;

    @Column(name="service")
    String service;

    @Column(name="lieu_travail")
    String lieuTravail;

    @Column(name="email")
    String email;

}
