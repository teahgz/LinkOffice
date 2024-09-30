document.addEventListener('DOMContentLoaded', function(){
	const vacationapprovalRows = document.querySelectorAll('.vacationapproval_row');
	    window.functionTypes = [5,6]; 
	    console.log("현재 기능 타입: " + window.functionTypes);	
	vacationapprovalRows.forEach(row => {
		row.addEventListener('click',function(){
			const vacationapprovalNo = this.getAttribute('data_vacationapproval_no');
			
			window.location.href = '/employee/vacationapproval/detail/'+vacationapprovalNo;
			
			if (window.functionTypes.includes(5)) {
					markApprovalAsRead(5, vacationapprovalNo); 
					console.log("문서번호 5 : " + vacationapprovalNo);	
				}
				
				if(window.functionTypes.includes(6)){
					markApprovalAsRead(6, vacationapprovalNo); 
					console.log("문서번호 6 : " + vacationapprovalNo);	
				}		
		});
	});
});

const location_text = document.getElementById('header_location_text');
location_text.innerHTML = '휴가&emsp;&gt;&emsp;휴가 신청함';