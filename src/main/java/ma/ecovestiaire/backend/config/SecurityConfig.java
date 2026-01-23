package ma.ecovestiaire.backend.config;

import ma.ecovestiaire.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/register", "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/items", "/items/*").permitAll()
                    .requestMatchers(HttpMethod.POST, "/payments/webhook").permitAll()

                    .requestMatchers("/admin/categories/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/items/*/favorite").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/items/*/favorite").authenticated()
                    .requestMatchers("/api/users/me/favorites").authenticated()

                    .requestMatchers(HttpMethod.POST, "/items/*/comments").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/comments/*").authenticated()
                    .requestMatchers(HttpMethod.GET, "/items/*/comments").permitAll()

                    .requestMatchers("/items/**").authenticated()
                    .requestMatchers("/orders/**").authenticated()
                    .requestMatchers("/payments/**").authenticated()
                    .anyRequest().permitAll()
            )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}