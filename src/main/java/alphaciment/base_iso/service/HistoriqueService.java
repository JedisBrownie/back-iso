package alphaciment.base_iso.service;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import alphaciment.base_iso.model.connection.IsoDataSource;
import alphaciment.base_iso.model.object.HistoriqueEtat;

@Service
public class HistoriqueService {
    
    public void saveEtatHistorique(String ref_document,int id_document,int idUtilisateur,String motif,int idEtat) throws Exception{
        Connection connection = IsoDataSource.getConnection();
        HistoriqueEtat he = new HistoriqueEtat();
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



}
