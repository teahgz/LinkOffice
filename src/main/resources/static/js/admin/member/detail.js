const memberName = document.getElementById('member_name').innerHTML;

function confirmEnd(){
	Swal.fire({
		text:'퇴사처리 하시겠습니까?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#B1C2DD',
        cancelButtonColor: '#C0C0C0',
        confirmButtonText: '확인',
        cancelButtonText: '취소'
	}).then((result) => {
		if(result.isConfirmed){
			const csrfToken = document.getElementById('csrf_token').value;
			const memberNo = document.getElementById('member_no').value;
			
			fetch('/admin/member/status/'+memberNo,{
				method : 'put',
				headers:{
					'X-CSRF-TOKEN':csrfToken
				}
			})
			.then(response => response.json())
			.then(data => {
				if(data.res_code == '200'){
					Swal.fire({
					icon : 'success',
					text : data.res_msg,
					confirmButtonColor: '#C0C0C0',
					confirmButtonText : "확인"
					}).then((result) =>{
						location.href="/admin/member/detail/"+memberNo;
					});
				} else {
					Swal.fire({
					icon : 'error',
					text : data.res_msg,
					confirmButtonColor: '#C0C0C0',
					confirmButtonText : "확인"
				});
			}}
		)}
	});
}
const location_text = document.getElementById('header_location_text');
location_text.innerHTML = '사원 관리&emsp;&gt;&emsp;사원 조회&emsp;&gt;&emsp;'+memberName;