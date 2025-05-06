package com.dipartimento.eventservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import static org.springframework.security.config.Customizer.withDefaults;

//Per il momento è così, poi quando verrà collegato il microservizio User & AuthService
// bisogna modificare questa classe e adattarla al microservizio

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // 🔒 TUTTE le richieste devono essere autenticate
                )
                .httpBasic(withDefaults()); // 🔐 Basic Auth

        return http.build();
    }

}