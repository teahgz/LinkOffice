document.addEventListener('DOMContentLoaded',function(){
	const formRows = document.querySelectorAll('.form_row');
	
	formRows.forEach(row => {
		row.addEventListener('click',function(){
			const formNo = this.getAttribute('data_form_no');
			
			window.location.href = '/admin/approval/detail/'+formNo;
		});
	});
});