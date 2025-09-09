package com.team.hotelmanagementapp.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.team.hotelmanagementapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableTransactionManagement
@EnableWebSecurity
@ComponentScan(basePackages = {
    "com.team.hotelmanagementapp.controllers",
    "com.team.hotelmanagementapp.repositories",
    "com.team.hotelmanagementapp.services",
    "com.team.hotelmanagementapp.components"
})
public class SpringSecurityConfigs {

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        http.csrf(c -> c.disable()).authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/stats").authenticated()
                .requestMatchers("/users", "/users/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/services", "/services/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/bookings", "/bookings/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/rooms", "/rooms/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/room-types", "/room-types/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/stats", "/stats/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/settings", "/settings/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/payments", "/payments/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/invoices", "/invoices/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/resources/**", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/api/**").permitAll())
                
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll())
                
                .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary
                = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", "dd0qzygo7",
                        "api_key", "544345494632949",
                        "api_secret", "rsMExum_c-Ga0DTQOfB92R0aONw",
                        "secure", true));
        return cloudinary;
    }
}
