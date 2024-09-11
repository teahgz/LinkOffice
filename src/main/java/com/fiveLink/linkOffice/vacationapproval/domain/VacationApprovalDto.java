package com.fiveLink.linkOffice.vacationapproval.domain;

import java.time.LocalDateTime;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.vacation.domain.VacationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class VacationApprovalDto {
	
	private Long vacation_approval_no;
	private Long member_no;
	private String vacation_approval_title;
	private Long vacation_type_no;
	private String vacation_approval_start_date;
	private String vacation_approval_end_date;
	private String vacation_approval_total_days;
	private String vacation_approval_content;
	private Long vacation_approval_status;
	private String vacation_approval_cancel_reason;
	private LocalDateTime vacation_approval_create_date;
	private LocalDateTime vacation_approval_update_date;
	
	private String member_name;
	private int search_type = 1;
	private String search_text;
	private String format_vacation_approval_create_date;
	
	public VacationApproval toEntity(Member member, VacationType vacationType) {
		return VacationApproval.builder()
				.vacationApprovalNo(vacation_approval_no)
				.member(member)
				.vacationApprovalTitle(vacation_approval_title)
				.vacationType(vacationType)
				.vacationApprovalStartDate(vacation_approval_start_date)
				.vacationApprovalEndDate(vacation_approval_end_date)
				.vacationApprovalTotalDays(vacation_approval_total_days)
				.vacationApprovalContent(vacation_approval_content)
				.vacationApprovalStatus(vacation_approval_status)
				.vacationApprovalCancelReason(vacation_approval_cancel_reason)
				.vacationApprovalCreateDate(vacation_approval_create_date)
				.vacationApprovalUpdateDate(vacation_approval_update_date)
				.build();
	}
	
}
