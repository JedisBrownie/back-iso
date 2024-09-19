package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import alphaciment.base_iso.model.object.ProcessusLie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewDocument {

    String referenceDocument;
    int idDocument;
    String nom;
    Date dateApplication;
    Date dateArchive;
    int nombreRevision;
    String status;
    Boolean confidentiel;
    Boolean modification;

    List<ProcessusLie> listeProcessusLie;


    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, Date dateArchive, int nombreRevision, String status, Boolean confidentiel, Boolean modification) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.dateApplication = dateApplication;
        this.dateArchive = dateArchive;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.modification = modification;
    }

    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, int nombreRevision, String status, Boolean confidentiel, Boolean modification) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.dateApplication = dateApplication;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.modification = modification;
    }



    public List<ViewDocument> getViewDocumentsApplicable(int idProcessusLie,int typeDocument,Connection connection) throws Exception{
        List<ViewDocument> liste = new ArrayList<>();
        String sql = "SELECT vda.ref_document,vda.id_document,vda.titre,vda.etat,vda.date_mise_application,vda.confidentiel,vda.nombre_revision,vda.modifiable,vda.status " +
                    "FROM v_document_applicable vda " + 
                    "JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document " +
                    "WHERE pld.id_processus_lie = ? AND vda.id_type = ?";
                    
        PreparedStatement statement = null;
        ResultSet rs = null;

        try{
            statement = connection.prepareStatement(sql);
            statement.setInt(1,idProcessusLie);
            statement.setInt(2, typeDocument);

            rs = statement.executeQuery();

            while (rs.next()) {
                String reference = rs.getString("ref_document");
                int idDoc = rs.getInt("id_document");
                String titre = rs.getString("titre");
                Date dateApplication = rs.getDate("date_mise_application");
                Boolean confidentiel = rs.getBoolean("confidentiel");
                int nbRevision = rs.getInt("nombre_revision");
                Boolean modifiable = rs.getBoolean("modifiable");
                String status = rs.getString("status");

                ViewDocument vdc =  new ViewDocument(reference, idDoc, titre, dateApplication, nbRevision, status, confidentiel, modifiable);
                liste.add(vdc);
            }
        }catch(Exception e){
            throw e;
        }finally{
            if(statement != null){
                statement.close();
            }

            if(statement != null){
                statement.close();
            }
        }
        
        return liste;
    }

}
