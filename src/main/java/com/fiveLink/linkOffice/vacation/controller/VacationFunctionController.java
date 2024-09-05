package com.fiveLink.linkOffice.vacation.controller;

import com.fiveLink.linkOffice.vacation.domain.Vacation;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/vacation")
public class VacationFunctionController {

    private final VacationService vacationService;

    @Autowired
    public VacationFunctionController(VacationService vacationService){
        this.vacationService = vacationService;
    }

    @PostMapping("/addVacationAction")
    @ResponseBody
    public Map<String, String> addVacation(@RequestBody Map<String, Object> params) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "휴가 생성 중 오류가 발생했습니다.");
        System.out.println(params);
        try {
            Long memberNo = Long.parseLong((String) params.get("memberNo"));
            boolean lessThanOneYear = (Boolean) params.get("lessThanOneYear");

            // 연차 입력 데이터 처리
            Map<String, Object> vacationData = (Map<String, Object>) params.get("vacationData");

            VacationDto dto = new VacationDto();
            dto.setMember_no(memberNo);
            //dto.setLessThanOneYear(lessThanOneYear);  // 체크박스 상태 설정
            dto.setVacationData(vacationData);

            List<Vacation> vacations = dto.toEntities();
            System.out.println(vacations);
            for (Vacation vacation : vacations) {
                if (vacationService.addVacation(vacation) > 0) {
                    resultMap.put("res_code", "200");
                    resultMap.put("res_msg", "성공적으로 생성되었습니다.");
                } else {
                    resultMap.put("res_msg", "연차 정보를 추가하는 데 실패했습니다.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 휴가 종류 추가
    @PostMapping("/addTypeVacation")
    @ResponseBody
    public Map<String, String> addTypeVacation(@RequestBody Map<String, Object> payload) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "휴가 생성 중 오류가 발생했습니다.");

        try {
            List<Map<String, String>> vacationTypesList = (List<Map<String, String>>) payload.get("vacationTypes");
            System.out.println("Received Vacation Types List: " + vacationTypesList);

            VacationTypeDto dto = new VacationTypeDto();
            for(int i = 0; i<vacationTypesList.size(); i++ ){
                Map<String, String> vacationType = vacationTypesList.get(i);
                String name = vacationType.get("name");
                String description = vacationType.get("description");
                dto.setVacation_type_name(name);
                dto.setVacation_type_calculate(Integer.parseInt(description));
                System.out.println(dto);
                if (vacationService.addTypeVacation(dto) > 0) {
                    resultMap.put("res_code", "200");
                    resultMap.put("res_msg", "성공적으로 생성되었습니다.");
                }
            }


        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());

        }



        return resultMap;
    }
}