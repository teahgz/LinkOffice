// 캔버스
 const canvas = document.getElementById('canvas');
        const ctx = canvas.getContext('2d');
        let painting = false;

        function setupCanvas() {
            canvas.style.border = '3px double';
            canvas.style.cursor = 'pointer';
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

        function clearCanvas() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        }


        // 이벤트 리스너 설정
        document.addEventListener('DOMContentLoaded', function() {
            const clearBtn = document.getElementById('clearBtn');

            if (canvas) {
                setupCanvas();
            }


            if (clearBtn) {
                clearBtn.addEventListener('click', clearCanvas);
            }
        });