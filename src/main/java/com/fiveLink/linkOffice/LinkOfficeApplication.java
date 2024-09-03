package com.fiveLink.linkOffice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fiveLink.linkOffice.mapper") // 패키지 설정 확인
public class LinkOfficeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkOfficeApplication.class, args);
	}
}
