package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;

import alphaciment.base_iso.model.viewmodel.ViewMyDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChampDocument {
    /**
     * Fields
     */
    String refDocument;
    int idDocument;
    String refChamp;
    String valeur;



    /**
     * Methods
     */
    /**
     * Get Champs By Document Ref
     */
    public List<ChampDocument> getChampsByDocumentRef(Connection connection, String documentRef) throws Exception {
        List<ChampDocument> champDocumentList = new ArrayList<>();
        String sql = "select * from champ_document where ref_document = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, documentRef);

            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                champDocumentList.add(new ChampDocument(rs.getString("ref_document"), rs.getInt("id_document"), rs.getString("ref_champ"), rs.getString("valeur")));
            }
        } catch (Exception e) {
            throw e;
        }

        return champDocumentList;
    }
}
