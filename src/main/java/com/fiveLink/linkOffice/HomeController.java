package com.fiveLink.linkOffice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	private static final Logger LOGGER
		= LoggerFactory.getLogger(HomeController.class);
	
	@GetMapping({"","/"})
	public String home() {

		
		LOGGER.info("게시판 프로그램 시작");
		return "home";
	}
}
