document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.cancel_button').addEventListener('click', function() {
        const vapNo = document.querySelector('#vacationapproval_no').value;
        const csrfToken = document.getElementById("csrf_token").value;

	       Swal.fire({
	        text: '삭제하시겠습니까?',
	        icon: 'warning',
	        showCancelButton: true,
	        confirmButtonColor: '#EEB3B3',
	        cancelButtonColor: '#C0C0C0',
	        confirmButtonText: '삭제',
	        cancelButtonText: '취소'
	    }).then((result) => {
	        if (result.isConfirmed) {  
	
	            fetch('/employee/vacationapproval/delete/' + vapNo, {
	                method: 'put',
	                headers: {
	                    'X-CSRF-TOKEN': csrfToken
	                },
	            })
	            .then(response => response.json())
	            .then(data => {
	                if (data.res_code == '200') {
	                    Swal.fire({
	                        icon: 'success',
	                        text: data.res_msg,
	                        confirmButtonColor: '#B1C2DD',
	                        confirmButtonText: "확인"
	                    }).then((result) => {
	                        location.href = "/employee/vacationapproval/list";
	                    });
	                } else {
	                    Swal.fire({
	                        icon: 'error',
	                        text: data.res_msg,
	                        confirmButtonColor: '#B1C2DD',
	                        confirmButtonText: "확인"
	                    });
	                }
	            });
	        }
	    });
	});

});
