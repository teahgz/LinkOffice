package com.fiveLink.linkOffice;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        // 404과 500 및 다른 페이지를 만들까 고민중
        if (statusCode != null) {
            if (statusCode == 404) {
                return "404";  
            } else if (statusCode == 500) {
                return "500";  
            }
        }
        return "error";  
    }
}
