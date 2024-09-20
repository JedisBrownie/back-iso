package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import alphaciment.base_iso.model.object.ProcessusLie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewDocument {

    String referenceDocument;
    int idDocument;
    String nom;
    Date dateCreation;
    Date dateApplication;
    Date dateArchive;
    int nombreRevision;
    String status;
    Boolean confidentiel;
    Boolean modification;

    int idProcessusLie;
    int typeDocument;
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

    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, int nombreRevision, String status, Boolean confidentiel, Boolean modification , int idProcessusLie ,int typeDocument) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.dateApplication = dateApplication;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.modification = modification;
        this.idProcessusLie = idProcessusLie;
        this.typeDocument = typeDocument;
    }



    // public List<ViewDocument> getViewDocumentsApplicable(int idProcessusLie,int typeDocument,Connection connection) throws Exception{
    //     List<ViewDocument> liste = new ArrayList<>();
    //     String sql = "SELECT vda.ref_document,vda.id_document,vda.titre,vda.etat,vda.date_mise_application,vda.confidentiel,vda.nombre_revision,vda.modifiable,vda.status " +
    //                 "FROM v_document_applicable vda " + 
    //                 "JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document " +
    //                 "WHERE pld.id_processus_lie = ? AND vda.id_type = ?";
                    
    //     PreparedStatement statement = null;
    //     ResultSet rs = null;

    //     try{
    //         statement = connection.prepareStatement(sql);
    //         statement.setInt(1,idProcessusLie);
    //         statement.setInt(2, typeDocument);

    //         rs = statement.executeQuery();

    //         while (rs.next()) {
    //             String reference = rs.getString("ref_document");
    //             int idDoc = rs.getInt("id_document");
    //             String titre = rs.getString("titre");
    //             Date dateApplication = rs.getDate("date_mise_application");
    //             Boolean confidentiel = rs.getBoolean("confidentiel");
    //             int nbRevision = rs.getInt("nombre_revision");
    //             Boolean modifiable = rs.getBoolean("modifiable");
    //             String status = rs.getString("status");

    //             ViewDocument vdc =  new ViewDocument(reference, idDoc, titre, dateApplication, nbRevision, status, confidentiel, modifiable);
    //             liste.add(vdc);
    //         }
    //     }catch(Exception e){
    //         throw e;
    //     }finally{
    //         if(statement != null){
    //             statement.close();
    //         }

    //         if(rs != null){
    //             statement.close();
    //         }
    //     }
        
    //     return liste;
    // }

    public List<ViewDocument> getAllDocumentsApplicable(int idProcessusLie,Connection connection) throws Exception{
        List<ViewDocument> liste = new ArrayList<>();
        String sql = "SELECT vda.ref_document,vda.id_document,vda.titre,vda.etat,vda.date_mise_application,vda.confidentiel,vda.nombre_revision,vda.modifiable,vda.status,pld.id_processus_lie,vda.id_type " +
                    "FROM v_document_applicable vda " +
                    "JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document " + 
                    "WHERE pld.id_processus_lie = ?";

        PreparedStatement statement = null;
        ResultSet rs = null;

        try{
            statement = connection.prepareStatement(sql);
            statement.setInt(1,idProcessusLie);

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

                int idPl = rs.getInt("id_processus_lie");
                int typeDocument = rs.getInt("id_type");

                ViewDocument vdc =  new ViewDocument(reference, idDoc, titre, dateApplication, nbRevision, status, confidentiel, modifiable,idPl,typeDocument);

                liste.add(vdc);
            }
        }catch(Exception e){
            throw e;
        }finally{
            if(statement != null){
                statement.close();
            }

            if(rs != null){
                statement.close();
            }
        }
        
        return liste;
    }


    public List<ViewDocument> getViewDocumentsOfOwner(Connection connection,int idUtilisateur)throws Exception{
        List<ViewDocument> liste = new ArrayList<>();
        String sql = "SELECT * FROM v_document_en_cours_owner WHERE owner = ?";
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idUtilisateur);
            rs = statement.executeQuery();

            while(rs.next()){
                String referenceDocument = rs.getString("ref_document");
                int idDocument = rs.getInt("id_document");
                String statut = rs.getString("status");
                String titre = rs.getString("titre");
                Date date = rs.getDate("date_creation");
                int nbRevision = rs.getInt("nombre_revision");
                
            }
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

        return liste;
    }





}
