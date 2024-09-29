document.addEventListener('DOMContentLoaded', function(){
	const approvalRows = document.querySelectorAll('.approval_row');
	    window.functionTypes = [10]; 
	    console.log("현재 기능 타입: " + window.functionTypes);			
	approvalRows.forEach(row => {
		row.addEventListener('click',function(){
			const approvalNo = this.getAttribute('data_approval_no');
			
			window.location.href = '/employee/approval/approval_reject_detail/'+approvalNo;
				if(window.functionTypes.includes(10)){
					markApprovalAsRead(10, approvalNo); 
					console.log("문서번호 10 : " + approvalNo);	
				}				
		});
	});
});