package com.SmartHealthRemoteSystem.SHSR.WebConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.SmartHealthRemoteSystem.SHSR.Service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // ✅ REQUIRED for @PreAuthorize to work
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService myUserDetailsService;
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfiguration(MyUserDetailsService myUserDetailsService, AuthenticationSuccessHandler successHandler) {
        this.myUserDetailsService = myUserDetailsService;
        this.successHandler = successHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure()
            .and()
            .authorizeRequests()
                .antMatchers("/registerPatient").permitAll()
                .antMatchers("/Sensor-data/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/assignpatient/**").hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/prescription/**").hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/patient/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .antMatchers("/pharmacist/**").hasAnyRole("ADMIN", "PHARMACIST")
                .antMatchers("/clinicassistant/**").hasAnyRole("ADMIN", "CLINIC_ASSISTANT", "DOCTOR") // ✅ updated
                .antMatchers("/js/**", "/css/**").permitAll()
            .and()
            .formLogin()
                .loginPage("/login").permitAll()
                .successHandler(successHandler)
            .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
            .and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/diagnosis/receiveSymptoms").permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}