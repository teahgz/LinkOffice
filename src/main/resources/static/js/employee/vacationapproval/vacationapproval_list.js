document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.vacationapproval_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const memberNo = this.getAttribute('data_vacationapproval_no');
			
			window.location.href = '/employee/vacationapproval/detail/'+memberNo;
		});
	});
});