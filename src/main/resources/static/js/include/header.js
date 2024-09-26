// 초기 세션 시간 (초) 설정
let remainingTime = 1800;

function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(remainingSeconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
}

function updateSessionTime() {
    remainingTime--;
    const formattedTime = formatTime(remainingTime);
    document.getElementById('session-time').innerText = `남은 시간: ${formattedTime}`;

    if (remainingTime <= 0) {
        alert('세션이 만료되었습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/';
    }
}

window.onload = function() {
    fetch('/session-time')
        .then(response => response.json())
        .then(timeLeft => {
            remainingTime = timeLeft;
            setInterval(updateSessionTime, 1000);
        })
};

document.getElementById('userImage').addEventListener('click', function(event) {
   var dropdownMenu = document.getElementById('dropdownMenu');
   dropdownMenu.classList.toggle('show');
   event.stopPropagation();
});

document.addEventListener('click', function(event) {
   var dropdownMenu = document.getElementById('dropdownMenu');
    if (!event.target.closest('.user_image')) {
       dropdownMenu.classList.remove('show');
    }
});

//알림 모달
const modal = document.getElementById("notification-modal");
const closeButton = document.querySelector(".close-notification-modal");

function showNotification(message) {
    document.getElementById("notification-message").textContent = message;
    modal.style.display = "block";
    setTimeout(() => {
            modal.style.display = "none"; // 모달 닫기
        }, 5000);
}

closeButton.onclick = function() {
    modal.style.display = "none";
}

window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

// 웹소켓 선언 (공용)
// 전역 웹소켓 변수
let alarmSocket;

function connectWebSocket() {
    if (!alarmSocket || alarmSocket.readyState === WebSocket.CLOSED) {
        alarmSocket = new WebSocket(`ws://localhost:8080/websocket/notifications`);

        alarmSocket.onopen = function() {
            console.log("웹소켓이 연결되었습니다.");
        };

        alarmSocket.onclose = function(event) {
            console.log("웹소켓 연결이 해제되었습니다.", event);
            // 필요시 재연결 로직
        };

        alarmSocket.onerror = function(error) {
            console.error("에러 발생", error);
        };

        alarmSocket.onmessage = function(event) {
            const message = JSON.parse(event.data);
            if (message.type === 'chatAlarm') {
                // 알림 메시지를 처리하는 로직
                console.log(message);
                showNotification(message.data);
            }
        };


    }
}
if (!alarmSocket) {
    connectWebSocket();
}
//------ 수정불가 ------