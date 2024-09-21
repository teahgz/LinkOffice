// 모달 창 기안 취소

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("myModal");
    const closeModal = document.querySelector(".close");

    document.querySelector('.cancel_button').addEventListener('click', function() {
        modal.style.display = "flex";
    });

    closeModal.addEventListener('click', function() {
        modal.style.display = "none";
    });

    document.getElementById('confirm_cancel_button').addEventListener('click', function() {
        const cancelReason = document.getElementById('cancel_reason').value;
        const aapNo = document.querySelector('#approval_no').value;
        const csrfToken = document.getElementById("csrf_token").value;

        if (!cancelReason) {
            Swal.fire({
                icon: 'warning',
                text: '취소 사유를 입력해주세요.',
                confirmButtonColor: '#B1C2DD',
                confirmButtonText: "확인"
            });
            return;
        }

        fetch('/employee/approval/cancel/' + aapNo, {
            method: 'put',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                approval_cancel_reason: cancelReason
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.res_code == '200') {
                Swal.fire({
                    icon: 'success',
                    text: data.res_msg,
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: "확인"
                }).then(() => {
                    location.href = "/employee/approval/approval_progress_detail/"+aapNo;
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
    });
});
