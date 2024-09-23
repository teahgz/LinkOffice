document.addEventListener('DOMContentLoaded', function() {
    const approvalRow = document.querySelectorAll('.approval_row');

    approvalRow.forEach(row => {
        row.addEventListener('click', function() {
            const approvalNo = this.getAttribute('data-approval_no');
            const approvalType = this.getAttribute('data-type');
            
            alert("Approval Type: " + approvalType); 
            if (approvalType === 'VACATION') {
                window.location.href = '/employee/approval/approval_history_vacation_detail/' + approvalNo;
            } else {
                window.location.href = '/employee/approval/approval_history_detail/' + approvalNo;
            }
        });
    });
});
