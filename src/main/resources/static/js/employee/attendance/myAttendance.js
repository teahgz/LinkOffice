document.addEventListener("DOMContentLoaded", function() {
    // work_date와 근태를 담을 배열
    var attendanceDates = [];
    
    // 모든 attendanceList의 work_date의 값을 가져옴
    var attendanceList = document.querySelectorAll(".hidden_table .hidden_date");
    
    // 날짜와 근태를 배열에 추가합니다.
    attendanceList.forEach(function(cell) {
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
            if (checkInHour >= 9) {
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
    // 공휴일 데이터와 함께 달력을 업데이트하는 함수 호출
    fetchAndRenderHolidays(today, attendanceDates);
    
    // 이전 달
    function prevMonth() {
        // 올해, 이번 달-1, 1일 
        today = new Date(today.getFullYear(), today.getMonth() - 1, 1);
        fetchAndRenderHolidays(today, attendanceDates);
    }
    
    // 다음 달 
    function nextMonth() {
        // 올해, 이번 달+1, 1일 
        today = new Date(today.getFullYear(), today.getMonth() + 1, 1);
        fetchAndRenderHolidays(today, attendanceDates);
    }
    // 이전 달, 다음 달 보내기 
    window.prevMonth = prevMonth; 
    window.nextMonth = nextMonth; 
});

// 공휴일 
function fetchAndRenderHolidays(today, attendanceDates) {
    // 연도와 월 추출
    var year = today.getFullYear();
    var month = String(today.getMonth() + 1).padStart(2, '0'); // 1-based month, pad with zero
    
    // API 호출
    fetch(`/holidays?year=${year}&month=${month}`)
        .then(response => response.json())
        .then(data => {
            var items = data.response.body.items;
            var holidays = [];
            
            if (items) {
                if (Array.isArray(items.item)) {
                    holidays = items.item;
                } else {
                    holidays = [items.item];
                }
            }
            
            // 공휴일 데이터를 달력 function의 매개변수로 보내기 
            attendanceCalendar(today, attendanceDates, holidays);
        })
}

// 달력 
function attendanceCalendar(today, attendanceDates, holidays) {
    var row = null;
    var cnt = 0;
    var calendarTable = document.getElementById("calendar");
    var calendarTableTitle = document.getElementById("year_and_month");

    var currentYear = today.getFullYear();
    var currentMonth = today.getMonth();
    var currentMonthDisplay = currentMonth + 1; 
    calendarTableTitle.innerHTML = `${currentYear}년 ${currentMonthDisplay < 10 ? '0' + currentMonthDisplay : currentMonthDisplay}월`;

    var firstDate = new Date(currentYear, currentMonth, 1);
    var lastDate = new Date(currentYear, currentMonth + 1, 0);
    var prevMonthLastDate = new Date(currentYear, currentMonth, 0);

    // 기존 행 삭제
    while (calendarTable.rows.length > 2) {
        calendarTable.deleteRow(calendarTable.rows.length - 1);
    }

    // 첫 주에 전 달의 마지막 주 날짜 추가
    row = calendarTable.insertRow();
    var daysFromPrevMonth = firstDate.getDay(); 
    var prevMonthStartDate = prevMonthLastDate.getDate() - daysFromPrevMonth + 1;

    // 전 달의 날짜를 추가
    for (var i = prevMonthStartDate; i <= prevMonthLastDate.getDate(); i++) {
        var cell = row.insertCell();
        var dateToCheck = `${currentYear}-${String(currentMonth).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        var dateBar = '';

        // attendanceDates 배열에서 해당 날짜가 있는지 확인
        var dateEntry = attendanceDates.find(entry => entry.date === dateToCheck);
        if (dateEntry) {
            dateBar = `<div class="date_bar ${dateEntry.statusClass}"></div>`;
        }

        var formattedDateToCheck = `${currentYear}${String(currentMonth + 1).padStart(2, '0')}${String(i).padStart(2, '0')}`;

        // 전 달 날짜는 회색
        var textColor = '#d3d3d3';

        cell.innerHTML = `<div class="date_container">
                            <span style="color: ${textColor};">${i}</span>
                            ${dateBar}
                          </div>`;
        cell.align = "center"; 
        cnt += 1;
    }

    // 이번 달 날짜 추가
    for (var i = 1; i <= lastDate.getDate(); i++) {
        if (cnt % 7 == 0) { 
            row = calendarTable.insertRow();
        }

        var cell = row.insertCell();
        cnt += 1;

        var dateToCheck = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        var dateBar = '';

        // attendanceDates 배열에서 해당 날짜가 있는지 확인
        var dateEntry = attendanceDates.find(entry => entry.date === dateToCheck);
        if (dateEntry) {
            dateBar = `<div class="date_bar ${dateEntry.statusClass}"></div>`; 
        }

        var formattedDateToCheck = `${currentYear}${String(currentMonth + 1).padStart(2, '0')}${String(i).padStart(2, '0')}`;
        
        // 공휴일 확인
        var holidayItem = holidays.find(item => item.locdate.toString() === formattedDateToCheck);
        var holidayName = holidayItem ? holidayItem.dateName : '';
        var isHoliday = holidayItem ? true : false;

        // 주말 여부 확인
        var isWeekend = (new Date(currentYear, currentMonth, i).getDay() === 0 || new Date(currentYear, currentMonth, i).getDay() === 6);
        
        // 날짜 색상 설정
        var textColor = isHoliday ? 'red' : (isWeekend ? (new Date(currentYear, currentMonth, i).getDay() === 0 ? '#F79DC2' : 'skyblue') : 'black');

        cell.innerHTML = `<div class="date_container">
                            <span style="color: ${textColor};">${i}</span>
                            ${holidayName ? `<div class="holiday_name">${holidayName}</div>` : ''}
                            ${dateBar}
                          </div>`;
        cell.align = "center"; 

        cell.setAttribute('id', i);
    }

    // 마지막 주에 다음 달의 날짜 추가
    if (cnt % 7 != 0) {
        var nextMonthDays = 7 - (cnt % 7);
        for (var i = 1; i <= nextMonthDays; i++) {
            var cell = row.insertCell();
            cell.innerHTML = `<div class="date_container">
                                <span style="color: #d3d3d3;">${i}</span>
                              </div>`; 
            cell.align = "center"; 
        }
    }
}

// 근태 조회 리스트 
document.addEventListener('DOMContentLoaded', function() {
	// 시작 날, 끝나는 날, 근태, 정렬 값과 리스트를 추가할 테이블, 페이징을 추가할 div 
    const startDateInput = document.getElementById('start_date');
    const endDateInput = document.getElementById('end_date');
    const attendanceStateSelect = document.getElementById('attendance_state');
    const sortSelect = document.getElementById('sort_select');
    const attendanceTable = document.getElementById('attendance_table_body');
    const paginationDiv = document.getElementById('pagination');
	
    // 날짜를 'yyyy-MM-dd' 형식의 String으로 변환하는 함수
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
    // 시작 날짜를 끝나는 날보다 나중 날짜로 설정 못하게 하는 함수 
    function startDateLimit() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);

        if (endDate < startDate) {
            endDateInput.value = formatDate(startDate);
        }
        startDateInput.max = formatDate(endDate);
    }
	// 오늘 날짜와 이번 달의 1일을 포맷 
    const today = new Date();
    const todayStr = formatDate(today);
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const firstDayStr = formatDate(firstDayOfMonth);
	
	// startDate와 endDate의 기본 값을 이번 달의 1일과 오늘로 설정 
    startDateInput.value = firstDayStr;
    endDateInput.value = todayStr;
	
	// startDate와 endDate를 오늘 이후의 날짜를 설정할 수 없게 설정 
    startDateInput.max = todayStr;
    endDateInput.max = todayStr;
	
	// memberNo의 값 
    const memberNo = document.getElementById('mem_no').textContent;
    // 전체 데이터를 저장할 배열 
    let allAttendanceData = []; 
    // 총 페이지 수
    let totalPages = 1; 
    // 현재 페이지
    let currentPage = 0; 
	
	// 출석 리스트 가져오기 
    function loadAttendanceList(page = 0) {
        currentPage = page; 
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        const attendanceState = attendanceStateSelect.value;
        const sortOrder = sortSelect.value === '최신 순' ? 'DESC' : 'ASC';

        fetch(`/employee/attendance/myAttendance?member_no=${memberNo}&start_date=${formatDate(startDate)}&end_date=${formatDate(endDate)}`)
            .then(response => response.json())
            .then(data => {
                allAttendanceData = data;
                filterAndPaginateData(page, attendanceState, sortOrder);
            })
    }

    function filterAndPaginateData(page, attendanceState, sortOrder) {
        // 필터링
        const filteredData = allAttendanceData.filter(attendance => {
            const checkInTime = attendance.check_in_time || '';
            let checkInHour = 0;
			
			// checkInTime의 시간만 가져오기 
            if (checkInTime) {
                const timeParts = checkInTime.split(':');
                checkInHour = parseInt(timeParts[0], 10);
            }
			
			// checkInTime의 시간으로 근태 파악 
            switch (attendanceState) {
                case '출근':
                    return checkInTime && checkInHour < 9;
                case '지각':
                    return checkInTime && checkInHour >= 9;
                case '결근':
                    return !checkInTime;
                case '전체':
                default:
                    return true;
            }
        }).sort((first, end) => {
            return sortOrder === 'ASC' 
                ? new Date(first.work_date) - new Date(end.work_date) 
                : new Date(end.work_date) - new Date(first.work_date);
        });

        // 페이지네이션
        // 한 페이지에 10줄
        const pageSize = 10;
        totalPages = Math.ceil(filteredData.length / pageSize); 
        const paginatedData = filteredData.slice(page * pageSize, (page + 1) * pageSize);

        // 테이블에 리스트 추가 
        attendanceTable.innerHTML = '';
        paginatedData.forEach(attendance => {
            const row = document.createElement('tr');
            const checkInTime = attendance.check_in_time || '';
            const checkOutTime = attendance.check_out_time || '';
            const status = checkInTime
                ? (parseInt(checkInTime.split(':')[0], 10) >= 9 ? '지각' : '출근')
                : '결근';

            row.innerHTML = `
                <td>${attendance.work_date}</td>
                <td>${checkInTime}</td>
                <td>${checkOutTime}</td>
                <td>${status}</td>
            `;
            attendanceTable.appendChild(row);
        });

        // 한 페이지에 행을 10개씩 맞추기 위한 빈 행 추가 
        const emptyRows = pageSize - paginatedData.length;
        for (let i = 0; i < emptyRows; i++) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            `;
            attendanceTable.appendChild(emptyRow);
        }
		// 페이지 개수 업데이트 
        updatePagination(page);
    }

    function updatePagination(currentPage) {
        paginationDiv.innerHTML = '';

        // 처음 페이지 버튼 (<<)
        const firstButton = document.createElement('span');
        firstButton.className = 'go_first_page_button';
        firstButton.textContent = '<<';
        firstButton.onclick = () => {
            if (currentPage > 0) {
                loadAttendanceList(0); 
            }
        };
        paginationDiv.appendChild(firstButton);
        
        // 이전 페이지 버튼 (<)
        const prevButton = document.createElement('span');
        prevButton.className = 'pagination_button';
        prevButton.textContent = '<';
        prevButton.onclick = () => {
            if (currentPage > 0) {
                loadAttendanceList(currentPage - 1);
            }
        };
        paginationDiv.appendChild(prevButton);

        // 페이지 번호 버튼
        for (let page = 0; page < totalPages; page++) {
            const pageButton = document.createElement('span');
            pageButton.className = `pagination_button ${page === currentPage ? 'active' : ''}`;
            pageButton.textContent = page + 1;
            pageButton.onclick = () => loadAttendanceList(page);
            paginationDiv.appendChild(pageButton);
        }

        // 다음 페이지 버튼 (>)
        const nextButton = document.createElement('span');
        nextButton.className = 'pagination_button';
        nextButton.textContent = '>';
        nextButton.onclick = () => {
            if (currentPage < totalPages - 1) {
                loadAttendanceList(currentPage + 1);
            }
        };
        paginationDiv.appendChild(nextButton);
        
        // 마지막 페이지 버튼 (>>)
        const lastButton = document.createElement('span');
        lastButton.className = 'go_last_page_button';
        lastButton.textContent = '>>';
        lastButton.onclick = () => {
            if (currentPage < totalPages - 1) {
                loadAttendanceList(totalPages - 1); 
            }
        };
        paginationDiv.appendChild(lastButton);
    }
	// 시작 날짜를 선택하면 동작하는 함수 
    startDateInput.addEventListener('change', () => {
        startDateLimit();
        loadAttendanceList();
    });
    // 끝나는 날짜를 선택하면 동작하는 함수 
    endDateInput.addEventListener('change', () => {
        startDateLimit();
        loadAttendanceList();
    });
    // 근태와 정렬을 선택하면 동작하는 함수 
    attendanceStateSelect.addEventListener('change', () => loadAttendanceList());
    sortSelect.addEventListener('change', () => loadAttendanceList());
	// 출석 리스트 가져오기 
    loadAttendanceList(); 
});
