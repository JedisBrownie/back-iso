package alphaciment.base_iso.service;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.object.HistoriqueEtat;
import alphaciment.base_iso.model.object.Utilisateur;

@Service
public class HistoriqueService {
    
    public void saveEtatHistorique(String ref_document,int id_document,int idUtilisateur,int idEtat) throws Exception{
        Connection connection = IsoDataSource.getConnection();
        HistoriqueEtat he = new HistoriqueEtat();
        String motif = "";
        try {
            he.saveHistoriqueEtat(ref_document, id_document, idEtat, idUtilisateur, motif, connection);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
        }
    }


    public void saveEtatInvalidation(String ref_document,int id_document,int idUtilisateur,String motif) throws Exception{
        Connection connection = IsoDataSource.getConnection();

        HistoriqueEtat he = new HistoriqueEtat();
        connection.setAutoCommit(false);
        int etatInvalide = 3;
        int etatBrouillon = 1;
        try {
            he.saveHistoriqueEtat(ref_document, id_document, etatInvalide, idUtilisateur, motif, connection);
            he.saveHistoriqueEtat(ref_document, id_document, etatBrouillon, idUtilisateur, motif, connection);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }
    
    
    public void saveEtatDesapprobation(String ref_document,int id_document,int idUtilisateur,String motif) throws Exception{
        Connection connection = IsoDataSource.getConnection();

        HistoriqueEtat he = new HistoriqueEtat();
        connection.setAutoCommit(false);
        int etatInvalide = 3;
        int etatBrouillon = 1;
        try {
            he.saveHistoriqueEtat(ref_document, id_document, etatInvalide, idUtilisateur, motif, connection);
            he.saveHistoriqueEtat(ref_document, id_document, etatBrouillon, idUtilisateur, motif, connection);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }

    public void demandeRevision(String ref_document,int id_document,int idUtilisateur) throws Exception{
        Connection connection = IsoDataSource.getConnection();
        HistoriqueEtat he = new HistoriqueEtat();
        connection.setAutoCommit(false);
        int etatDemande = 7;
        try {
            boolean estRedacteur = new Utilisateur().isRedacteur(ref_document, id_document, idUtilisateur, connection);
            if(estRedacteur ==  true){
                System.out.println("Vous êtes autoriser");
                he.saveHistoriqueEtat(ref_document, id_document, etatDemande, idUtilisateur, ref_document, connection);
                connection.commit();
            }else{
                throw new Exception("Vous n'avez pas accés à ce document");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
        }
    }





}
