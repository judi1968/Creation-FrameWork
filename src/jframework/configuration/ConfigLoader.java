package jframework.configuration;

import jakarta.servlet.ServletContext;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    public static Properties load(ServletContext context) {
        Properties properties = new Properties();

        try (InputStream is =
                 context.getResourceAsStream("/WEB-INF/application.properties")) {

            if (is == null) {
                throw new RuntimeException("application.properties introuvable");
            }

            properties.load(is);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}
