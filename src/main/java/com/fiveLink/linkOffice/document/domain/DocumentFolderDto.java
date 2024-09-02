package com.fiveLink.linkOffice.document.domain;

import java.time.LocalDateTime;

import com.fiveLink.linkOffice.member.domain.Member;

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
public class DocumentFolderDto {
    private Long document_folder_no;         
    private String document_folder_name;  
    private Long document_folder_parent_no;      
    private Long document_folder_level;   
    private Long department_no;
    private Long document_box_type;  
//    private Long member_no;      
    private Long member_no;
    private LocalDateTime document_folder_create_date; 
    private LocalDateTime document_folder_update_date; 
    private Long document_folder_status;       

    // DTO를 Entity로 변경
    public DocumentFolder toEntity(Member member) {
        return DocumentFolder.builder()
                .documentFolderNo(document_folder_no)
                .documentFolderName(document_folder_name)
                .documentFolderParentNo(document_folder_parent_no)
                .documentFolderLevel(document_folder_level)
                .departmentNo(department_no)
                .documentBoxType(document_box_type)
//                .member(member)
                .memberNo(member_no)
                .documentFolderCreateDate(document_folder_create_date)
                .documentFolderUpdateDate(document_folder_update_date)
                .documentFolderStatus(document_folder_status)
                .build();
    }

    // Entity를 DTO로 변경
    public static DocumentFolderDto toDto(DocumentFolder documentFolder) {
        return DocumentFolderDto.builder()
                .document_folder_no(documentFolder.getDocumentFolderNo())
                .document_folder_name(documentFolder.getDocumentFolderName())
                .document_folder_parent_no(documentFolder.getDocumentFolderParentNo())
                .document_folder_level(documentFolder.getDocumentFolderLevel())
                .department_no(documentFolder.getDepartmentNo())
                .document_box_type(documentFolder.getDocumentBoxType())
//                .member_no(documentFolder.getMember().getMemberNo())
                .member_no(documentFolder.getMemberNo())
                .document_folder_create_date(documentFolder.getDocumentFolderCreateDate())
                .document_folder_update_date(documentFolder.getDocumentFolderUpdateDate())
                .document_folder_status(documentFolder.getDocumentFolderStatus())
                .build();
    }
}
