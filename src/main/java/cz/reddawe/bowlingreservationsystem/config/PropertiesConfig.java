package cz.reddawe.bowlingreservationsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Loads application properties from configuration file.
 *
 * @author David Dvorak
 */
@Configuration
@ConfigurationProperties(prefix = "application")
class PropertiesConfig {

    private String managerPassword;

    String getManagerPassword() {
        return managerPassword;
    }

    void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }
}
