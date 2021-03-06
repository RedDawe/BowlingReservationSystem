package cz.reddawe.bowlingreservationsystem.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Implements {@link WebSecurityConfigurerAdapter}
 *
 * @author David Dvorak
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()

                .authorizeRequests()
                    .antMatchers("api/v1/**")
                    .permitAll()
                    .and()

                .formLogin()
                    .defaultSuccessUrl("/")
                    .and()
                .httpBasic().disable();
    }
}
