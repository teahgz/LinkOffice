document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.member_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const memberNo = this.getAttribute('data_member_no');
			
			window.location.href = '/admin/member/detail/'+memberNo;
		});
	});
});