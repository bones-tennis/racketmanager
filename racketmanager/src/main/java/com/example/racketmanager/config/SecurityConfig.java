package com.example.racketmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.racketmanager.security.LoginFailureHandler;
import com.example.racketmanager.security.LoginSuccessHandler;
import com.example.racketmanager.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final LoginFailureHandler failureHandler;
    private final LoginSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          LoginFailureHandler failureHandler,
                          LoginSuccessHandler successHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup", "/login", "/css/**", "/images/**", "/js/**", "/liff/**").permitAll()
                .requestMatchers("/staff/**").hasRole("STAFF")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .sessionManagement(sm -> sm
                .sessionFixation(sf -> sf.migrateSession()) // セッション固定化対策
            )
            .authenticationProvider(authenticationProvider()) // ★ DaoAuthenticationProvider を適用
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)  // ★成功時（失敗回数リセット）
                .failureHandler(failureHandler)  // ★失敗時（カウント増＆ロック）
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // CSRFは「有効」がデフォルト。明示するならこう書く（無効化しない）
            .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // ★変数名注意
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
