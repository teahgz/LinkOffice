document.addEventListener('DOMContentLoaded', function(){
	const vacationapprovalRows = document.querySelectorAll('.vacationapproval_row');
	
	vacationapprovalRows.forEach(row => {
		row.addEventListener('click',function(){
			const vacationapprovalNo = this.getAttribute('data_vacationapproval_no');
			
			window.location.href = '/employee/vacationapproval/detail/'+vacationapprovalNo;
		});
	});
});