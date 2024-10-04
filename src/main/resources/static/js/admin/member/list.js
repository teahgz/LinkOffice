document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.member_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const memberNo = this.getAttribute('data_member_no');
			
			window.location.href = '/admin/member/detail/'+memberNo;
		});
	});
});

const location_text = document.getElementById('header_location_text');
location_text.innerHTML = '사원 관리&emsp;&gt;&emsp;사원 목록';