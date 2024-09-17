package alphaciment.base_iso.model.viewmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewListFromType {
    int idProcessusGlobal;
    String nomProcessusGlobal;
    int idProcessusLie;
    String nomProcessusLie;
    List<ViewTypeDocument> listeType;
    

}
