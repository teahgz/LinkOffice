document.addEventListener('DOMContentLoaded', function() { 
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    var calendarEl = document.getElementById('calendar');
    var calendar;
    
    var categoryColors = {};
    var categoryNames = {};
   
    var memberNo = document.getElementById('memberNo').value; 
    var userDepartmentNo = document.getElementById('departmentNo').value; 
    
    fetchDepartmentInfo();  
    
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
   	 
    $('#all_company_schedule').on('change', function () {
       updateScheduleCheck(memberNo, 1, $(this).prop('checked'));
    });

    $('#departmentCheckboxes').on('change', '.department-checkbox', function () {
        const departmentNo = $(this).data('department-no'); 
        updateScheduleCheck(memberNo, departmentNo, $(this).prop('checked'));
    });
   
    function updateScheduleCheck(memberNo, departmentNo, isChecked) {
       const scheduleCheckStatus = isChecked ? 0 : 1; 
       filterEvents();
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
            fetchAllSchedules();
        }
    });
	
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
	            callback(parMemberNos);
	        },
	        error: function(error) {
	            console.error("Error fetching participants:", error);
	            callback([]);
	        }
	    });
	} 
	
    function fetchAllSchedules() {
        var allEvents = [];  

        Promise.all([
            fetchSchedules('/api/personal/schedules/' + memberNo, 'personalResult'),
            fetchSchedules('/api/department/schedules', 'departmentResult'),
            fetchSchedules('/api/company/schedules', 'scheduleDtos'),
            fetchSchedules('/api/participate/schedules', 'participateResult'),
            $.ajax({
                url: '/api/repeat/schedules',
                method: 'GET',
                headers: { 'X-CSRF-TOKEN': csrfToken }
            }),
            $.ajax({
                url: '/api/company/exception/schedules',
                method: 'GET',
                headers: { 'X-CSRF-TOKEN': csrfToken }
            })
        ]).then(function([personalSchedules, departmentSchedules, companySchedules, participateSchedules, repeats, exceptions]) {
            processSchedules(personalSchedules.personalResult, 'personalResult', repeats, exceptions, allEvents);
            processSchedules(departmentSchedules.departmentResult, 'departmentResult', repeats, exceptions, allEvents);
            processSchedules(companySchedules.scheduleDtos, 'scheduleDtos', repeats, exceptions, allEvents);
            processSchedules(participateSchedules.participateResult, 'participateResult', repeats, exceptions, allEvents);

            initializeCalendar(allEvents); 
            console.log(allEvents.participateSchedules);
            filterEvents();
        });
    }

    function fetchSchedules(url, type) {
        return $.ajax({
            url: url,
            method: 'GET',
            headers: { 'X-CSRF-TOKEN': csrfToken }
        }).then(function(response) {
            var result = {};
            result[type] = response;
            return result;
        });
    }

    function processSchedules(schedules, type, repeats, exceptions, allEvents) { 
        if (!Array.isArray(schedules)) { 
            return;
        }

        schedules.forEach(function(schedule) {
            if (schedule.schedule_repeat === 0) { 
                allEvents.push(createEvent(schedule, type));
            } else {
                var repeatInfo = repeats.find(r => r.schedule_no === schedule.schedule_no);
                if (repeatInfo) {
                    var startDate = new Date(schedule.schedule_start_date);
                    var endDate = repeatInfo.schedule_repeat_end_date ? new Date(repeatInfo.schedule_repeat_end_date) : new Date(startDate.getFullYear() + 1, startDate.getMonth(), startDate.getDate());
                    var currentDate = new Date(startDate);
                    
                    while (currentDate <= endDate) {
                        var exceptionEvent = exceptions.find(e => 
                            e.schedule_no === schedule.schedule_no && 
                            e.schedule_exception_date === formatDate(currentDate)
                        );

                        if (exceptionEvent && exceptionEvent.schedule_exception_status === 0) {
                            allEvents.push(createExceptionEvent(exceptionEvent, currentDate, type));

                        } else if (exceptionEvent && exceptionEvent.schedule_exception_status === 1) {
                           
                        } else {  
                            allEvents.push(createEvent(schedule, type, currentDate));
                        }
                         
                        currentDate = calculateNextRepeatDate(currentDate, repeatInfo);
                    }
                }
            }
        });  
    }

    function createEvent(schedule, type, date) {
        var eventStart = date || new Date(schedule.schedule_start_date);
        var eventEnd = schedule.schedule_end_date ? new Date(schedule.schedule_end_date) : null;
    
        if (date) { 
            eventEnd = new Date(date);
            eventEnd.setHours(new Date(schedule.schedule_end_date).getHours());
            eventEnd.setMinutes(new Date(schedule.schedule_end_date).getMinutes());
        }  
        
        if (schedule.schedule_allday === 1 && eventEnd) {
	        eventEnd.setDate(eventEnd.getDate() + 1);
	    }
    
        var event = {
            order: 1,
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
                participant_no: [],
                participantsLoaded: false,
                isException: schedule.isException || false
            }
        };
    
        if (type === 'participateResult') {
            searchParticipate(schedule.schedule_no, function(participantNos) {
                event.extendedProps.participant_no = participantNos;
                event.extendedProps.participantsLoaded = true;
                calendar.getEventById(event.id).setExtendedProp('participant_no', participantNos);
                calendar.getEventById(event.id).setExtendedProp('participantsLoaded', true);
                filterEvents();   
            });
        }  
        return event;
    }

    function createExceptionEvent(exceptionEvent, currentDate, type) {
       const startDate = new Date(exceptionEvent.schedule_exception_start_date);
	    let endDate = exceptionEvent.schedule_exception_end_date ? new Date(exceptionEvent.schedule_exception_end_date) : null;
	 
	    if (!exceptionEvent.schedule_exception_start_time && !exceptionEvent.schedule_exception_end_time && endDate) {
	        endDate.setDate(endDate.getDate() + 1);
	    }
    
       var event = {
            order: 1,
            id: exceptionEvent.schedule_exception_no,
            title: exceptionEvent.schedule_exception_title,
            start: exceptionEvent.schedule_exception_start_date + (exceptionEvent.schedule_exception_start_time ? 'T' + exceptionEvent.schedule_exception_start_time : ''),
            end: endDate ? formatDate(endDate) + (exceptionEvent.schedule_exception_end_time ? 'T' + exceptionEvent.schedule_exception_end_time : '') : null,
            allDay: !exceptionEvent.schedule_exception_start_time && !exceptionEvent.schedule_exception_end_time,
            backgroundColor: categoryColors[exceptionEvent.schedule_exception_category_no] || '#3788d8',
            borderColor: categoryColors[exceptionEvent.schedule_exception_category_no] || '#3788d8',
            textColor: '#000000',
            className: type + '-event',
            extendedProps: {
				type: type,
                categoryName: categoryNames[exceptionEvent.schedule_exception_category_no],
                comment: exceptionEvent.schedule_exception_comment,
                createDate: exceptionEvent.schedule_exception_create_date,
                endDate: exceptionEvent.schedule_exception_end_date ? exceptionEvent.schedule_exception_end_date : null,
                startTime: exceptionEvent.schedule_exception_allday === 0 ? exceptionEvent.schedule_exception_start_time : null,
                endTime: exceptionEvent.schedule_exception_allday === 0 ? exceptionEvent.schedule_exception_end_time : null,
                exceptionNo: exceptionEvent.schedule_exception_no,
                isException: true 
            }
        };
        return event;
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
                left: 'prev,next today yearButton,monthButton',
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
            eventClick: function(info) {
				const eventStart = new Date(info.event.start.getTime() - (info.event.start.getTimezoneOffset() * 60000));
			    pickStartDate = eventStart.toISOString().split('T')[0];
			
			    let eventEnd;
			    if (info.event.end) { 
			        if (info.event.allDay) {
			            eventEnd = new Date(info.event.end.getTime() - 24 * 60 * 60 * 1000 - (info.event.end.getTimezoneOffset() * 60000));
			        } else {
			            eventEnd = new Date(info.event.end.getTime() - (info.event.end.getTimezoneOffset() * 60000));
			        }
			    } else {
			        eventEnd = eventStart;
			    }
			    pickEndDate = eventEnd.toISOString().split('T')[0]; 
			
			    if (info.event.extendedProps.isException) {
			        showExceptionEventModal(info.event);
			    } else if (info.event.extendedProps.createData) {
			        const eventId = info.event.id;
			        showEventModalById(calendar, eventId);
			    } else {
			        info.jsEvent.preventDefault();
			    }  
            },
            eventDidMount: function(info) { 
	            if (info.event.extendedProps.type === 'personalResult' || info.event.extendedProps.type === 'participateResult'
	            	|| info.event.extendedProps.type === 'scheduleDtos' || info.event.extendedProps.type === 'departmentResult') {
	                info.el.style.cursor = 'pointer';  
	            } else {
	                info.el.style.cursor = 'default'; 
	            }  
	        }, 
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
            eventClick: function(info) {
				console.log(info.event.id);
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
	
	        // 개인 일정
	        if (event.extendedProps.type === 'personalResult' && (event.extendedProps.member_no.toString() === memberNo)) {
	            shouldDisplay = true;  
	        } 
	        // 참여자 일정
	        else if (event.extendedProps.type === 'participateResult') {
                if (event.extendedProps.participantsLoaded) {
                    shouldDisplay = participantNos.includes(parseInt(memberNo));
                } else { 
                    shouldDisplay = true;
                }
            }  
	        // 전사 일정
	        else if (event.extendedProps.type === 'scheduleDtos') { 
	            shouldDisplay = companyChecked; 
	        } 
	        // 부서 일정  
	        else if (event.extendedProps.type === 'departmentResult') {
	            if (eventDepartmentNo.toString() === userDepartmentNo) { 
	                shouldDisplay = true;  
	            }  
	            else if (selectedDepartments.includes(eventDepartmentNo.toString())) { 
	                shouldDisplay = true; 
	            }
	        }
	
	        // 일정 표시 설정
	        event.setProp('display', shouldDisplay ? 'auto' : 'none');
	    });
	}


  
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