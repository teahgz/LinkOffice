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
updateClock();