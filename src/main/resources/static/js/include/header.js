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
        .catch(error => console.error('세션 시간 에러', error));
};