package com.fiveLink.linkOffice.approval.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.approval.domain.ApprovalForm;
import com.fiveLink.linkOffice.approval.domain.ApprovalFormDto;
import com.fiveLink.linkOffice.approval.repository.ApprovalFormRepository;

@Service
public class ApprovalFormService {
    private final ApprovalFormRepository approvalFormRepository;

    public ApprovalFormService(ApprovalFormRepository approvalFormRepository) {
        this.approvalFormRepository = approvalFormRepository;
    }
    
    public List<ApprovalFormDto> getAllApprovalForms() {
        List<ApprovalForm> forms = approvalFormRepository.findAllByApprovalFormStatusNot(1L);
        return forms.stream()
                .map(this::convertToDto)  
                .collect(Collectors.toList());
    }

    private ApprovalFormDto convertToDto(ApprovalForm form) {
        return ApprovalFormDto.builder()
                .approval_form_no(form.getApprovalFormNo())
                .approval_form_title(form.getApprovalFormTitle())
                .approval_form_content(form.getApprovalFormContent())
                .approval_form_create_date(form.getApprovalFormCreateDate())
                .approval_form_update_date(form.getApprovalFormUpdateDate())
                .approval_form_status(form.getApprovalFormStatus())
                .build();
    }
    
}