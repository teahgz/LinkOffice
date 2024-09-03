package com.fiveLink.linkOffice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	private final MemberService memberService;
	
	@Autowired
	public HomeController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/home")
	public String home(HttpServletRequest request, Model model) {
	    HttpSession session = request.getSession();
	    String userNumber = (String) session.getAttribute("userNumber");
	//    MemberDto memberdto = memberService.getMemberByNumber(userNumber);
	    List<MemberDto> memberdto = memberService.getMemberByNumber(userNumber);
	    // 멤버 객체 전달
	    model.addAttribute("memberdto", memberdto);
	    
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