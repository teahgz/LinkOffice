package com.fiveLink.linkOffice.schedule.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.schedule.domain.Schedule;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleException;
import com.fiveLink.linkOffice.schedule.domain.ScheduleExceptionDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeat;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeatDto;
import com.fiveLink.linkOffice.schedule.service.ScheduleCategoryService;
import com.fiveLink.linkOffice.schedule.service.ScheduleService;

@Controller
public class ScheduleApiController {
	private final MemberService memberService;
	private final ScheduleCategoryService scheduleCategoryService;
	private final ScheduleService scheduleService;

	@Autowired
	public ScheduleApiController(MemberService memberService, ScheduleCategoryService scheduleCategoryService,
			ScheduleService scheduleService) {
		this.memberService = memberService;
		this.scheduleCategoryService = scheduleCategoryService;
		this.scheduleService = scheduleService;
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
		Long categoryId = Long.valueOf(payload.get("scheduleCategoryId").toString());
		String categoryName = (String) payload.get("scheduleCategoryName");
		String categoryColor = (String) payload.get("scheduleCategoryColor");
		Long onlyAdmin = (long) ((Boolean) payload.get("onlyAdmin") ? 1 : 0);

		Map<String, String> response = scheduleCategoryService.updateScheduleCategory(categoryId, categoryName,
				categoryColor, onlyAdmin);

		if (response.containsKey("success")) {
			response.put("res_code", "200");
		} else {
			response.put("res_code", "400");
		}

		return response;
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
				resultMap.put("res_msg", "카테고리가 삭제되었습니다.");
			} else {
				resultMap.put("res_code", "400");
				resultMap.put("res_msg", "해당 카테고리로 등록된 일정이 있어 삭제가 불가능합니다.");
			}
		} catch (Exception e) {
			resultMap.put("res_msg", e.getMessage());
		}
		return resultMap;
	}

	// 등록
	@PostMapping("/schedule/category/add")
	@ResponseBody
	public Map<String, String> addScheduleCategory(@RequestBody Map<String, Object> payload) {
		String categoryName = (String) payload.get("scheduleCategoryName");
		String categoryColor = (String) payload.get("scheduleCategoryColor");
		Long onlyAdmin = (long) ((Boolean) payload.get("onlyAdmin") ? 1 : 0);

		Map<String, String> response = scheduleCategoryService.addScheduleCategory(categoryName, categoryColor,
				onlyAdmin);

		if (response.containsKey("success")) {
			response.put("res_code", "200");
		} else {
			response.put("res_code", "400");
		}

		return response;
	}

	// 관리자 - 일정 등록 카테고리
	@GetMapping("/categories")
	@ResponseBody
	public List<ScheduleCategoryDto> getAlladminScheduleCategory() {
		System.out.println(scheduleCategoryService.getAlladminScheduleCategory());
		return scheduleCategoryService.getAlladminScheduleCategory();
	}

	// 관리자 - 일정 등록
	@PostMapping("/company/schedule/save")
	@ResponseBody
	public String saveSchedule(@RequestBody Map<String, Object> request) {
		Long memberNo = memberService.getLoggedInMemberNo();
		ScheduleDto scheduleDto = new ScheduleDto();
		ScheduleRepeatDto scheduleRepeatDto = new ScheduleRepeatDto();

		scheduleDto.setMember_no(memberNo);
		scheduleDto.setSchedule_title((String) request.get("title"));
		scheduleDto.setSchedule_comment((String) request.get("description"));
		scheduleDto.setSchedule_start_date((String) request.get("startDate"));
		scheduleDto.setSchedule_allday((Boolean) request.get("allDay") ? 1L : 0L);
		scheduleDto.setSchedule_end_date((String) request.get("endDate"));
		if (request.get("repeat") != null) {
			Object repeatValue = request.get("repeat");
			long repeat = repeatValue instanceof Integer ? ((Integer) repeatValue).longValue()
					: Long.parseLong(repeatValue.toString());
			scheduleDto.setSchedule_repeat(repeat != 0 ? 1L : 0L);

			if (repeat != 0) {
				scheduleRepeatDto.setSchedule_repeat_type(repeat);
			}
		}

		if (request.get("category") != null) {
			Object category = request.get("category");
			scheduleDto.setSchedule_category_no(
					category instanceof Integer ? ((Integer) category).longValue() : Long.parseLong((String) category));
		}

		scheduleDto.setSchedule_start_time((String) request.get("startTime"));
		scheduleDto.setSchedule_end_time((String) request.get("endTime"));

		if (request.get("schedule_day_of_week") != null) {
			Object scheduleDayOfWeek = request.get("schedule_day_of_week");
			scheduleRepeatDto.setSchedule_repeat_day(
					scheduleDayOfWeek instanceof Integer ? ((Integer) scheduleDayOfWeek).longValue()
							: Long.parseLong((String) scheduleDayOfWeek));
		}
		if (request.get("schedule_week") != null) {
			Object scheduleWeek = request.get("schedule_week");
			scheduleRepeatDto
					.setSchedule_repeat_week(scheduleWeek instanceof Integer ? ((Integer) scheduleWeek).longValue()
							: Long.parseLong((String) scheduleWeek));
		}
		if (request.get("schedule_date") != null) {
			Object scheduleDate = request.get("schedule_date");
			scheduleRepeatDto
					.setSchedule_repeat_date(scheduleDate instanceof Integer ? ((Integer) scheduleDate).longValue()
							: Long.parseLong((String) scheduleDate));
		}
		if (request.get("schedule_month") != null) {
			Object scheduleMonth = request.get("schedule_month");
			scheduleRepeatDto
					.setSchedule_repeat_month(scheduleMonth instanceof Integer ? ((Integer) scheduleMonth).longValue()
							: Long.parseLong((String) scheduleMonth));
		}
		if (request.get("repeatEndDate") != null) {
			scheduleRepeatDto.setSchedule_repeat_end_date((String) request.get("repeatEndDate"));
		}

		scheduleService.saveCompanySchedule(scheduleDto, scheduleRepeatDto);

		return "success";
	}

	// 관리자 - 월간 일정
	@ResponseBody
	@GetMapping("/api/company/schedules")
	public List<ScheduleDto> getSchedules() {
		List<Schedule> schedules = scheduleService.getAllSchedules();

		List<ScheduleDto> scheduleDtos = new ArrayList<>();
		for (Schedule schedule : schedules) {
			ScheduleDto dto = ScheduleDto.toDto(schedule);
			scheduleDtos.add(dto);
		}

		System.out.println("scheduleDtos : " + scheduleDtos);
		return scheduleDtos;
	}

	// 관리자 - 반복 일정
	@ResponseBody
	@GetMapping("/api/repeat/schedules")
	public List<ScheduleRepeatDto> getRepeatSchedules() {
		List<ScheduleRepeat> scheduleRepeats = scheduleService.getAllRepeatSchedules();

		List<ScheduleRepeatDto> scheduleRepeatDtos = new ArrayList<>();
		for (ScheduleRepeat scheduleRepeat : scheduleRepeats) {
			ScheduleRepeatDto dto = ScheduleRepeatDto.toDto(scheduleRepeat);
			scheduleRepeatDtos.add(dto);
		}

		System.out.println("scheduleRepeatDtos : " + scheduleRepeatDtos);
		return scheduleRepeatDtos;
	}

	// 관리자 - 일정 수정
	@ResponseBody
	@GetMapping("/schedule/edit/{eventNo}")
	public Map<String, Object> getScheduleById(@PathVariable("eventNo") Long eventNo) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			ScheduleDto scheduleDto = scheduleService.getScheduleById(eventNo);
			ScheduleRepeatDto scheduleRepeatDto = scheduleService.getScheduleRepeatById(eventNo); // 반복 일정 정보 가져오기

			if (scheduleDto != null) {
				resultMap.put("res_code", "200");
				resultMap.put("schedule", scheduleDto);
				resultMap.put("scheduleRepeat", scheduleRepeatDto);
			} else {
				resultMap.put("res_code", "404");
				resultMap.put("res_msg", "일정을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "서버 오류가 발생했습니다.");
			e.printStackTrace();
		}
		return resultMap;
	}
 
	// 관리자 일정 수정
	// 일반 수정
	@PostMapping("/company/schedule/edit/{eventId}")
	@ResponseBody
	public String editSchedule(@PathVariable("eventId") Long eventId, @RequestBody Map<String, Object> request) {
		System.out.println("Received data: " + request);

		ScheduleDto scheduleDto = new ScheduleDto();
		ScheduleRepeatDto scheduleRepeatDto = new ScheduleRepeatDto();

		scheduleDto.setSchedule_no(eventId);
		scheduleDto.setSchedule_title((String) request.get("title"));
		scheduleDto.setSchedule_comment((String) request.get("description"));
		scheduleDto.setSchedule_start_date((String) request.get("startDate"));
		scheduleDto.setSchedule_end_date((String) request.get("endDate"));
		scheduleDto.setSchedule_allday(Boolean.TRUE.equals(request.get("allDay")) ? 1L : 0L);
		scheduleDto.setSchedule_start_time((String) request.get("startTime"));
		scheduleDto.setSchedule_end_time((String) request.get("endTime"));
		scheduleDto.setSchedule_category_no(getLongValue(request.get("category")));
		scheduleDto.setSchedule_type(3L);

		Long repeatValue = getLongValue(request.get("repeat"));
		scheduleDto.setSchedule_repeat(repeatValue != null && repeatValue != 0 ? 1L : 0L);

		if (repeatValue != null && repeatValue != 0) {
			scheduleRepeatDto.setSchedule_repeat_type(repeatValue);
			scheduleRepeatDto.setSchedule_repeat_day(getLongValue(request.get("schedule_day_of_week")));
			scheduleRepeatDto.setSchedule_repeat_week(getLongValue(request.get("schedule_week")));
			scheduleRepeatDto.setSchedule_repeat_date(getLongValue(request.get("schedule_date")));
			scheduleRepeatDto.setSchedule_repeat_month(getLongValue(request.get("schedule_month")));
			scheduleRepeatDto.setSchedule_repeat_end_date((String) request.get("repeatEndDate"));
		}

		scheduleService.updateCompanySchedule(eventId, scheduleDto, scheduleRepeatDto);

		return "success";
	}

	private Long getLongValue(Object value) {
		if (value == null)
			return null;
		if (value instanceof Integer)
			return ((Integer) value).longValue();
		if (value instanceof Long)
			return (Long) value;
		if (value instanceof String) {
			try {
				return Long.parseLong((String) value);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	
	// 관리자 - 반복 일정 수정 
	@ResponseBody
	@PostMapping("/company/schedule/edit/recurring/{eventId}")
	public Map<String, String> editRecurringEvent(
	        @PathVariable("eventId") Long eventId,
	        @RequestBody Map<String, Object> request,  
	        @RequestParam("editOption") Long editOption,
	        @RequestParam("pickStartDate") String pickStartDate,
	        @RequestParam("pickEndDate") String pickEndDate) { 
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "일정 수정 중 오류가 발생했습니다.");

	    ScheduleDto scheduleDto = new ScheduleDto();
	    ScheduleRepeatDto scheduleRepeatDto = new ScheduleRepeatDto();
	    Long memberNo = memberService.getLoggedInMemberNo();
 
	    scheduleDto.setMember_no(memberNo);
	    scheduleDto.setSchedule_title((String) request.get("title"));
	    scheduleDto.setSchedule_comment((String) request.get("description"));
	    scheduleDto.setSchedule_start_date((String) request.get("startDate"));
	    scheduleDto.setSchedule_allday((Boolean) request.get("allDay") ? 1L : 0L);
	    scheduleDto.setSchedule_end_date((String) request.get("endDate"));
	    scheduleDto.setSchedule_start_time((String) request.get("startTime"));
	    scheduleDto.setSchedule_end_time((String) request.get("endTime"));

	    // 반복 설정
	    if (request.get("repeat") != null) {
	        Object repeatValue = request.get("repeat");
	        long repeat = repeatValue instanceof Integer ? ((Integer) repeatValue).longValue()
	                : Long.parseLong(repeatValue.toString());
	        scheduleDto.setSchedule_repeat(repeat != 0 ? 1L : 0L);

	        if (repeat != 0) {
	            scheduleRepeatDto.setSchedule_repeat_type(repeat);
	        }
	    }

	    if (request.get("category") != null) {
	        Object category = request.get("category");
	        scheduleDto.setSchedule_category_no(
	                category instanceof Integer ? ((Integer) category).longValue() : Long.parseLong((String) category));
	    }

	    // 반복 주기 설정
	    if (request.get("schedule_day_of_week") != null) {
	        Object scheduleDayOfWeek = request.get("schedule_day_of_week");
	        scheduleRepeatDto.setSchedule_repeat_day(
	                scheduleDayOfWeek instanceof Integer ? ((Integer) scheduleDayOfWeek).longValue()
	                        : Long.parseLong((String) scheduleDayOfWeek));
	    }
	    if (request.get("schedule_week") != null) {
	        Object scheduleWeek = request.get("schedule_week");
	        scheduleRepeatDto.setSchedule_repeat_week(
	                scheduleWeek instanceof Integer ? ((Integer) scheduleWeek).longValue()
	                        : Long.parseLong((String) scheduleWeek));
	    }
	    if (request.get("schedule_date") != null) {
	        Object scheduleDate = request.get("schedule_date");
	        scheduleRepeatDto.setSchedule_repeat_date(
	                scheduleDate instanceof Integer ? ((Integer) scheduleDate).longValue()
	                        : Long.parseLong((String) scheduleDate));
	    }
	    if (request.get("schedule_month") != null) {
	        Object scheduleMonth = request.get("schedule_month");
	        scheduleRepeatDto.setSchedule_repeat_month(
	                scheduleMonth instanceof Integer ? ((Integer) scheduleMonth).longValue()
	                        : Long.parseLong((String) scheduleMonth));
	    }
	    if (request.get("repeatEndDate") != null) {
	        scheduleRepeatDto.setSchedule_repeat_end_date((String) request.get("repeatEndDate"));
	    }
 
	    try {
	        switch (editOption.intValue()) {
	            case 1: // 이 일정만 수정
	                scheduleService.updateSingleEvent(eventId, scheduleDto, scheduleRepeatDto, pickStartDate, pickEndDate);
	                resultMap.put("res_code", "200");
	                resultMap.put("res_msg", "이 일정만 수정되었습니다.");
	                break;

	            case 2: // 이 일정 및 향후 일정 수정
	                scheduleService.updateFutureEvents(eventId, scheduleDto, scheduleRepeatDto, pickStartDate);
	                resultMap.put("res_code", "200");
	                resultMap.put("res_msg", "이 일정 및 향후 일정 수정되었습니다.");
	                break;

	            case 3: // 모든 일정 수정
	                scheduleService.updateAllEvents(eventId, scheduleDto, scheduleRepeatDto);
	                resultMap.put("res_code", "200");
	                resultMap.put("res_msg", "모든 일정이 수정되었습니다.");
	                break;

	            default:
	                resultMap.put("res_code", "400");
	                resultMap.put("res_msg", "유효하지 않은 수정 옵션입니다.");
	                break;
	        }
	    } catch (Exception e) {
	        resultMap.put("res_msg", e.getMessage());
	    }

	    return resultMap;
	}
	
	// 예외 일정
	@ResponseBody
	@GetMapping("/api/company/exception/schedules")
	public List<ScheduleExceptionDto> getExceptionSchedules() {
		List<ScheduleException> exceptionSchedules = scheduleService.getAllExceptionSchedules();

		List<ScheduleExceptionDto> scheduleExceptionDtos = new ArrayList<>();
		for (ScheduleException scheduleException : exceptionSchedules) {
			ScheduleExceptionDto dto = ScheduleExceptionDto.toDto(scheduleException);
			scheduleExceptionDtos.add(dto);
		}

		System.out.println("scheduleExceptionDtos : " + scheduleExceptionDtos);
		return scheduleExceptionDtos;
	}
	
	// 예외 상세
	@ResponseBody
	@GetMapping("/schedule/exception/edit/{eventNo}")
	public Map<String, Object> getExceptionScheduleById(@PathVariable("eventNo") Long eventNo) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			ScheduleExceptionDto scheduleExceptionDto = scheduleService.getScheduleExceptionById(eventNo); 

			if (scheduleExceptionDto != null) {
				resultMap.put("res_code", "200");
				resultMap.put("schedule", scheduleExceptionDto); 
			} else {
				resultMap.put("res_code", "404");
				resultMap.put("res_msg", "일정을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "서버 오류가 발생했습니다.");
			e.printStackTrace();
		}
		return resultMap;
	}

	// 예외 일정 수정
	@PostMapping("/company/schedule/exception/edit/{eventId}")
	@ResponseBody
	public String editExceptionSchedule(@PathVariable("eventId") Long eventId, @RequestBody Map<String, Object> request) {

		ScheduleExceptionDto scheduleExceptionDto = new ScheduleExceptionDto(); 

		scheduleExceptionDto.setSchedule_exception_no(eventId);
		scheduleExceptionDto.setSchedule_exception_title((String) request.get("title"));
		scheduleExceptionDto.setSchedule_exception_comment((String) request.get("description"));
		scheduleExceptionDto.setSchedule_exception_start_date((String) request.get("startDate"));
		scheduleExceptionDto.setSchedule_exception_end_date((String) request.get("endDate"));
		scheduleExceptionDto.setSchedule_exception_allday(Boolean.TRUE.equals(request.get("allDay")) ? 1L : 0L);
		scheduleExceptionDto.setSchedule_exception_start_time((String) request.get("startTime"));
		scheduleExceptionDto.setSchedule_exception_end_time((String) request.get("endTime"));
		scheduleExceptionDto.setSchedule_exception_category_no(getLongValue(request.get("category")));

		scheduleService.updateCompanyExceptionSchedule(eventId, scheduleExceptionDto);

		return "success";
	}
	
	// 기본 일정 삭제
	@PostMapping("/company/schedule/delete")
    @ResponseBody
    public Map<String, String> deleteScheduleCompany(@RequestBody Map<String, Object> payload) {
        Map<String, String> resultMap = new HashMap<>();
        Long eventId = Long.valueOf(payload.get("eventId").toString());

        boolean isDeleted = scheduleService.deleteBasicSchedule(eventId);

        if (isDeleted) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "일정이 삭제되었습니다.");
        } else {
            resultMap.put("res_code", "404");
            resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
        }

        return resultMap;
    }
	
	// 예외 일정 삭제
	@PostMapping("/company/schedule/exception/delete")
    @ResponseBody
    public Map<String, String> deleteScheduleExceptionCompany(@RequestBody Map<String, Object> payload) {
        Map<String, String> resultMap = new HashMap<>();
        Long eventId = Long.valueOf(payload.get("eventId").toString());

        boolean isDeleted = scheduleService.deleteExceptionSchedule(eventId);

        if (isDeleted) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "일정이 삭제되었습니다.");
        } else {
            resultMap.put("res_code", "404");
            resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
        }

        return resultMap;
    }
	
	// 관리자 - 반복 일정 삭제
	@ResponseBody
	@PostMapping("/company/schedule/repeat/delete/{eventId}")
	public Map<String, String> deleteRepeatEvent(
			@PathVariable("eventId") Long eventId,  
	        @RequestParam("editOption") Long editOption,
	        @RequestParam("pickStartDate") String pickStartDate,
	        @RequestParam("pickEndDate") String pickEndDate) { 
		
		Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "일정 수정 중 오류가 발생했습니다.");
	     
	    ScheduleDto scheduleDto = scheduleService.getScheduleById(eventId);
		ScheduleRepeatDto scheduleRepeatDto = scheduleService.getScheduleRepeatById(eventId); 
	     
	    boolean isDeleted;
	    
	    try {
	        switch (editOption.intValue()) {
	            case 1: // 이 일정만 삭제
	            	isDeleted = scheduleService.deleteSingleEvent(eventId, scheduleDto, scheduleRepeatDto, pickStartDate, pickEndDate);
	            	if (isDeleted) {
	                    resultMap.put("res_code", "200");
	                    resultMap.put("res_msg", "이 일정만 삭제되었습니다.");
	                } else {
	                    resultMap.put("res_code", "404");
	                    resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
	                }
	                break;

	            case 2: // 이 일정 및 향후 일정 삭제
	            	isDeleted = scheduleService.deleteFutureEvents(eventId, scheduleDto, scheduleRepeatDto, pickStartDate, pickEndDate);
	            	if (isDeleted) {
	                    resultMap.put("res_code", "200");
	                    resultMap.put("res_msg", "이 일정 및 향후 일정이 삭제되었습니다.");
	                } else {
	                    resultMap.put("res_code", "404");
	                    resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
	                }
	                break;

	            case 3: // 모든 일정 삭제
	            	isDeleted = scheduleService.deleteAllEvents(eventId);
	            	if (isDeleted) {
	                    resultMap.put("res_code", "200");
	                    resultMap.put("res_msg", "모든 일정이 삭제되었습니다.");
	                } else {
	                    resultMap.put("res_code", "404");
	                    resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
	                }
	                break;

	            default:
	            	resultMap.put("res_code", "404");
                    resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
	                break;
	        }
	    } catch (Exception e) {
	        resultMap.put("res_msg", e.getMessage());
	    }

	    return resultMap;
	}
}
