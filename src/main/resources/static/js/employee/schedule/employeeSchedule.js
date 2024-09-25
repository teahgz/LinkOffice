document.addEventListener('DOMContentLoaded', function() { 
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    var calendarEl = document.getElementById('calendar');
    var calendar;
    var events = []; 
    
	var categoryColors = {};
	var categoryNames = {};
   
    var memberNo = document.getElementById('memberNo').value; 
	var userDepartmentNo = document.getElementById('departmentNo').value; 
	
	fetchDepartmentInfo();  
    
	// 사원별 선택 부서 체크박스
	function fetchDepartmentInfo() {
	    $.ajax({
	        url: '/api/schedule/department/list',
	        method: 'GET',
	        contentType: 'application/json',
	        headers: {
	            'X-CSRF-TOKEN': csrfToken
	        }
	    })
	    .done(function(departments) {
	        const departmentCheckboxesDiv = $('#departmentCheckboxes');
	        departmentCheckboxesDiv.empty();
	        
	        departments.forEach(function(department) {
	            const checkbox = $(`
	                <input type="checkbox" class="department-checkbox" id="dept_${department.department_no}" data-department-no="${department.department_no}">
	                <label for="dept_${department.department_no}">${department.department_name}</label>
	            `);
	            
	            departmentCheckboxesDiv.append(checkbox); 
	            
	            $(`#dept_${department.department_no}`).on('change', filterEvents);
	        });
	        
    		updateCheckboxes(memberNo);  
	    });
	}

	// 사원별 선택 부서 체크박스 상태 
	function updateCheckboxes(memberNo) {
	    $.ajax({
	        url: '/api/schedule/checks/' + memberNo,
	        method: 'GET',
	        dataType: 'json',
	        success: function (data) {
				const UserIncheckbox = $(`[data-department-no="${userDepartmentNo}"]`);  
                UserIncheckbox.prop('checked', true); 
                
	            data.forEach(function (scheduleChecks) {
	                const departmentNo = scheduleChecks.department_no; 
	                const checkbox = $(`[data-department-no="${departmentNo}"]`); 
	                  
	                if (checkbox.length) {   
	                    if (scheduleChecks.schedule_check_status === 0) {
	                        checkbox.prop('checked', true); 
	                    }
	                } 
	                  
	            });
	        }
	    });
	} 
	
	// 전사 체크 박스
    $('#all_company_schedule').on('change', function () {
       updateScheduleCheck(memberNo, 1, $(this).prop('checked'));
    });

    // 부서별 체크박스 클릭 이벤트  
    $('#departmentCheckboxes').on('change', '.department-checkbox', function () {
        const departmentNo = $(this).data('department-no'); 
        updateScheduleCheck(memberNo, departmentNo, $(this).prop('checked'));
    });
   
    function updateScheduleCheck(memberNo, departmentNo, isChecked) {
	   const scheduleCheckStatus = isChecked ? 0 : 1; 
	    
	   $.ajax({
	     url: '/api/schedule/checks/save',
	     method: 'POST',
	     contentType: 'application/json',
	     data: JSON.stringify({
	       member_no: memberNo,
	       department_no: departmentNo,
	       schedule_check_status: scheduleCheckStatus
	     }),
	     headers: {
            'X-CSRF-TOKEN': csrfToken
        },
	     dataType: 'json'
	   });
	}

	$.ajax({
        url: '/employee/categories',
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
        success: function(categories) {
            categories.forEach(function(category) {
                categoryColors[category.schedule_category_no] = '#' + category.schedule_category_color;
                categoryNames[category.schedule_category_no] = category.schedule_category_name;
            });
            fetchSchedules();
        }
    });
    
	
	function fetchSchedules(url, type) {
        return $.ajax({
            url: url,
            method: 'GET',
            dataType: 'json',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        }).then(function(schedules) {
            return processSchedules(schedules, type);
        });
    }
	 
	// 참여자 정보 
	var parMemberNos = [];

	function searchParticipate(scheduleNo, callback) {
	    $.ajax({
	        url: '/api/participate/member/schedules/' + scheduleNo,
	        method: 'GET',
	        dataType: 'json',
	        headers: {
	            'X-CSRF-TOKEN': csrfToken
	        },
	        success: function(data) {
	            parMemberNos = data.participants.map(participant => participant.member_no);
	            console.log(parMemberNos);
	            callback(parMemberNos);
	        },
	        error: function(error) {
	            console.error("Error fetching participants:", error);
	            callback([]);
	        }
	    });
	} 
        
	function fetchAllSchedules() {
	    const allSchedules = [];
	
	    // 개인 일정
	    return fetchSchedules('/api/personal/schedules/' + memberNo, 'personalResult')
	        .then(personalSchedules => { 
	            allSchedules.push(...personalSchedules);
	
	            // 부서 일정
	            return fetchSchedules('/api/department/schedules', 'departmentResult');
	        })
	        .then(departmentSchedules => { 
	            allSchedules.push(...departmentSchedules);
	
	            // 전사 일정
	            return fetchSchedules('/api/company/schedules', 'scheduleDtos');
	        })
	        .then(companySchedules => { 
	            allSchedules.push(...companySchedules);
	
	            // 참여자 일정
	            return fetchSchedules('/api/participate/schedules', 'participateResult');
	        })
	        .then(participateSchedules => { 
	            allSchedules.push(...participateSchedules);
	 
	            return allSchedules;
	        });
	}

	// 반복, 예외 확인
	function processSchedules(schedules, type) {
        return Promise.all([
            $.ajax({
                url: '/api/repeat/schedules',
                method: 'GET',
                dataType: 'json',
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                }
            }),
            $.ajax({
                url: '/api/company/exception/schedules',
                method: 'GET',
                dataType: 'json',
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                }
            })
        ]).then(function([repeats, exceptions]) {
            var processedEvents = [];

            schedules.forEach(function(schedule) {
                if (schedule.schedule_repeat === 0) {
                    processedEvents.push(createEvent(schedule, type));
                } else {
                    var repeatInfo = repeats.find(r => r.schedule_no === schedule.schedule_no);
                    if (repeatInfo) {
                        processedEvents.push(...createRepeatingEvents(schedule, repeatInfo, exceptions, type));
                    }
                }
            });

            return processedEvents;
        });
    }
    
    function createEvent(schedule, type, date) {
	    var eventStart = date || new Date(schedule.schedule_start_date);
	    var eventEnd = schedule.schedule_end_date ? new Date(schedule.schedule_end_date) : null;
	
	    var event = {
	        id: schedule.schedule_no,
	        title: schedule.schedule_title,
	        start: formatDate(eventStart) + (schedule.schedule_start_time ? 'T' + schedule.schedule_start_time : ''),
	        end: eventEnd ? formatDate(eventEnd) + (schedule.schedule_end_time ? 'T' + schedule.schedule_end_time : '') : null,
	        allDay: schedule.schedule_allday === 1,
	        backgroundColor: categoryColors[schedule.schedule_category_no] || '#3788d8',
	        borderColor: categoryColors[schedule.schedule_category_no] || '#3788d8',
	        textColor: '#000000',
	        className: type + '-event',
	        extendedProps: {
	            type: type,
	            categoryName: categoryNames[schedule.schedule_category_no],
	            comment: schedule.schedule_comment,
	            repeatOption: schedule.schedule_repeat,
	            createDate: schedule.schedule_create_date,
	            startDate: eventStart ? formatDate(eventStart) : null,
	            endDate: eventEnd ? formatDate(eventEnd) : null,
	            startTime: schedule.schedule_allday === 0 ? schedule.schedule_start_time : null,
	            endTime: schedule.schedule_allday === 0 ? schedule.schedule_end_time : null,
	            department_no: schedule.department_no,
	            member_no: schedule.member_no,
	            participant_no: schedule.member_no ? [] : []
	        }
	    };
	
	    if (type === 'participateResult') {
	        searchParticipate(schedule.schedule_no, function(participantNos) {
	            event.extendedProps.participant_no = participantNos;
	        });
	    }
	
	    return event;
	}
	
	// 반복, 예외 확인
	function processSchedules(schedules, type) {
	    return Promise.all([
	        $.ajax({
	            url: '/api/repeat/schedules',
	            method: 'GET',
	            dataType: 'json',
	            headers: {
	                'X-CSRF-TOKEN': csrfToken
	            }
	        }),
	        $.ajax({
	            url: '/api/company/exception/schedules',
	            method: 'GET',
	            dataType: 'json',
	            headers: {
	                'X-CSRF-TOKEN': csrfToken
	            }
	        })
	    ]).then(function([repeats, exceptions]) {
	        var processedEvents = [];
			
			// 참여자 일정 
	        schedules.forEach(function(schedule) {
	            if (schedule.schedule_repeat === 0) {
	                processedEvents.push(createEvent(schedule, type));
	            } else {
	                var repeatInfo = repeats.find(r => r.schedule_no === schedule.schedule_no);
	                if (repeatInfo) {
	                    processedEvents.push(...createRepeatingEvents(schedule, repeatInfo, exceptions, type));
	                }
	            }
	        }); 
	        var participateEvents = processedEvents.filter(event => event.extendedProps.type === 'participateResult');
	        var participatePromises = participateEvents.map(event => 
	            new Promise(resolve => {
	                searchParticipate(event.id, function(participantNos) {
	                    event.extendedProps.participant_no = participantNos;
	                    resolve();
	                });
	            })
	        );
	
	        return Promise.all(participatePromises).then(() => processedEvents);
	    });
	}
    
    function createRepeatingEvents(schedule, repeatInfo, exceptions, type) {
        var events = [];
        var startDate = new Date(schedule.schedule_start_date);
        var endDate = repeatInfo.schedule_repeat_end_date ? new Date(repeatInfo.schedule_repeat_end_date) : new Date(startDate.getFullYear() + 1, startDate.getMonth(), startDate.getDate());
        var currentDate = new Date(startDate);

        while (currentDate <= endDate) {
            var exceptionEvent = exceptions.find(e =>
                e.schedule_no === schedule.schedule_no &&
                e.schedule_exception_date === formatDate(currentDate)
            );

            if (exceptionEvent && exceptionEvent.schedule_exception_status === 0) {
                events.push(createExceptionEvent(exceptionEvent, currentDate, type));
            } else if (!exceptionEvent || exceptionEvent.schedule_exception_status !== 1) {
                events.push(createEvent(schedule, type, currentDate));
            }

            currentDate = calculateNextRepeatDate(currentDate, repeatInfo);
        }

        return events;
    }

	function createExceptionEvent(exceptionEvent, date, type) {
        return createEvent({
            ...exceptionEvent,
            schedule_start_date: formatDate(date),
            schedule_end_date: formatDate(date)
        }, type);
    }
 
    function calculateNextRepeatDate(currentDate, repeatInfo) {
	    switch (repeatInfo.schedule_repeat_type) {
	        case 1: // 매일
	            return new Date(currentDate.setDate(currentDate.getDate() + 1));
	        case 2: // 매주
	            return new Date(currentDate.setDate(currentDate.getDate() + 7));
	        case 3: // 매월 n일
	            var nextMonth = new Date(currentDate.setMonth(currentDate.getMonth() + 1));
	            nextMonth.setDate(repeatInfo.schedule_repeat_date);
	            return nextMonth;
	        case 4: // 매월 n번째 요일
	            var nextMonth = new Date(currentDate.setMonth(currentDate.getMonth() + 1));
	            while (nextMonth.getDay() !== repeatInfo.schedule_repeat_day || 
	                   Math.floor((nextMonth.getDate() - 1) / 7) + 1 !== repeatInfo.schedule_repeat_week) {
	                nextMonth.setDate(nextMonth.getDate() + 1);
	            }
	            return nextMonth;
	        case 5: // 매년
	            return new Date(currentDate.setFullYear(currentDate.getFullYear() + 1));
	        default:
	            return new Date(currentDate.setDate(currentDate.getDate() + 1));
	    }
	}

	function formatDate(date) {
	    return date.getFullYear() + '-' + 
	           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
	           String(date.getDate()).padStart(2, '0');
	}
 
    function initializeCalendar(events) {
        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay,listMonth'
            },
            customButtons: {
                yearButton: {
                    text: '년도',
                    click: showYearPicker
                },
                monthButton: {
                    text: '월',
                    click: showMonthPicker
                }
            },
            contentHeight: 'auto',
            handleWindowResize: true,
            fixedWeekCount: false,
            googleCalendarApiKey: 'AIzaSyBaQi-ZLyv7aiwEC6Ca3C19FE505Xq2Ytw',
            eventSources: [
                {
                    googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                    color: 'transparent',
                    textColor: 'red',
                    className: 'google-holiday',
                    allDay: true,
                    order: -1
                },
                {
                    events: events
                }
            ],
            eventOrder: '-order,-allDay,start',
            buttonText: {
                today: '오늘',
                month: '월',
                week: '주',
                day: '일'
            },
            dayCellContent: function(info) {
                var number = document.createElement("a");
                number.classList.add("fc-daygrid-day-number");
                number.innerHTML = info.dayNumberText.replace("일", '').replace("日", "");
                
                var wrapper = document.createElement("div");
                wrapper.classList.add("fc-daygrid-day-top");
                wrapper.appendChild(number);
                
                if (info.view.type === "dayGridMonth") {
                    return { domNodes: [wrapper] };
                }
                return { domNodes: [] };
            },
            dayMaxEvents: 3,
            eventDidMount: function(info) {
                const companyChecked = document.getElementById('all_company_schedule').checked;
                const eventDepartmentNo = info.event.extendedProps.department_no;
                const departmentCheckbox = document.getElementById(`dept_${eventDepartmentNo}`);

                if (info.event.extendedProps.type === 'personal' || info.event.extendedProps.type === 'participate') {
                    return true;
                } else if (info.event.extendedProps.type === 'company' && !companyChecked) {
                    info.el.style.display = 'none';
                    return false;
                } else if (info.event.extendedProps.type === 'department') {
                    if (!departmentCheckbox || !departmentCheckbox.checked) {
                        info.el.style.display = 'none';
                        return false;
                    }
                }
                return true;
            },
            eventClick: function(info) {
				console.log(info.event.extendedProps);
			}
        });
        filterEvents();
        calendar.render();
    }
 
    function filterEvents() {
        const companyChecked = document.getElementById('all_company_schedule').checked;
        
        const selectedDepartments = $('.department-checkbox:checked').map(function() {
            return $(this).data('department-no').toString(); 
        }).get();
         
        
        calendar.getEvents().forEach(function(event) {
            const eventDepartmentNo = event.extendedProps.department_no;
            const participantNos = event.extendedProps.participant_no || [];
            
            let shouldDisplay = false;  
				
            if (event.extendedProps.type === 'personalResult' && (event.extendedProps.member_no.toString() === memberNo)) {
                shouldDisplay = true;  
            } else if(event.extendedProps.type === 'participateResult') {
				shouldDisplay = participantNos.includes(parseInt(memberNo));  
			} else if (event.extendedProps.type === 'scheduleDtos') {
                shouldDisplay = companyChecked;  
            } else if (event.extendedProps.type === 'departmentResult' && (event.extendedProps.department_no.toString() === userDepartmentNo)) {
                shouldDisplay =  true;   
            } else if (event.extendedProps.type === 'departmentResult') {
                shouldDisplay = selectedDepartments.includes(eventDepartmentNo); 
            }

            event.setProp('display', shouldDisplay ? 'auto' : 'none');
        });
    }
 
 
    fetchAllSchedules().then(function(allEvents) {
        initializeCalendar(allEvents);
        fetchDepartmentInfo();
 
        $('#all_company_schedule').on('change', filterEvents);
    });
    
     function showYearPicker() {
	    const currentYear = calendar.getDate().getFullYear();
	    const yearPicker = document.createElement('select');
	    
	    for (let year = currentYear - 5; year <= currentYear + 5; year++) {
	        const option = document.createElement('option');
	        option.value = year;
	        option.text = year;
	        if (year === currentYear) option.selected = true;
	        yearPicker.appendChild(option);
	    }
	    
	    yearPicker.onchange = function() {
	        const selectedYear = parseInt(this.value);
	        const currentDate = calendar.getDate();
	        calendar.gotoDate(new Date(selectedYear, currentDate.getMonth(), 1));
	    };
	    
	    Swal.fire({
	        title: '년도 선택',
	        html: yearPicker,
	        showCancelButton: true,
	        cancelButtonColor: '#C0C0C0',
	        confirmButtonColor: '#B1C2DD',
	        confirmButtonText: '확인',
	        cancelButtonText: '취소'
	    });
	}
	
	function showMonthPicker() {
	    const currentDate = calendar.getDate();
	    const monthPicker = document.createElement('select');
	    const months = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
	    
	    months.forEach((month, index) => {
	        const option = document.createElement('option');
	        option.value = index;
	        option.text = month;
	        if (index === currentDate.getMonth()) option.selected = true;
	        monthPicker.appendChild(option);
	    });
	    
	    monthPicker.onchange = function() {
	        const selectedMonth = parseInt(this.value);
	        calendar.gotoDate(new Date(currentDate.getFullYear(), selectedMonth, 1));
	    };
	    
	    Swal.fire({
	        title: '월 선택',
	        html: monthPicker,
	        confirmButtonColor: '#B1C2DD',
	        cancelButtonColor: '#C0C0C0',
	        showCancelButton: true,
	        confirmButtonText: '확인',
	        cancelButtonText: '취소'
	    });
	}
});