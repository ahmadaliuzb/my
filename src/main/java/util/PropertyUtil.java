package util;

import connection.PostgresConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    private static final String PROPERTIES_FILE = "my.properties";

    public static String getProperty(String propertyName) {
        Properties prop = new Properties();
        try (InputStream input = PostgresConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            prop.load(input);
            return prop.getProperty(propertyName);
        } catch (IOException ex) {
            throw new RuntimeException("Error reading " + PROPERTIES_FILE, ex);
        }
    }
}
