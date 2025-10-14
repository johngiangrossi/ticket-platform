package com.bool.ticketplatform.security;

import javax.swing.Spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()

            // .requestMatchers("/pizzas/create", "/pizzas/edit/**").hasAuthority("ADMIN")

            // .requestMatchers(HttpMethod.POST, "/pizzas/**").hasAuthority("ADMIN")

            // .requestMatchers("/ingredients", "/ingredients/**").hasAnyAuthority("ADMIN")

            // .requestMatchers("/pizzas", "/pizzas/**").hasAnyAuthority("USER", "ADMIN")

            .requestMatchers("/**").permitAll()

            .and().formLogin()

            .and().logout()

            .and().csrf().disable();

        return http.build();

    }


    @Bean
    DatabaseUserDetailService userDetailService() {
        return new DatabaseUserDetailService();
    }
    

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
