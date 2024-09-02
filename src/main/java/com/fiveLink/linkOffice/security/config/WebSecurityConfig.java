package com.fiveLink.linkOffice.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Configuration
public class WebSecurityConfig implements HttpSessionListener {

    private final DataSource dataSource;
    
    @Autowired
    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/css/**", "/img/**", "/js/**").permitAll()
                    .requestMatchers("/pwchange", "/error", "/session-time").permitAll()
                    .requestMatchers("/**").authenticated()
                    .requestMatchers("/home","/employee/member/**").authenticated()
            )
            .formLogin(login ->
                login
                    .loginPage("/")
                    .loginProcessingUrl("/login")
                    .usernameParameter("member_number")
                    .passwordParameter("member_pw")
                    .permitAll()
                    .successHandler(new MyLoginSuccessHandler())
                    .failureHandler(new MyLoginFailureHandler()))
                    
            .logout(logout ->
                logout.permitAll())
            .rememberMe(rememberMe -> 
                rememberMe.rememberMeParameter("remember-me")
                    .tokenValiditySeconds(86400*7)
                    .alwaysRemember(false)
                    .tokenRepository(tokenRepository())
                    .authenticationSuccessHandler(rememberMeSuccessHandler()))
            .sessionManagement(sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) 
                    .invalidSessionUrl("/") 
                    .maximumSessions(1) 
                    .expiredUrl("/") 
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean 
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> 
                web.ignoring()
                    .requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations()
                    ));
    }
    
    // remember-me 성공 시 핸들러
    @Bean
    public AuthenticationSuccessHandler rememberMeSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/home");
        return handler;
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session created: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session destroyed: " + se.getSession().getId());
    }
    
    /*
	 * @Bean public PasswordEncoder passwordEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */
}