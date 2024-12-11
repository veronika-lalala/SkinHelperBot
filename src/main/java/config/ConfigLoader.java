package config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    public Properties getProperties() {
        return properties;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Не удалось найти config.properties");
                return;
            }
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
