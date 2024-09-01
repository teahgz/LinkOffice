package com.fiveLink.linkOffice.document.domain;

import java.time.LocalDateTime;

import com.fiveLink.linkOffice.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fl_document_folder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DocumentFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_folder_no")
    private Long documentFolderNo;

    @Column(name = "document_folder_name")
    private String documentFolderName;

    @Column(name = "document_folder_parent_no")
    private Long documentFolderParentNo;

    @Column(name = "document_folder_level")
    private Long documentFolderLevel;
    
    @Column(name = "department_no")
    private Long departmentNo;

    @Column(name = "document_box_type")
    private Long documentBoxType;
    
    @Column(name = "member_no")
    private Long memberNo;

    @Column(name = "document_folder_create_date")
    private LocalDateTime documentFolderCreateDate;

    @Column(name = "document_folder_update_date")
    private LocalDateTime documentFolderUpdateDate;

    @Column(name = "document_folder_status")
    private Long documentFolderStatus;
}
