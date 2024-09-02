package com.fiveLink.linkOffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	@GetMapping({"","/"})
	public String loginPage() {
		return "login";
	}
	
	@GetMapping("/pwchange")
	public String pwchangePage() {
		return "pwchange";
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
	

}