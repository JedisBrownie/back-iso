package alphaciment.base_iso.model.viewmodel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    boolean confidentiel;
    boolean modification;

    int typeDocument;
    String nomTypeDocument;
    int owner;
    List<ProcessusLie> listeProcessusLie;


    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, Date dateArchive, int nombreRevision, String status, boolean confidentiel, boolean modification) {
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

    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, int nombreRevision, String status, boolean confidentiel, boolean modification) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.dateApplication = dateApplication;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.modification = modification;
    }

    public ViewDocument(String referenceDocument, int idDocument, String nom, Date dateApplication, int nombreRevision, String status, boolean confidentiel, boolean modification , int idProcessusLie ,int typeDocument) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.dateApplication = dateApplication;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.modification = modification;
        this.typeDocument = typeDocument;
    }

    public ViewDocument(String referenceDocument, int idDocument,String nom,String status,Date dateCreation,int nombreRevision,String nomTypeDocument,boolean confidentiel) {
        this.referenceDocument = referenceDocument;
        this.idDocument = idDocument;
        this.nom = nom;
        this.nombreRevision = nombreRevision;
        this.status = status;
        this.confidentiel = confidentiel;
        this.dateCreation = dateCreation;
        this.nombreRevision = nombreRevision;
        this.nomTypeDocument = nomTypeDocument;
        this.confidentiel = confidentiel;
    }



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
                Date dateApp = rs.getDate("date_mise_application");
                Boolean confid = rs.getBoolean("confidentiel");
                int nbRevision = rs.getInt("nombre_revision");
                Boolean modifiable = rs.getBoolean("modifiable");
                String stut = rs.getString("status");

                int idPl = rs.getInt("id_processus_lie");
                int typeDoc = rs.getInt("id_type");

                ViewDocument vdc =  new ViewDocument(reference, idDoc, titre, dateApp, nbRevision, stut, confid, modifiable,idPl,typeDoc);

                liste.add(vdc);
            }
            
        }catch(Exception e){
            throw e;
        }finally{
            if(rs != null){
                rs.close();
            }

            if(statement != null){
                statement.close();
            }
        }
        
        return liste;
    }


    public List<String[]> getViewProcessusDocumentEnCours(Connection connection) throws Exception{
        List<String[]> liste = new ArrayList<>();
        String sql =  "SELECT pld.ref_document,pld.id_document,pld.id_processus_lie " +
                      "FROM v_document_en_cours_owner vde " + 
                      "JOIN processus_lie_document pld ON pld.ref_document = vde.ref_document AND pld.id_document = vde.id_document " +
                      "GROUP BY pld.ref_document,pld.id_document,pld.id_processus_lie";

        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()){
                String[] processLie = new String[3];
                
                processLie[0] = rs.getString("ref_document");
                processLie[1] = String.valueOf(rs.getInt("id_document"));
                processLie[2] = String.valueOf(rs.getInt("id_processus_lie"));
                
                liste.add(processLie);
            }

        } catch (Exception e) {
            throw e;
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

                String refDoc = rs.getString("ref_document");
                int idDoc = rs.getInt("id_document");
                String statut = rs.getString("status");
                String titre = rs.getString("titre");
                Date date = rs.getDate("date_creation");
                int nbRevision = rs.getInt("nombre_revision");
                String nomType = rs.getString("nom");
                boolean confid = rs.getBoolean("confidentiel");

                ViewDocument viewDocument = new ViewDocument();

                viewDocument.setReferenceDocument(refDoc);
                viewDocument.setIdDocument(idDoc);
                viewDocument.setStatus(statut);
                viewDocument.setNom(titre);
                viewDocument.setDateCreation(date);
                viewDocument.setNombreRevision(nbRevision);
                viewDocument.setNomTypeDocument(nomType);
                viewDocument.setConfidentiel(confid);

                liste.add(viewDocument);
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

        List<String[]> processusDoc = getViewProcessusDocumentEnCours(connection);

        liste.forEach(inListe ->{
            List<ProcessusLie> listeProcessusL = processusDoc.stream()
            .filter(pl->pl[0].compareTo(inListe.getReferenceDocument()) == 0 && Integer.parseInt(pl[1]) == inListe.getIdDocument())
            .map(pl -> new ProcessusLie(Integer.parseInt(pl[2])))
            .collect(Collectors.toList());

            inListe.setListeProcessusLie(listeProcessusL);
        });

        return liste;
    }

    public List<ViewDocument> getAllDocumentEnCours(Connection connection)throws Exception{
        List<ViewDocument> liste = new ArrayList<>();
        String sql = "SELECT * FROM v_document_en_cours_owner";
        
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(sql)) {
            
            while(rs.next()){

                String refDoc = rs.getString("ref_document");
                int idDoc = rs.getInt("id_document");
                String statut = rs.getString("status");
                String titre = rs.getString("titre");
                Date date = rs.getDate("date_creation");
                int nbRevision = rs.getInt("nombre_revision");
                String nomType = rs.getString("nom");
                int matricule = rs.getInt("owner");
                boolean confid = rs.getBoolean("confidentiel");

                ViewDocument viewDocument = new ViewDocument(refDoc,idDoc,titre,statut,date,nbRevision,nomType,confid);
                viewDocument.setOwner(matricule);

                liste.add(viewDocument);
            }
            
            rs.close();
            statement.close();

        } catch (Exception e) {
            throw e;
        }

        List<String[]> processusDoc = getViewProcessusDocumentEnCours(connection);

        liste.forEach(inListe ->{
            List<ProcessusLie> listeProcessusL = processusDoc.stream()
            .filter(pl->pl[0].compareTo(inListe.getReferenceDocument()) == 0 && Integer.parseInt(pl[1]) == inListe.getIdDocument())
            .map(pl -> new ProcessusLie(Integer.parseInt(pl[2])))
            .collect(Collectors.toList());

            inListe.setListeProcessusLie(listeProcessusL);
        });

        return liste;
    }





}
