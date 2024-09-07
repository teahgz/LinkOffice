package com.fiveLink.linkOffice.member.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fiveLink.linkOffice.inventory.domain.Inventory;
import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.organization.domain.Position;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="fl_member")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
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
	

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_no", insertable = false, updatable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_no", insertable = false, updatable = false)
    private Position position;
	
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
	
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Inventory> inventory; 

}