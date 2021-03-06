package cz.reddawe.bowlingreservationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides password encoder bean.
 *
 * @author David Dvorak
 */
@Configuration
public class PasswordConfiguration {

    /**
     * Provides {@link PasswordEncoder}
     *
     * @return passwordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
