package com.fiveLink.linkOffice.member.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="fl_member")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="member_no")
	private Long memberNo;
	
	@Column(name="member_number")
	private String memberNumber;

	@Column(name="member_pw")
	private String memberPw;
	
	@Column(name="member_name")
	private String memberName;
	
	@Column(name="member_national")
	private String memberNational;
	
	@Column(name="member_internal")
	private String memberInternal;
	
	@Column(name="member_mobile")
	private String memberMobile;
	
	@Column(name="department_no")
	private Long departmentNo;
	
	@Column(name="position_no")
	private Long positionNo;
	
	@Column(name="member_address")
	private String memberAddress;
	
	@Column(name="member_hire_date")
	private String memberHireDate;
	
	@Column(name="member_end_date")
	private LocalDateTime memberEndDate;
	
	@Column(name="member_create_date")
	@CreationTimestamp
	private LocalDateTime memberCreateDate;
	
	@Column(name="member_update_date")
	@UpdateTimestamp
	private LocalDateTime memberUpdateDate;
	
	@Column(name="member_ori_profile_img")
	private String memberOriProfileImg;
	
	@Column(name="member_new_profile_img")
	private String memberNewProfileImg;
	
	@Column(name="member_ori_digital_img")
	private String memberOriDigitalImg;
	
	@Column(name="member_new_digital_img")
	private String memberNewDigitalImg;
	
	@Column(name="member_status")
	private Long memberStatus;
	
	@Column(name="member_additional")
	private Long memberAdditional;
	
	
}
