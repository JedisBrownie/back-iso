package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewTypeDocument {
    int idTypeDocument;
    String nomTypeDocument;
    List<ViewDocument> listeDocument;

    public ViewTypeDocument(int idDocument,String nom){
        this.setIdTypeDocument(idDocument);
        this.setNomTypeDocument(nom);
    }


    public List<ViewTypeDocument> getViewTypeDocumentApplicable(int idProcessusLie,Connection connection) throws Exception{
        List<ViewTypeDocument> liste = new ArrayList<>();
        String sql = "SELECT vda.id_type,td.nom " + 
                        "FROM v_document_applicable vda " + 
                        "JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document " + 
                        "JOIN type_document td ON td.id_type = vda.id_type " +
                        "WHERE pld.id_processus_lie = ? " +
                        "GROUP BY vda.id_type,td.nom";

        PreparedStatement statement = null;
        ResultSet rs = null;
        try{
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idProcessusLie);

            rs = statement.executeQuery();

            while(rs.next()){
                int type = rs.getInt("id_type");
                String nomType = rs.getString("nom");
                ViewTypeDocument viewType = new ViewTypeDocument(type, nomType);
                liste.add(viewType);                
            }

            statement.close();
            rs.close();
        } catch (Exception e) {
            throw e;
        }finally{
            if(statement != null){
                statement.close();
            }
            if(rs != null){
                rs.close();
            }
        }

        // for(ViewTypeDocument vd : liste){
        //     vd.setListeDocument(new ViewDocument().getViewDocumentsApplicable(idProcessusLie, vd.getIdTypeDocument(), connection));
        // }

        // List<Document> 

        // ViewDocument vdc =  new ViewDocument(reference, idDoc, titre, dateApplication, nbRevision, status, confidentiel, modifiable);

        List<ViewDocument> listeDoc = new ViewDocument().getAllDocumentsApplicable(idProcessusLie, connection);

        liste.forEach(inListe -> {
            List<ViewDocument> listeDocuments = listeDoc.stream()
                    .filter(doc -> doc.getTypeDocument() == inListe.getIdTypeDocument())
                    .map(doc -> new ViewDocument(doc.getReferenceDocument(),doc.getIdDocument(),doc.getNom(),doc.getDateApplication(),doc.getNombreRevision(),doc.getStatus(),doc.confidentiel,doc.modification))
                    .collect(Collectors.toList());
            inListe.setListeDocument(listeDocuments);
        });

        return liste;
    }



}
