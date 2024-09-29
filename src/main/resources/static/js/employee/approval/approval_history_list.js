document.addEventListener('DOMContentLoaded', function() {
    const approvalRow = document.querySelectorAll('.approval_row');
    window.functionTypes = [3, 5]; // 배열로 정의
    console.log("현재 기능 타입: " + window.functionTypes);
    approvalRow.forEach(row => {
        row.addEventListener('click', function() {
            const approvalNo = this.getAttribute('data-approval_no');
            const approvalType = this.getAttribute('data-type');
            
            if (approvalType === 'VACATION') {
                window.location.href = '/employee/approval/approval_history_vacation_detail/' + approvalNo;
                 if (window.functionTypes.includes(3) || window.functionTypes.includes(5)) {
                                     markApprovalAsRead(3, approvalNo); // 3 또는 5를 사용할 수 있습니다
                                     markApprovalAsRead(5, approvalNo); // 필요 시 둘 다 호출
                                 }
            } else {
                window.location.href = '/employee/approval/approval_history_detail/' + approvalNo;
            }
        });
    });
    
});
