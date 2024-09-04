package com.fiveLink.linkOffice.security.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String errorMsg;
        if (exception instanceof BadCredentialsException) {
            errorMsg = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else {
            errorMsg = "로그인 중 오류가 발생했습니다.";
        }

        request.getSession().setAttribute("error", errorMsg);

        response.sendRedirect("/");
    }

}
