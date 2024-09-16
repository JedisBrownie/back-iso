package alphaciment.base_iso.repository.iso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alphaciment.base_iso.model.object.iso.Etat;

@Repository
public interface EtatRepository extends JpaRepository<Etat,Integer>{
 
}
