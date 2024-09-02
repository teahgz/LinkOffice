package com.fiveLink.linkOffice.document.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fl_document_file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DocumentFile {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_file_no")
    private Long documentFileNo;

    @Column(name = "document_ori_file_name")
    private String documentOriFileName;

    @Column(name = "document_new_file_name")
    private String documentNewFileName;

    @Column(name = "document_folder_no")
    private Long documentFolderNo;

    @Column(name = "member_no")
    private Long memberNo;
    
    @Column(name = "documemt_file_size")
    private String documentFileSize;

    @Column(name = "document_file_upload_date")
    private LocalDateTime documentFileUploadDate;

    @Column(name = "document_file_update_date")
    private LocalDateTime documentFileUpdateDate;
    
    @Column(name = "document_file_status")
    private Long documentFileStatus;
}
