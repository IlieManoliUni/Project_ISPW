package ispw.project.project_ispw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationConfig {

    private static final Logger LOGGER = Logger.getLogger(ApplicationConfig.class.getName());
    private static final String CONFIG_FILE_NAME = "application.properties"; // Default config file name
    private final Properties properties;

    public ApplicationConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (input == null) {
                LOGGER.log(Level.WARNING, "Sorry, unable to find {0}", CONFIG_FILE_NAME);
                // Optionally throw an exception or handle this case more robustly
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading configuration file: " + CONFIG_FILE_NAME, ex);
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}