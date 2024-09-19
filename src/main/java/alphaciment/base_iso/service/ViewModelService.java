package alphaciment.base_iso.service;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.viewmodel.ViewDocument;
import alphaciment.base_iso.model.viewmodel.ViewListFromType;
import alphaciment.base_iso.model.viewmodel.ViewTypeDocument;

@Service
public class ViewModelService {
    
    public ViewListFromType getAllDocumentApplicable(int idProcessusLie) throws Exception{
        Connection connection = IsoDataSource.getConnection();
        ViewListFromType viewModel = new  ViewListFromType();
        ViewTypeDocument viewTypeDocument = new ViewTypeDocument();
        ViewDocument vdc = new ViewDocument();

        try {
            viewModel = new ViewListFromType().getViewListApplicable(connection, idProcessusLie);
            // viewModel.setListeType(viewTypeDocument.getViewTypeDocumentApplicable(idProcessusLie, connection));

            // for(ViewTypeDocument viewTypeDocument1 : viewModel.getListeType()){
            //     viewTypeDocument1.setListeDocument(vdc.getViewDocumentsApplicable(idProcessusLie, viewTypeDocument1.getIdTypeDocument(), connection));        
            // }
        } catch (Exception e) {
            throw e;
        }finally{
            connection.close();
        }
        return viewModel;
    }


}
