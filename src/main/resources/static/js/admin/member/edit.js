function previewImage(event) {
    const file = event.target.files[0];

    if (file) {
        const fileType = file.type;
        const validTypes = ['image/png', 'image/jpeg'];

        if (validTypes.includes(fileType)) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.getElementById('profileImg');
                img.src = e.target.result;
            }

            reader.readAsDataURL(file);
        } else {
            Swal.fire({
                icon: 'error',
                text: 'PNG 또는 JPG 파일만 등록 가능합니다.',
                confirmButtonColor: '#B1C2DD',
                confirmButtonText: '확인'
            });

            document.getElementById('profile_img').value = "";
        }
    }
}

const editFrm = document.getElementById('editFrm');

editFrm.addEventListener('submit',(e)=>{
	e.preventDefault();
	
	const csrfToken = document.getElementById("csrf_token").value;	
	const memberNo = document.getElementById("member_no").value;	
	
	const formData = new FormData(editFrm);

	fetch('/admin/member/edit/'+memberNo,{
		method:'put',
		headers :{
			'X-CSRF-TOKEN':csrfToken
		},
		body: formData
	})
	  .then(response => response.json())
        .then(data => {
			if(data.res_code == '200'){
				Swal.fire({
				icon : 'success',
				text : data.res_msg,
				confirmButtonColor: '#C0C0C0',
				confirmButtonText : "닫기"
		}).then((result)=>{
			location.href = "/admin/member/list";
		});
			}else if(data.res_code == '409'){
				Swal.fire({
				icon : 'warning',
				text : data.res_msg,
				confirmButtonColor: '#C0C0C0',
				confirmButtonText : "닫기"
			});	
			}else {
				Swal.fire({
				icon : 'error',
				text : data.res_msg,
				confirmButtonColor: '#C0C0C0',
				confirmButtonText : "닫기"
			});
			}
		}
	)})
	
