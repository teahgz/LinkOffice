document.addEventListener('DOMContentLoaded', function(){
	const approvalRows = document.querySelectorAll('.approval_row');
	
	approvalRows.forEach(row => {
		row.addEventListener('click',function(){
			const approvalNo = this.getAttribute('data_approval_no');
			
			window.location.href = '/employee/approval/approval_reject_detail/'+approvalNo;
		});
	});
});