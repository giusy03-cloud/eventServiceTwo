package com.dipartimento.eventservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Consente tutte le rotte
                .allowedOrigins("http://localhost:4200","http://localhost:8081") // frontend origin
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // Consente i cookie/Authorization header
    }
}
