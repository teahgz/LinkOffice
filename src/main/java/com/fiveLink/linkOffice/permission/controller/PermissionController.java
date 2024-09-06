package com.fiveLink.linkOffice.permission.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class PermissionController {
	
	@GetMapping("/permission")
	public String listPermissions (Model model) {
		return "/admin/permission/permission_list";
    }
}

