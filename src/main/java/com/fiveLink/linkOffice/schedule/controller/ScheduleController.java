package com.fiveLink.linkOffice.schedule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.service.ScheduleCategoryService;

@Controller
public class ScheduleController { 
	private final MemberService memberService; 
	private final ScheduleCategoryService scheduleCategoryService;
	
	@Autowired
	public ScheduleController(MemberService memberService, ScheduleCategoryService scheduleCategoryService) { 
	    this.memberService = memberService; 
	    this.scheduleCategoryService = scheduleCategoryService; 
	}
	
	@GetMapping("/schedule/category")
	public String listPermissions(Model model, @RequestParam(value = "id", required = false) Long id) { 
	    Long memberNo = memberService.getLoggedInMemberNo();  
	    List<MemberDto> memberDto = memberService.getMembersByNo(memberNo);
	    List<ScheduleCategoryDto> scheduleCategories = scheduleCategoryService.getAllScheduleCategories();
 
	    if (id == null && !scheduleCategories.isEmpty()) {
	        id = scheduleCategories.get(0).getSchedule_category_no();
	    }

	    ScheduleCategoryDto selectedCategory = null;
	    if (id != null) {
	        selectedCategory = scheduleCategoryService.getScheduleCategoryById(id);
	    }

	    model.addAttribute("memberdto", memberDto); 
	    model.addAttribute("scheduleCategories", scheduleCategories);
	    model.addAttribute("selectedCategory", selectedCategory); 
	    
	    return "/admin/schedule/scheduleCategory";
	}

	
	@GetMapping("/schedule/category/get/{categoryId}")
	@ResponseBody
	public ScheduleCategoryDto getScheduleCategory(@PathVariable("categoryId") Long id) {
	    System.out.println(id);
	    return scheduleCategoryService.getScheduleCategoryById(id);
	}

	// 수정
	@PostMapping("/schedule/category/update") 
	@ResponseBody
	public Map<String, String> updateScheduleCategory(@RequestBody Map<String, Object> payload) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "카테고리 수정 중 오류가 발생했습니다.");

	    try {
	        Long categoryId = Long.valueOf(payload.get("scheduleCategoryId").toString());
	        String categoryName = (String) payload.get("scheduleCategoryName");
	        String categoryColor = (String) payload.get("scheduleCategoryColor");
	        Long onlyAdmin = (long) ((Boolean) payload.get("onlyAdmin") ? 1 : 0);
	          
	        scheduleCategoryService.updateScheduleCategory(categoryId, categoryName, categoryColor, onlyAdmin);
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "카테고리가 성공적으로 수정되었습니다.");
	    } catch (Exception e) {
	        resultMap.put("res_msg", e.getMessage());
	    }
	    return resultMap;
	}

	// 삭제
	@PostMapping("/schedule/category/delete")
	@ResponseBody
	public Map<String, String> deleteScheduleCategory(@RequestBody Map<String, Object> payload) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "카테고리 삭제 중 오류가 발생했습니다.");

	    try {
	        Long categoryId = Long.valueOf(payload.get("scheduleCategoryId").toString());

	        boolean deleteSuccess = scheduleCategoryService.deleteScheduleCategory(categoryId);

	        if (deleteSuccess) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "카테고리가 성공적으로 삭제되었습니다.");
	        } else {
	            resultMap.put("res_code", "400");
	            resultMap.put("res_msg", "해당 카테고리로 등록된 일정이 있어 삭제가 불가능합니다.");
	        }
	    } catch (Exception e) {
	        resultMap.put("res_msg", e.getMessage());
	    }

	    return resultMap;
	}


}
