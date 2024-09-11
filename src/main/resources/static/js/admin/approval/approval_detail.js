<<<<<<< Updated upstream
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
                        title: '성공',
                        text: data.res_msg,
                        confirmButtonText: "닫기"
                    }).then((result) => {
                        location.href = "/admin/approval/form";
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '실패',
                        text: data.res_msg,
                        confirmButtonText: "닫기"
                    });
                }
            });
        }
=======
document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelectorAll('.table_container input, .table_container textarea, .table_container select').forEach(element => {
        element.disabled = true;
>>>>>>> Stashed changes
    });
});
