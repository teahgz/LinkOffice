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
				location.href = "/employee/approval/approval_history_vacation_detail/" + vacationapprovalNo;
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


// 승인 취소 업데이트
function cancelApproval(){
	 const csrfToken = document.querySelector('#csrf_token').value;
	 const vacationapprovalNo = document.querySelector('#vacationapproval_no').value;
	 
	fetch('/employee/vacationapproval/approvecancel/'+vacationapprovalNo,{
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
				location.href = "/employee/approval/approval_history_vacation_detail/" + vacationapprovalNo;
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


document.addEventListener('DOMContentLoaded', function() {

    const modal = document.getElementById("myModal");
    const closeModal = document.querySelector(".close");

	
	 document.querySelector('.reject_button').addEventListener('click', function() {
	        modal.style.display = "flex";
	    });

    closeModal.addEventListener('click', function() {
        modal.style.display = "none";
    });

	// 반려 버튼 업데이트
	document.getElementById('confirm_reject_button').addEventListener('click', function( ){
		const csrfToken = document.querySelector('#csrf_token').value;
		const vacationapprovalNo = document.querySelector('#vacationapproval_no').value;
		 const rejectReason = document.getElementById('reject_reason').value;
		 
		fetch('/employee/vacationapproval/reject/'+vacationapprovalNo,{
			method: 'put',
			headers: {
				'Content-Type': 'application/json',
				 'X-CSRF-TOKEN': csrfToken
			},
			body: JSON.stringify({
                vacation_approval_flow_reject_reason: rejectReason
            })
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
					location.href = "/employee/approval/approval_history_vacation_detail/" + vacationapprovalNo;
				});
			}else{
				Swal.fire({
				     icon: 'error',
				     text: data.res_msg,
				     confirmButtonColor: '#B1C2DD',
				     confirmButtonText: "확인"
				});
			}
		});
	});
});