// 시계 
function updateClock() {
	var now = new Date();
    let hours = now.getHours();
    let minutes = now.getMinutes();
    let seconds = now.getSeconds();
    	
    if(hours < 10){
		hours = '0' + hours;
	}
	if(minutes < 10){
		minutes = '0' + minutes;
	}
	if(seconds < 10){
		seconds = '0' + seconds;
	}
    var clock = hours + ':' + minutes + ':' + seconds;
    document.getElementById('clock').textContent = clock;
}

// 매 초마다 업데이트 
setInterval(updateClock, 1000);

// 페이지 로드 시에도 시계 업데이트
document.addEventListener('DOMContentLoaded', function() {
	updateClock();
});

// 출퇴근 기능 
document.addEventListener('DOMContentLoaded', function() {
    // memberNo 값 가져오기 
    var memberNo = document.getElementById('attendance_memberNo').value;
	
	// 출퇴근 버튼 상태 
	var checkInButton = document.getElementById('check_in_button');
    var checkOutButton = document.getElementById('check_out_button');
	
    // 출근버튼이 활성화된 상태라면 출근 기능 실행 
    if(!checkInButton.disabled){		
	    document.getElementById('check_in_button').addEventListener('click', function() {
			// submit이 자동으로 되지 않게 해줌 
			event.preventDefault();
			
            var csrfToken = document.querySelector('input[name="_csrf"]').value;
            var url = '/attendance/checkIn';
            var jsonData = JSON.stringify({ memberNo: memberNo });

            // 버튼 비활성화 및 스타일 업데이트
            checkInButton.disabled = true;

            // AJAX 요청 보내기
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: jsonData
            })
            .then(response => response.json())
            .then(data => {
                if (data.res_code === '200') {
                    Swal.fire({
                        icon: 'success',
                        title: '출석 확인',
                        text: data.res_msg,
                        confirmButtonText: '확인'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '오류',
                        text: data.res_msg,
                        confirmButtonText: '확인'
                    });
                }
            });
	    });
	}
   	// 퇴근 기능 
    document.getElementById('check_out_button').addEventListener('click', function() {
		// submit이 자동으로 되지 않게 해줌 
		event.preventDefault();
		
        var csrfToken = document.querySelector('input[name="_csrf"]').value;
        var url = '/attendance/checkOut';
        var jsonData = JSON.stringify({ memberNo: memberNo });

        // AJAX 요청 보내기
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': csrfToken
            },
            body: jsonData
        })
        .then(response => response.json())
        .then(data => {
            if (data.res_code === '200') {
                Swal.fire({
                    icon: 'success',
                    title: '퇴근 확인',
                    text: data.res_msg,
                    confirmButtonText: '확인'
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: data.res_msg,
                    confirmButtonText: '확인'
                });
            }
        });
    });
});