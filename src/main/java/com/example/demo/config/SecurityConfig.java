package com.example.demo.config;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityConfig {

    @Autowired
    private AppUserRepository appUserRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()

            // ✅ MDC filter with null check to avoid crash
            .addFilterBefore((request, response, chain) -> {
                var auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    MDC.put("user", auth.getName());
                }
                chain.doFilter(request, response);
                MDC.clear();
            }, UsernamePasswordAuthenticationFilter.class)

            // ✅ Role-based access control
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/users").permitAll()     // ✅ Allow POST /users
                .requestMatchers("/items/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasRole("ADMIN")             // ✅ Protect GET/DELETE /users/{id}
                .anyRequest().authenticated()
            )

            .httpBasic();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            AppUser user = appUserRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().replace("ROLE_", "")) // e.g. ROLE_ADMIN → ADMIN
                    .build();
        };
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}