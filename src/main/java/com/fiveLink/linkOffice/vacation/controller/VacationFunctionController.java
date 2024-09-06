package com.fiveLink.linkOffice.vacation.controller;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.vacation.domain.Vacation;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

        try {
            Long memberNo = Long.parseLong((String) params.get("memberNo"));
            // 연차 입력 데이터 처리
            Map<String, Object> vacationData = (Map<String, Object>) params.get("vacationData");
            VacationDto dto = new VacationDto();
            dto.setMember_no(memberNo);
            dto.setVacationData(vacationData);

            int count = Integer.parseInt(String.valueOf(params.get("countVacation")));
            if(count > 0) {
                List<String> vacationPk = (List<String>) params.get("vacationPkData");
                for(int i = 0; i< vacationPk.size(); i++){

                    String pk = vacationPk.get(i);
                    dto.setVacation_no(Long.parseLong(pk));

                    List<Vacation> vacations = dto.toEntities();

                    dto.setVacation_no(vacations.get(i).getVacationNo());
                    dto.setVacation_annual_leave(vacations.get(i).getVacationAnnualLeave());
                    dto.setVacation_year(vacations.get(i).getVacationYear());
                    if (vacationService.addVacation(dto) > 0) {
                        resultMap.put("res_code", "200");
                        resultMap.put("res_msg", "성공적으로 생성되었습니다.");
                    }
                }

            }
            List<Vacation> vacations = dto.toEntities();

            for (Vacation vacation : vacations) {
                dto.setVacation_year(vacation.getVacationYear());
                dto.setVacation_annual_leave(vacation.getVacationAnnualLeave());

                if (vacationService.addVacation(dto) > 0) {
                    resultMap.put("res_code", "200");
                    resultMap.put("res_msg", "성공적으로 생성되었습니다.");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "404");
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
            String vacationType = (String) payload.get("vacationType");
            double vacationValue = Double.parseDouble((String)payload.get("vacationValue")) ;
            VacationTypeDto dto = new VacationTypeDto();
            dto.setVacation_type_name(vacationType);
            dto.setVacation_type_calculate(vacationValue);

                if(vacationService.addTypeVacation(dto)>0) {
                    resultMap.put("res_code", "200");
                    resultMap.put("res_msg", "성공적으로 생성되었습니다.");
                }


        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "404");
            resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
        }


        return resultMap;
    }
    // 휴가 종류 수정
    @PostMapping("/updateVacation")
    @ResponseBody
    public Map<String, String> updateVacation(@RequestBody Map<String, Object> payload) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "휴가 생성 중 오류가 발생했습니다.");


        try {
            Long vacationTypeNo = Long.parseLong((String) payload.get("vacationTypeNo"));
            String vacationTypeName = (String)payload.get("vacationTypeName");
            double vacationTypeCalculate = Double.parseDouble((String)payload.get("vacationTypeCalculate"));

            VacationTypeDto dto = new VacationTypeDto();

            dto.setVacation_type_no(vacationTypeNo);
            dto.setVacation_type_name(vacationTypeName);
            dto.setVacation_type_calculate(vacationTypeCalculate);
            if(vacationService.addTypeVacation(dto)>0) {
                resultMap.put("res_code", "200");
                resultMap.put("res_msg", "성공적으로 수정되었습니다.");
            }


        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "404");
            resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
        }


        return resultMap;

    }

    // 휴가 종류 삭제
    @PostMapping("/deleteVacation")
    @ResponseBody
    public Map<String, String> deleteVacation(@RequestBody Map<String, Object> payload) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "휴가 생성 중 오류가 발생했습니다.");


        try {
            Long vacationTypeNo = Long.parseLong((String) payload.get("vacationTypeNo"));
            String vacationTypeName = (String)payload.get("vacationTypeName");
            double VacationTypeCal = Double.parseDouble((String)payload.get("vacationTypeCal"));

            VacationTypeDto dto = new VacationTypeDto();

            dto.setVacation_type_no(vacationTypeNo);
            dto.setVacation_type_name(vacationTypeName);
            dto.setVacation_type_calculate(VacationTypeCal);
            dto.setVacation_type_status(1);

            if(vacationService.addTypeVacation(dto)>0) {
                resultMap.put("res_code", "200");
                resultMap.put("res_msg", "성공적으로 수정되었습니다.");
            }


        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "404");
            resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
        }


        return resultMap;

    }

}