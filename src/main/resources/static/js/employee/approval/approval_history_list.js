document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.vacationapproval_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const vacationApprovalNo = this.getAttribute('data_vacationapproval_no');
			
			window.location.href = '/employee/approval/approval_history_detail/'+vacationApprovalNo;
		});
	});
});