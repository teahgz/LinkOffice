package com.fiveLink.linkOffice.security.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                   Authentication authentication) throws IOException, ServletException {
	    // Authentication 객체에서 사용자 정보
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    String userNumber = userDetails.getUsername();
	    // 세션에 사용자 정보를 저장
	    HttpSession session = request.getSession();
	    session.setAttribute("userNumber", userNumber);
	    // 성공 시 리다이렉트할 URL 설정
	    response.sendRedirect("/home");
	}
}