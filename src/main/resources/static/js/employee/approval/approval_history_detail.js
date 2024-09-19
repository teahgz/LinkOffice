// 승인 버튼 업데이트
function approveRequest(){
	 const csrfToken = document.querySelector('#csrf_token').value;
	 const vacationapprovalNo = document.querySelector('#vacationapproval_no').value;
	 
	fetch('/employee/vacationapproval/approve/'+vacationapprovalNo,{
		method: 'put',
		headers: {
			 'X-CSRF-TOKEN': csrfToken
		}
	})
	.then(reponse => reponse.json())
	.then(data => {
		if(data.res_code == '200'){
			Swal.fire({
				icon: 'success',
			    text: data.res_msg,
			    confirmButtonColor: '#B1C2DD',
			    confirmButtonText: "확인"
			}).then((result) => {
				location.href = "/employee/approval/approval_history_detail/" + vacationapprovalNo;
			});
		}else{
			Swal.fire({
			     icon: 'error',
			     text: data.res_msg,
			     confirmButtonColor: '#B1C2DD',
			     confirmButtonText: "확인"
			});
		}
	})
}