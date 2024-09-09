package com.fiveLink.linkOffice.notice.domain;

import java.time.LocalDateTime;


import com.fiveLink.linkOffice.member.domain.Member;


public class NoticeDto {

	private Long notice_no;
	private String notice_title;
	private String notice_text;
	private Member member_no;
	private LocalDateTime notice_create_date;
	private LocalDateTime notice_update_date;
	private String notice_ori_img;
	private String notice_new_img;
	
	public Notice toEntity() {
		
		

		 return Notice.builder()
			        .noticeNo(notice_no)
			        .noticeTitle(notice_title)
			        .noticeText(notice_text)
			        .noticeCreateDate(notice_create_date)
			        .noticeUpdateDate(notice_update_date)
			        .noticeOriImg(notice_ori_img)
			        .noticeNewImg(notice_new_img)
			        .build();			
	}
}
