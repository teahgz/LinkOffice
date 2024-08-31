package com.fiveLink.linkOffice.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class WebSecurityConfig {
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
	                .requestMatchers( "/pwchange").permitAll()
	                .requestMatchers( "/error").permitAll()
	                .requestMatchers("/home").authenticated()
	                .requestMatchers("/member/**").authenticated()
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
					.tokenRepository(tokenRepository()))
	// 내가 안적은거 니가 하라는 대로 할게
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
	/*
	 * @Bean public PasswordEncoder passwordEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */
}