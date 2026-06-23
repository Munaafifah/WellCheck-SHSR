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
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService myUserDetailsService;
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfiguration(MyUserDetailsService myUserDetailsService,
            AuthenticationSuccessHandler successHandler) {
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
                .authorizeRequests()

                // ── Public routes ──
                .antMatchers("/registerPatient").permitAll()
                .antMatchers("/Sensor-data/**").permitAll()
                .antMatchers("/api/diagnosis/receiveSymptoms").permitAll()
                .antMatchers("/js/**", "/css/**", "/image/**").permitAll()

                // ── Patient sensor routes (specific, before any catch-all) ──
                .antMatchers("/register").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/sensor/agreement").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/sensor/request").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")

                // ── Admin specific routes (MUST be before /admin/**) ──
                .antMatchers("/admin/assign-sensor",
                             "/admin/assign-sensor/**",
                             "/admin/sensor-status",
                             "/admin/doctor-schedule/**").hasRole("ADMIN")

                // ── Admin catch-all ──
                .antMatchers("/admin/**").hasRole("ADMIN")

                // ── Doctor routes ──
                .antMatchers("/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/assignpatient/**").hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/prescription/**").hasAnyRole("ADMIN", "DOCTOR")

                // ── Patient routes ──
                .antMatchers("/patient/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")

                // ── Pharmacist routes ──
                .antMatchers("/pharmacist/**").hasAnyRole("ADMIN", "PHARMACIST")

                // ── Clinic Assistant routes ──
                .antMatchers("/clinicassistant/**")
                    .hasAnyRole("ADMIN", "CLINIC_ASSISTANT", "DOCTOR")

                // ── Radiographer routes ──
                .antMatchers("/radiographer/**").hasAnyRole("ADMIN", "RADIOGRAPHER")

                // ── Radiologist routes ──
                .antMatchers("/radiologist/**").hasAnyRole("ADMIN", "RADIOLOGIST")

                // ── Communication routes ──
                .antMatchers("/communication/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST", "CLINIC_ASSISTANT")
                .antMatchers("/api/chats/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST", "CLINIC_ASSISTANT")
                .antMatchers("/api/messages/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST", "CLINIC_ASSISTANT")
                .antMatchers("/api/users/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST", "CLINIC_ASSISTANT")

                // ── Radiology routes ──
                .antMatchers("/radiology", "/radiology/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST")
                .antMatchers("/api/images/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOGRAPHER", "RADIOLOGIST", "PATIENT")

                // ── Radiology Request & Scheduling routes (UCR008-UCR015) ──
                // UCR008 — submit request: Doctor only
                .antMatchers("/request-scheduling")
                    .hasAnyRole("ADMIN", "DOCTOR")
                .antMatchers("/imaging-request-form")
                    .hasAnyRole("ADMIN", "DOCTOR")
                // UCR009 — manage requests: Radiologist / Radiographer
                .antMatchers("/manage-requests")
                    .hasAnyRole("ADMIN", "RADIOLOGIST", "RADIOGRAPHER")
                // UCR010 — appointment scheduling: Radiographer
                .antMatchers("/appointment-scheduling")
                    .hasAnyRole("ADMIN", "RADIOGRAPHER")
                // UCR013 — view request status: Admin / Doctor only
                .antMatchers("/request-status")
                    .hasAnyRole("ADMIN", "DOCTOR")
                // Notifications: all scheduling module roles
                .antMatchers("/notifications")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOLOGIST", "RADIOGRAPHER")
                // API: imaging requests — Doctor submits, Radiologist/Radiographer manage/cancel
                .antMatchers("/api/imaging-requests/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOLOGIST", "RADIOGRAPHER")
               // API: appointments — Radiographer schedules, Radiologist/Radiographer cancel/confirm
                .antMatchers("/api/appointments/**")
                    .hasAnyRole("ADMIN", "RADIOGRAPHER", "RADIOLOGIST")
                // API: rooms — Radiographer/Radiologist check room availability when scheduling
                .antMatchers("/api/rooms/**")
                    .hasAnyRole("ADMIN", "RADIOGRAPHER", "RADIOLOGIST")
                // API: notifications — all scheduling module roles
                .antMatchers("/api/notifications/**")
                    .hasAnyRole("ADMIN", "DOCTOR", "RADIOLOGIST", "RADIOGRAPHER")
                // ── Shared patient/doctor/admin routes ──
                .antMatchers("/DiagnosisResult")
                    .hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/predictionHistory")
                    .hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/Health-status/**")
                    .hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/ViewDailyHealthSymptom/**")
                    .hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                .antMatchers("/viewPatientHealthStatus/**")
                    .hasAnyRole("PATIENT", "ADMIN", "DOCTOR")

                // ── Any other request must be authenticated ──
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .successHandler(successHandler)

                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")

               .and()
.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}