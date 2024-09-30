document.addEventListener('DOMContentLoaded', function(){
	const approvalRows = document.querySelectorAll('.approval_row');
	    window.functionTypes = [9]; 
	approvalRows.forEach(row => {
		row.addEventListener('click',function(){
			const approvalNo = this.getAttribute('data_approval_no');
			
			window.location.href = '/employee/approval/approval_progress_detail/'+approvalNo;
			
				if (window.functionTypes.includes(9)) {
					markApprovalAsRead(9, approvalNo); 
				}
				

		});
	});
});