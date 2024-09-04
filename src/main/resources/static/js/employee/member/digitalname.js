// 이벤트 발생
document.addEventListener('DOMContentLoaded', function () {
    const modifyBtn = document.getElementById('modifyBtn');
    const saveBtn = document.getElementById('saveBtn');
    const clearBtn = document.getElementById('clearBtn');
    const signatureBox = document.querySelector('.signature_box');
    const canvas = document.getElementById('canvas');
    const ctx = canvas ? canvas.getContext('2d') : null;
    let painting = false;

    if (modifyBtn) {
        modifyBtn.addEventListener('click', function () {
            modifyBtn.style.display = 'none';
            if (saveBtn) saveBtn.style.display = 'inline-block';
            signatureBox.style.display = 'block';
            const currentSignature = document.getElementById('currentSignature');
            if (currentSignature) {
                currentSignature.style.display = 'none';
            }
            if (canvas) {
                canvas.style.display = 'block';
                setupCanvas();
            }
        });
    } else {
        if (canvas) {
            signatureBox.style.display = 'block';
            canvas.style.display = 'block';
            setupCanvas();
        }
    }
	// 캔버스
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
	// 지우기
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
});
