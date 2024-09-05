document.addEventListener('DOMContentLoaded', function () {
    const modifyBtn = document.getElementById('modifyBtn');
    const saveBtn = document.getElementById('saveBtn');
    const clearBtn = document.getElementById('clearBtn');
    const digitalBox = document.querySelector('.digital_box');
    const canvas = document.getElementById('canvas');
    const ctx = canvas ? canvas.getContext('2d') : null;
    const currentSignature = document.getElementById('currentSignature');
    let painting = false;
	// 수정 버튼
    if (modifyBtn) {
        modifyBtn.addEventListener('click', function () {
            modifyBtn.style.display = 'none';
            if (saveBtn) saveBtn.style.display = 'inline-block';
            if (clearBtn) clearBtn.style.display = 'inline-block'; 
            if (currentSignature) currentSignature.style.display = 'none';
            if (canvas) {
                canvas.style.display = 'block';
                setupCanvas();
            }
        });
    }

    if (canvas) {
        if (currentSignature) {
            canvas.style.display = 'none';
        } else {
            digitalBox.style.display = 'block';
            canvas.style.display = 'block';
            setupCanvas();
        }
    }

    function setupCanvas() {
        if (!ctx) return;
        ctx.lineWidth = 3;
        canvas.addEventListener('mousemove', draw);
        canvas.addEventListener('mousedown', startPainting);
        canvas.addEventListener('mouseup', stopPainting);
        canvas.addEventListener('mouseleave', stopPainting);
    }

    function startPainting() {
        painting = true;
    }

    function stopPainting() {
        painting = false;
        ctx.beginPath();
    }

    function draw(e) {
        if (!painting) return;
        ctx.lineTo(e.offsetX, e.offsetY);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(e.offsetX, e.offsetY);
    }
	// 지우기 버튼
    if (clearBtn) {
        clearBtn.addEventListener('click', function (e) {
            e.preventDefault();
            clearCanvas();
        });
    }

    function clearCanvas() {
        if (ctx) {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        }
    }

    if (saveBtn) {
        if (currentSignature) {
            saveBtn.style.display = 'none';
            clearBtn.style.display = 'none';
        } else {
            saveBtn.style.display = 'inline-block';
        }
    }
    
// 전자서명 등록
    const form = document.getElementById('digitalFrm');
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const payload = new FormData(form);
        const memberNo = form.member_no.value;
        const csrfToken = document.getElementById('csrf_token').value;
        const signatureDataUrl = canvas.toDataURL('image/png');

        payload.append('signatureData', signatureDataUrl);

        fetch('/employee/member/digitalname/' + memberNo, {
            method: 'post',
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
                    title: '서명 등록',
                    text: data.res_msg,
                    confirmButtonText: '닫기'
                }).then(() => {
                    location.href = "/employee/member/digitalname/" + memberNo;
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '서명 등록',
                    text: data.res_msg,
                    confirmButtonText: '닫기'
                });
            }
        });
    });
});
