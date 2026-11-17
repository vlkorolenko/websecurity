package com.docushield.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.csrf.enabled:false}")
    private boolean csrfEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN", "MANAGER", "USER")
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(encoder.encode("manager123"))
                .roles("MANAGER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails guest = User.builder()
                .username("guest")
                .password(encoder.encode("guest123"))
                .roles("GUEST")
                .build();

        return new InMemoryUserDetailsManager(admin, manager, user, guest);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \""
                            + authException.getMessage() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \""
                            + accessDeniedException.getMessage() + "\"}");
                })
            );

        if (csrfEnabled) {
            http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/v1/csrf")
            );
        } else {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        http.authorizeHttpRequests(auth -> auth
            // Public info & CSRF token retrieval
            .requestMatchers("/api/v1/public/**", "/api/v1/csrf", "/actuator/health").permitAll()
            // Document CRUD permissions based on multi-user roles
            .requestMatchers(HttpMethod.GET, "/api/v1/documents/**").hasAnyRole("USER", "MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/documents/**").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/documents/**").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/documents/**").hasRole("ADMIN")
            // Audit log retrieval only for ADMIN
            .requestMatchers("/api/v1/audit-logs/**").hasRole("ADMIN")
            // All other requests require authentication
            .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", source.getCorsConfigurations().getOrDefault("/**", config));
        return source;
    }
}
