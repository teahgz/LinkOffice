package com.fiveLink.linkOffice.document.domain;

import java.time.LocalDateTime;

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
public class DocumentFileDto {
	private Long document_file_no;         
    private String document_ori_file_name;  
    private String document_new_file_name;  
    private Long document_folder_no;      
    private Long member_no;          
    private String documemt_file_size;
    private LocalDateTime document_file_upload_date; 
    private LocalDateTime document_file_update_date; 
    private Long document_file_status; 
    
    // DTO를 Entity로 변경
    public DocumentFile toEntity() {
        return DocumentFile.builder()
                .documentFileNo(document_file_no)
                .documentOriFileName(document_ori_file_name)
                .documentNewFileName(document_new_file_name)
                .documentFolderNo(document_folder_no)
                .memberNo(member_no)
                .documentFileSize(documemt_file_size)
                .documentFileUploadDate(document_file_upload_date)
                .documentFileUpdateDate(document_file_update_date)
                .documentFileStatus(document_file_status)
                .build();
    }
    // Entity를 DTO로 변경
    public static DocumentFileDto toDto(DocumentFile documentFile) {
        return DocumentFileDto.builder()
                .document_file_no(documentFile.getDocumentFileNo())
                .document_ori_file_name(documentFile.getDocumentOriFileName())
                .document_new_file_name(documentFile.getDocumentNewFileName())
                .document_folder_no(documentFile.getDocumentFolderNo())
                .member_no(documentFile.getMemberNo())
                .document_file_upload_date(documentFile.getDocumentFileUploadDate())
                .document_file_update_date(documentFile.getDocumentFileUpdateDate())
                .document_file_status(documentFile.getDocumentFileStatus())
                .build();
    }
}
