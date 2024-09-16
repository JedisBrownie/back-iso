package alphaciment.base_iso.repository.iso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alphaciment.base_iso.model.object.iso.ProcessusGlobal;

@Repository
public interface ProcessusGlobalRepository extends JpaRepository<ProcessusGlobal,Integer>{
    
}
