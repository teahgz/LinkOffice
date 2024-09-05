document.addEventListener("DOMContentLoaded", function() {
    // work_date와 근태를 담을 배열
    var attendanceDates = [];
    
    // 모든 attendanceList의 work_date의 값을 가져옴
    var attendanceCells = document.querySelectorAll("#attendanceTable .attendance_date");
    
    // 날짜와 근태를 배열에 추가합니다.
    attendanceCells.forEach(function(cell) {
        var workDate = cell.textContent.trim();
        var checkInTime = cell.nextElementSibling ? cell.nextElementSibling.textContent.trim() : '';
        
        // 근태 값을 넣어줄 변수
        var status = '';

        // 출근 시간 존재 여부 및 출근 지각 결근 판단
        // 출근 시간 없음 = 결근 
        if (checkInTime === '') {
            status = 'bar_absent'; 
        } else {
			// 출근 시간이 존재하면 시간과 분으로 나눔 
            var checkInHour = parseInt(checkInTime.split(':')[0]);
            // 출근 시간이 9시보다 늦으면 지각 
            if (checkInHour > 9) {
                status = 'bar_late'; 
            // 출근 시간 존재&9시 이전이면 출근 
            } else {
                status = 'bar_normal'; 
            }
        }
		
		// 배열에 workDate와 근태를 담아줌 
        if (workDate) {
            attendanceDates.push({ date: workDate, statusClass: status });
        }
    });
    
	// 오늘 날짜 
    var today = new Date();
    // attendanceCalendar function에 오늘 날짜와 attendanceDates 배열 보내기 
    attendanceCalendar(today, attendanceDates);
	
	// 이전 달
    function prevMonth() {
		// 올해, 이번 달-1, 1일 
        today = new Date(today.getFullYear(), today.getMonth() - 1, 1);
        attendanceCalendar(today, attendanceDates);
    }
	
	// 다음 달 
    function nextMonth() {
		// 올해, 이번 달+1, 1일 
        today = new Date(today.getFullYear(), today.getMonth() + 1, 1);
        attendanceCalendar(today, attendanceDates);
    }
	// 이전 달, 다음 달 보내기 
    window.prevMonth = prevMonth; 
    window.nextMonth = nextMonth; 
});

// 달력 
function attendanceCalendar(today, attendanceDates) {
    var row = null;
    var cnt = 0;
    // 달력(월~일) table 
    var calendarTable = document.getElementById("calendar");
    var calendarTableTitle = document.getElementById("year_and_month");

    // 올해, 이번달  
    var currentYear = today.getFullYear();
    var currentMonth = today.getMonth();
    // 월을 1 기반으로 표시
    var currentMonthDisplay = currentMonth + 1; 
    // 0000년 00월 형식으로 출력 
    calendarTableTitle.innerHTML = currentYear + "년 " + (currentMonthDisplay < 10 ? '0' + currentMonthDisplay : currentMonthDisplay) + "월";

    // 달력의 첫날과 마지막 날, 이전 달과 다음 달의 날짜 계산
    var firstDate = new Date(currentYear, currentMonth, 1);
    var lastDate = new Date(currentYear, currentMonth + 1, 0);
    var prevMonthLastDate = new Date(currentYear, currentMonth, 0);

    // 기존 행 삭제
    while (calendarTable.rows.length > 2) {
        calendarTable.deleteRow(calendarTable.rows.length - 1);
    }

    // 첫 주에 전 달의 마지막 주 날짜 추가
    row = calendarTable.insertRow();
    // 이번 달의 첫 날이 시작되는 요일
    var daysFromPrevMonth = firstDate.getDay(); 
    var prevMonthStartDate = prevMonthLastDate.getDate() - daysFromPrevMonth + 1;

    // 전 달의 날짜를 추가 
    for (var i = prevMonthStartDate; i <= prevMonthLastDate.getDate(); i++) {
        var cell = row.insertCell();
        var dateToCheck = `${currentYear}-${String(currentMonth).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        var dateBarHtml = '';

        // attendanceDates 배열에서 해당 날짜가 있는지 확인
        var dateEntry = attendanceDates.find(entry => entry.date === dateToCheck);
        // 날짜가 존재한다면 근태를 표시할 수 있는 bar와 근태별 class(색상 표현을 위한) 추가 
        if (dateEntry) {
            dateBarHtml = `<div class="date_bar ${dateEntry.statusClass}"></div>`; 
        }
		
        cell.innerHTML = `<div class="date_container">
                            <span style="color: #d3d3d3;">${i}</span>
                            ${dateBarHtml}
                          </div>`;
        cell.align = "center"; 
        cnt += 1;
    }

    // 이번 달 날짜 추가
    for (var i = 1; i <= lastDate.getDate(); i++) {
		// 일요일부터 월요일까지 한 줄에 출력했다면 다음 줄 추가 
        if (cnt % 7 == 0) { 
            row = calendarTable.insertRow();
        }

        var cell = row.insertCell();
        cnt += 1;

        var dateToCheck = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        var dateBarHtml = '';

        // attendanceDates 배열에서 해당 날짜가 있는지 확인
        var dateEntry = attendanceDates.find(entry => entry.date === dateToCheck);
        // 날짜가 존재한다면 근태를 표시할 수 있는 bar와 근태별 class(색상 표현을 위한) 추가 
        if (dateEntry) {
            dateBarHtml = `<div class="date_bar ${dateEntry.statusClass}"></div>`; 
        }

        cell.innerHTML = `<div class="date_container">
                            <span>${i}</span>
                            ${dateBarHtml}
                          </div>`;
        cell.align = "center"; 

        cell.setAttribute('id', i);

        // 일요일은 빨간색, 토요일은 파란색으로 표시
        if (cnt % 7 == 1) {
            cell.querySelector('span').style.color = '#F79DC2'; 
        } else if (cnt % 7 == 0) {
            cell.querySelector('span').style.color = 'skyblue'; 
        }
    }

    // 마지막 주에 다음 달의 날짜 추가
    if (cnt % 7 != 0) {
        var nextMonthDaysToShow = 7 - (cnt % 7);
        for (var i = 1; i <= nextMonthDaysToShow; i++) {
            var cell = row.insertCell();
            cell.innerHTML = `<div class="date_container">
                                <span style="color: #d3d3d3;">${i}</span>
                              </div>`; 
            cell.align = "center"; 
        }
    }
}
