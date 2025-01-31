package alphaciment.base_iso.model.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    /**
     * Fields
     */
    int userSerialId;
    String userMatricule;
    String userFirstName;
    String userLastName;
    String userEmail;
    String userPassword;


    /**
     * Methods
     */
    /**
     * Get User By Full Name
     */
    public static User getUserByFullName(Connection connection, String userFirstName, String userLastName) throws Exception {
        String sql = "SELECT * FROM users WHERE user_first_name = ? AND user_last_name = ?";
        User user = new User();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userFirstName);
            statement.setString(2, userLastName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                user.setUserMatricule(rs.getString("user_matricule"));
                user.setUserEmail(rs.getString("user_email"));
            }
        } catch (Exception e) {
            throw e;
        }

        return user;
    }
}