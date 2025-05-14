package uit.fs.bibliotheque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import uit.fs.bibliotheque.model.RoleConstants;
import uit.fs.bibliotheque.service.UtilisateurService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/mon-compte", "/changer-mot-de-passe").authenticated()
                .requestMatchers("/dashboard","/dashboard/**").hasRole(RoleConstants.ADMINISTRATEUR)
                .requestMatchers("/inscription", "/connexion").anonymous()
                .requestMatchers("/", 
                "/css/**", "/bootstrap/**",
                 "/fonts/**", 
                 "/js/**", 
                 "/images/**", 
                 "/uploads/**",
                 "/accueil", "/livres", "/livres/**"
                 ).permitAll()
                // .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/connexion")
                .loginProcessingUrl("/connexion")
                .defaultSuccessUrl("/", true)
                .failureUrl("/connexion?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/connexion?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("bibliothequenumerique-remember-me-key")
                .tokenValiditySeconds(86400) // 1 jour
                .userDetailsService(utilisateurService)
                .rememberMeParameter("remember-me")
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(utilisateurService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}