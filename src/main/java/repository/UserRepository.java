package repository;


import connection.PostgresConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.logging.Logger;

public class UserRepository {
    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    public static User findByUsername(String username) {
        try (Connection conn = PostgresConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("email"));
            }
        } catch (Exception e) {
            logger.info(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}

