const form = document.getElementById("appDeleteFrm");
form.addEventListener('submit', (e) => {
    e.preventDefault();
    const formNo = form.form_no.value;
    const csrfToken = document.getElementById("csrf_token").value;

    Swal.fire({
        text: '삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#B1C2DD',
        cancelButtonColor: '#C0C0C0',
        confirmButtonText: '확인',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {  
            const payload = new FormData(form);

            fetch('/admin/approval/delete/' + formNo, {
                method: 'put',
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                body: payload
            })
            .then(response => response.json())
            .then(data => {
                if (data.res_code == '200') {
                    Swal.fire({
                        icon: 'success',
                        text: data.res_msg,
                        confirmButtonColor: '#C0C0C0',
                        confirmButtonText: "닫기"
                    }).then((result) => {
                        location.href = "/admin/approval/form";
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        text: data.res_msg,
                        confirmButtonColor: '#C0C0C0',
                        confirmButtonText: "닫기"
                    });
                }
            });
        }
    });
});
