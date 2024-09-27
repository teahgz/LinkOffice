document.addEventListener('DOMContentLoaded', function() { 
	let participantMembers = [];
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    var calendarEl = document.getElementById('calendar');
    var calendar;
    
	const startInput = document.getElementById('startTime');
    const endInput = document.getElementById('endTime'); 
    
    var categoryColors = {};
    var categoryNames = {};
   
    var memberNo = document.getElementById('memberNo').value; 
    var userDepartmentNo = document.getElementById('departmentNo').value; 
    var createEmployeeScheduleForm = document.getElementById("eventForm");
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
        url: '/categories',
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
	            callback([]);
	        }
	    });
	} 
	
	// 예외 일정 참여자 정보 
	var exceptionParMemberNos = [];

	function searchExceptionParticipate(scheduleExceptionNo, callback) {
	    $.ajax({
	        url: '/api/participate/member/schedules/exception/' + scheduleExceptionNo,
	        method: 'GET',
	        dataType: 'json',
	        headers: {
	            'X-CSRF-TOKEN': csrfToken
	        },
	        success: function(data) {
	            exceptionParMemberNos = data.participants.map(participant => participant.member_no); 
	            callback(exceptionParMemberNos);
	        },
	        error: function(error) { 
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
                            allEvents.push(createExceptionEvent(exceptionEvent, currentDate, type, schedules[0].member_no)); 
                        } else if (exceptionEvent && exceptionEvent.schedule_exception_status === 1) {
                           
                        } else {  
                            allEvents.push(createEvent(schedule, type, currentDate, repeatInfo));
                        }
                         
                        currentDate = calculateNextRepeatDate(currentDate, repeatInfo);
                    }
                }
            }
        });  
    }

    function createEvent(schedule, type, date, repeatInfo) {
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
                isException: schedule.isException || false,
                repeatType: repeatInfo ? repeatInfo.schedule_repeat_type : null,
	            repeatDay : repeatInfo ? repeatInfo.schedule_repeat_day : null,
	            repeatWeek: repeatInfo ? repeatInfo.schedule_repeat_week : null,
	            repeatDate : repeatInfo ? repeatInfo.schedule_repeat_date : null,
	            repeatMonth: repeatInfo ? repeatInfo.schedule_repeat_month : null,
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

    function createExceptionEvent(exceptionEvent, currentDate, type, ori_memberNo) {
       const startDate = new Date(exceptionEvent.schedule_exception_start_date);
	   let endDate = exceptionEvent.schedule_exception_end_date ? new Date(exceptionEvent.schedule_exception_end_date) : null;
	 
	   if (!exceptionEvent.schedule_exception_start_time && !exceptionEvent.schedule_exception_end_time && endDate) {
	       endDate.setDate(endDate.getDate() + 1);
	   } 
       
       console.log(exceptionEvent);
       var event = {
            order: 1,
            id: exceptionEvent.schedule_exception_no,
            title: exceptionEvent.schedule_exception_title,
            start: exceptionEvent.schedule_exception_start_date + (exceptionEvent.schedule_exception_start_time ? 'T' + exceptionEvent.schedule_exception_start_time : ''),
            end: endDate ? formatDate(endDate) + (exceptionEvent.schedule_exception_end_time ? 'T' + exceptionEvent.schedule_exception_end_time : '') : null,
            allDay: !exceptionEvent.schedule_exception_start_time && !exceptionEvent.schedule_exception_end_time,
            backgroundColor: categoryColors[exceptionEvent.schedule_category_no] || '#3788d8',
            borderColor: categoryColors[exceptionEvent.schedule_category_no] || '#3788d8',
            textColor: '#000000',
            className: type + '-event',
            extendedProps: {
				type: type,
                categoryName: categoryNames[exceptionEvent.schedule_category_no],
                comment: exceptionEvent.schedule_exception_comment,
                createDate: exceptionEvent.schedule_exception_create_date,
                endDate: exceptionEvent.schedule_exception_end_date ? exceptionEvent.schedule_exception_end_date : null,
                startTime: exceptionEvent.schedule_exception_allday === 0 ? exceptionEvent.schedule_exception_start_time : null,
                endTime: exceptionEvent.schedule_exception_allday === 0 ? exceptionEvent.schedule_exception_end_time : null,
                exceptionNo: exceptionEvent.schedule_exception_no,
                isException: true,
                exceptionType: exceptionEvent.schedule_exception_type,
                originNo : exceptionEvent.schedule_no,
                member_no: ori_memberNo,
                department_no : exceptionEvent.department_no
            }
        };
        
        if (exceptionEvent.schedule_exception_type === 2) { 
            searchExceptionParticipate(exceptionEvent.schedule_exception_no, function(participantNos) {
                event.extendedProps.participant_no = participantNos;
                event.extendedProps.participantsLoaded = true;
                calendar.getEventById(event.id).setExtendedProp('participant_no', participantNos);
                calendar.getEventById(event.id).setExtendedProp('participantsLoaded', true);
                filterEvents();   
            });
        }  
        console.log(event);
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
			    } else if (info.event.extendedProps.type === 'personalResult' || info.event.extendedProps.type === 'participateResult'
	            	|| info.event.extendedProps.type === 'scheduleDtos' || info.event.extendedProps.type === 'departmentResult') {
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
            dayMaxEvents: 3 ,
            dateClick: function(info) {
			    selectedDate = info.dateStr;   
			    $('#eventDate').val(selectedDate);   
			    document.getElementById('eventDate').dispatchEvent(new Event('change'));
    
			    document.getElementById('eventModal').style.display = 'block';
			    const submitButton = document.getElementById('create_modal_submit');
				submitButton.textContent = '저장';
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
	
    // 일정 등록
    function roundToNearest30Minutes(timeStr) {
        const [hours, minutes] = timeStr.split(':').map(Number);
        const roundedMinutes = Math.round(minutes / 30) * 30;
        const adjustedHours = hours + Math.floor(roundedMinutes / 60);
        const adjustedMinutes = roundedMinutes % 60;
        return `${String(adjustedHours).padStart(2, '0')}:${String(adjustedMinutes).padStart(2, '0')}`;
    }

    function handleTimeChange(event) {
        const input = event.target;
        const originalValue = input.value;
        if (originalValue) {
            input.value = roundToNearest30Minutes(originalValue);
        }
    }
    
    startInput.addEventListener('change', handleTimeChange);
    endInput.addEventListener('change', handleTimeChange); 
    
	// 등록 모달 열기
	document.getElementById('addEventBtn').addEventListener('click', function() {
		document.getElementById('eventModal').style.display = 'block';
	});

	// 등록 모달 닫기 
	document.getElementById('closeModalBtn').addEventListener('click', function() {
		Swal.fire({
			text: '작성한 내용이 저장되지 않습니다.',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#B1C2DD',
			cancelButtonColor: '#C0C0C0',
			confirmButtonText: '확인',
			cancelButtonText: '취소',
		}).then((result) => {
			if (result.isConfirmed) {
				document.getElementById('eventModal').style.display = 'none';
				resetForm(createEmployeeScheduleForm);
				resetCreateModal();
				document.getElementById('eventRepeatModal').style.display = 'none';
			}
		});
	});
	
	document.getElementById('event_repeat_close_btn').addEventListener('click', function() { 
		document.getElementById('eventRepeatModal').style.display = 'none'; 
	});
	 
	function resetCreateModal() { 
	    reservation_form[0].reset();
	     
	    $('#reservationModal').modal('hide');
	 
	    $('#organization-chart').jstree("uncheck_all");
	 
	    const reservationArea = $('.selected-participants-container');  
	    reservationArea.find('.selected-participants').remove(); 
	    
	    selectedMembers = [];
	    localStorage.removeItem('selectedMembers');  
	    $('#selectedMembers').val('');
	}
	
	document.getElementById('closeModalBtn2').addEventListener('click', function() {
		Swal.fire({
			text: '작성한 내용이 저장되지 않습니다.',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#B1C2DD',
			cancelButtonColor: '#C0C0C0',
			confirmButtonText: '확인',
			cancelButtonText: '취소',
		}).then((result) => {
			if (result.isConfirmed) {
				document.getElementById('eventModal').style.display = 'none';
				resetForm(createEmployeeScheduleForm);
			}
		});
	});
	
	document.getElementById('eventDate').addEventListener('change', function() {
        const selectedDate = this.value;
        const endDateInput = document.getElementById('endDate');
        endDateInput.value = '';
        endDateInput.min = selectedDate; 
    });
    
	// 반복 옵션
	document.getElementById('eventDate').addEventListener('change', function() {
	    const selectedDate = new Date(this.value);
	    const repeatOption = document.getElementById('repeatOption');
	     
	    const dayOfWeek = selectedDate.toLocaleString('ko-KR', { weekday: 'long' });
	    const weekOfMonth = Math.ceil(selectedDate.getDate() / 7);
	 
	    repeatOption.innerHTML = `
	        <option value="0">반복 없음</option>
	        <option value="1">매일</option>
	        <option value="2">매주 ${dayOfWeek}</option>
	        <option value="3">매월 ${selectedDate.getDate()}일</option>
	        <option value="4">매월 ${weekOfMonth}번째 ${dayOfWeek}</option>
	        <option value="5">매년 ${selectedDate.getMonth() + 1}월 ${selectedDate.getDate()}일</option>
	    `;
	});
    
	// 종일 체크박스 선택  
	document.getElementById('allDay').addEventListener('change', function() {
	    const isChecked = this.checked;
	
	    const timeGroup = document.getElementById('timeGroup');
	    const startTimeInput = timeGroup.querySelector('input');
	
	    const endTimeGroup = document.getElementById('endTimeGroup');
	    const endTimeInput = endTimeGroup.querySelector('input');
	
	    const endDateGroup = document.getElementById('endDateGroup');
	    const endDateInput = endDateGroup.querySelector('input');
	
	    timeGroup.style.display = isChecked ? 'none' : 'block';
	    endTimeGroup.style.display = isChecked ? 'none' : 'block';
	    endDateGroup.style.display = isChecked ? 'block' : 'none';
	
	    // 클래스 추가 및 제거
	    if (timeGroup.style.display === 'block') {
	        startTimeInput.classList.add('plus_detail_option');
	    } else {
	        startTimeInput.classList.remove('plus_detail_option');  
	    }
	
	    if (endTimeGroup.style.display === 'block') {
	        endTimeInput.classList.add('plus_detail_option');
	    } else {
	        endTimeInput.classList.remove('plus_detail_option');  
	    }
	
	    if (endDateGroup.style.display === 'block') {
	        endDateInput.classList.add('plus_detail_option');
	    } else {
	        endDateInput.classList.remove('plus_detail_option');  
	    }
	}); 

	 
	// 카테고리 옵션
	$.ajax({
        url: '/employee/categories', 
        method: 'GET',
        success: function(data) {
			const categorySelect = $('#category');
            data.forEach(category => {
                categorySelect.append(`<option value="${category.schedule_category_no}">${category.schedule_category_name}</option>`);
            });
        } 
    });
    
    // 반복 종료  
	document.getElementById('eventDate').addEventListener('change', function() {
	    const eventDate = this.value;   
	    const repeatEndDate = document.getElementById('repeatEndDate');
	     
	    if (eventDate) {
	        repeatEndDate.min = eventDate;
	        repeatEndDate.value = '';   
	    }
	});
	
	document.getElementById('endDate').addEventListener('change', function() {
	    const endDate = this.value;   
	    const repeatEndDate = document.getElementById('repeatEndDate');
	     
	    if (endDate) {
	        repeatEndDate.min = endDate;
	        repeatEndDate.value = '';   
	    }
	});
	
	document.getElementById('allDay').addEventListener('change', function() {
	    const repeatEndDate = document.getElementById('repeatEndDate');
	    
	    repeatEndDate.value = ''; 
	});

	document.getElementById('repeatOption').addEventListener('change', function() {
	    const repeatEndGroup = document.getElementById('repeatEndGroup'); 
	    const repeatEndDate = document.getElementById('repeatEndDate');
	
	    if (this.value != 0) {
	        repeatEndGroup.style.display = 'block';
	        repeatEndDate.classList.add('plus_detail_option_2');
	    } else {
	        repeatEndGroup.style.display = 'none';  
	        repeatEndDate.classList.remove('plus_detail_option_2');  
	        repeatEndDate.value = '';   
	    } 
	});
	
	
	// 부서 체크박스, 참여자 버튼
	const departmentScheduleCheckbox = document.getElementById('department_schedule');
    const openOrganizationChartButton = document.getElementById('openOrganizationChartButton');
 
    departmentScheduleCheckbox.addEventListener('change', function () { 
        if (this.checked) {
            openOrganizationChartButton.disabled = true;
        } else {
            openOrganizationChartButton.disabled = false;
        }
    });
   
	$('#openOrganizationChartButton').on('click', function() {
        openOrganizationChartModal();
        updateCheckboxState();
    });
    
	function updateCheckboxState() {
	    if (document.getElementById('selectedMembers').value.trim() === '') {
	        departmentScheduleCheckbox.disabled = false; 
	    } else {
	        departmentScheduleCheckbox.disabled = true; 
	    }
	}
	
	function openOrganizationChartModal() {
	    selectedMembers = [];   
	
	    $('#organizationChartModal').modal('show');
	
	    loadOrganizationChart();
	}
	 
	document.getElementById('chart_close').addEventListener('click', function() { 
		$('#organizationChartModal').modal('hide');	 
	});
	
    // 조직도 로딩
    function loadOrganizationChart() {
        $.ajax({
            url: '/schedule/chart',
            method: 'GET',
            success: function(data) {
                console.log('조직도 데이터:', data);
                $('#organization-chart').jstree({ 
                    'core': {
                        'data': data,
                        'themes': { 
                            'icons': true,
                            'dots': false,
                            
                        }
                    },
                    'plugins': ['checkbox', 'types', 'search'],
                    'types': {
                        'default': {
                            'icon': 'fa fa-users'
                        },
                        'department': {
                            'icon': 'fa fa-users'
                        }, 
                        'member': {
			            	'icon': 'fa fa-user'  
			        	}
                    }
                }).on('ready.jstree', function (e, data) { 
                    setSelectedParticipants(participantMembers); 
                });

                // 체크박스 변경 시 선택된 사원 업데이트
                $('#organization-chart').on('changed.jstree', function (e, data) {
                    updateSelectedMembers(data.selected, data.instance);
                });
                
                // 검색  
                $('#organization_search').on('keyup', function() { 
                	const searchString = $(this).val();
	                $('#organization-chart').jstree(true).search(searchString);
	            });
            },
            error: function(xhr, status, error) {
                console.error('조직도 로딩 오류:', error);
            }
        });
    }

    // 선택된 사원 업데이트
    function updateSelectedMembers(selectedIds, instance) {
        const selectedMembersContainer = $('#selected-members');
        const permissionPickList = $('.permission_pick_list');
        selectedMembersContainer.empty();
        permissionPickList.empty();

        const selectedNodes = instance.get_selected(true);
        let selectedMembers = [];

        selectedNodes.forEach(function(node) {
            if (node.original.type === 'member') {
                const memberId = node.id;
                const memberNumber = memberId.replace('member_', ''); // 사원 번호
                const memberElement = $('<div class="selected-member"></div>');
                const memberName = $('<span></span>').text(node.text);
                const removeButton = $('<button class="remove-member">&times;</button>');

                memberElement.append(memberName).append(removeButton);
                selectedMembersContainer.append(memberElement);

                selectedMembers.push(memberNumber);

                removeButton.click(function() {
                    instance.uncheck_node(node);
                    memberElement.remove();
                    const index = selectedMembers.indexOf(memberNumber);
                    if (index !== -1) {
                        selectedMembers.splice(index, 1);
                    }

                    localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));

                    permissionPickList.find(`.permission-item[data-name="${node.text}"]`).remove();
                });

                const permissionItem = $(`<div class="permission-item" data-name="${node.text}"></div>`);
                permissionItem.text(node.text);
                permissionPickList.append(permissionItem);
            }
        });

        $('#selectedMembers').val(selectedMembers.join(','));

        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
    }  
    
    // 조직도 확인 -> 예약 모달 사원 출력 
	$('#participate_confirmButton').click(function()  {
	    const reservationArea = $('.selected-participants-container');  
	    const selectedMembersContainer = $('#selected-members');  
	    const selectedMembersList = selectedMembersContainer.find('.selected-member');
	    
	    reservationArea.find('.selected-participants').remove();  
	    
	    selectedMembersList.each(function() {
	        const memberName = $(this).find('span').text();
	        const participantItem = $('<span class="selected-participants"></span>');
	        participantItem.text(memberName);
	        reservationArea.append(participantItem);
	    }); 
	    $('#organizationChartModal').modal('hide');
	    updateCheckboxState();
	 });
	 
	// 요일 
	function getDayOfWeek() {
	    const selectedDate = new Date(document.getElementById('eventDate').value);
	    return selectedDate.getDay();  
	}
	
	// 주차 
	function getWeekNumber() {
	    const selectedDate = new Date(document.getElementById('eventDate').value);
	    return Math.ceil(selectedDate.getDate() / 7);  
	} 
	
	function showAlert(message) {
	    Swal.fire({
	        text: message,
	        icon: 'warning',
	        confirmButtonColor: '#B1C2DD',
	        confirmButtonText: '확인',
	    });
	}
	
	function resetForm(form) {
        form.reset(); 
    } 
	
	// 일정 등록 
	document.getElementById('eventForm').addEventListener('submit', function(event) {
		const type_form = document.getElementById('create_modal_submit').innerText;
	    event.preventDefault();
   		 
	    const title = document.getElementById('eventTitle').value.trim();
	    const category = document.getElementById('category').value;
	    const startDate = document.getElementById('eventDate').value;
	    const description = document.getElementById('description').value.trim();
	    const allDay = document.getElementById('allDay').checked; 
	    const endDate = document.getElementById('endDate').value;
	    const startTime = document.getElementById('startTime').value;
	    const endTime = document.getElementById('endTime').value;
	    const repeatOption = document.getElementById('repeatOption').value;
	    const repeatEndDate = document.getElementById('repeatEndDate').value;
	
	    if (!title) {
	        showAlert('일정 제목을 입력해 주세요.');
	        return;
	    }
	
	    if (!category) {
	        showAlert('카테고리를 선택해 주세요.');
	        return;
	    }
	
	    if (!startDate) {
	        showAlert('날짜를 선택해 주세요.');
	        return;
	    }
	 
	    if (!allDay) {
	        if (!startTime) {
	            showAlert('시작 시간을 입력해 주세요.');
	            return;
	        }
	        
	        if (!endTime) {
	            showAlert('종료 시간을 입력해 주세요.');
	            return;
	        }
	
	        if (endTime < startTime) {
	            showAlert('종료 시간은 시작 시간 이후로 설정해 주세요.');
	            return;
	        }
	    }
	    
	    if(allDay) {
			if (!startDate) {
	            showAlert('시작 날짜를 입력해 주세요.');
	            return;
	        }
	        
			if (!endDate) {
	            showAlert('종료 날짜를 입력해 주세요.');
	            return;
	        }
		}
	     
		if (repeatOption != 0 && !repeatEndDate && !$('#repeatOption').is(':disabled')) {
		    showAlert('반복 종료일을 입력해 주세요.');
		    return;
		} 
	    if (!description) {
	        showAlert('내용을 입력해 주세요.');
	        return;
	    } 
	
	    // 반복 옵션 값 
	    const repeat_insert_date = document.getElementById('eventDate').value;
	    const repeat = parseInt(document.getElementById('repeatOption').value);
	    const repeatDayOfWeek = repeat === 2 ||  repeat ===  4 ? getDayOfWeek() : null; // 요일
	    const repeatWeek = repeat === 4 ? getWeekNumber() : null; // 주차
	    const repeatDate = repeat === 3 ||  repeat ===  5 ? new Date(repeat_insert_date).getDate() : null; // 특정 일
	    const repeatMonth = repeat === 5 ? new Date(repeat_insert_date).getMonth() + 1 : null; // 특정 월
	 
	 	const department_schedule = document.getElementById('department_schedule').checked; 
	 	const selectedMembers = document.getElementById('selectedMembers').value;
	 	var schedule_type = 0;
	 	var departmentNo; 
	 	
	 	if (selectedMembers) { 
		    schedule_type = 2;
		} else if (department_schedule) { 
		    schedule_type = 1;
		    departmentNo = userDepartmentNo; 
		} else { 
		    schedule_type = 0;
		}
	 	
	    const eventData = {
	        title: title,
	        category: category,
	        startDate: startDate,
	        endDate : allDay ? endDate : null,
	        repeatEndDate : repeatEndDate,
	        startTime: allDay ? null : startTime,  
	        endTime: allDay ? null : endTime,
	        allDay: allDay,
	        repeat: repeat,
	        description: description,
	        repeatEndDate: repeatEndDate,
	        schedule_day_of_week: repeatDayOfWeek,
	        schedule_week: repeatWeek,
	        schedule_date: repeatDate,
	        schedule_month: repeatMonth,
	        schedule_type : schedule_type,
	        department_no : departmentNo,
	        selectedMembers: selectedMembers
	    };
	  
		if(type_form === "저장") {     
			console.log(eventData);
		    $.ajax({
		        type: "POST",
		        url: '/employee/schedule/save/' + memberNo,   
		        contentType: 'application/json',
		        data: JSON.stringify(eventData),
		        headers: {
		            'X-CSRF-TOKEN': csrfToken
		        },
		        success: function(response) {
					if (response.res_code === "200") {
		                Swal.fire({
		                    text: response.res_msg,
		                    icon: 'success',
		                    confirmButtonColor: '#B1C2DD',
		                    confirmButtonText: '확인'
		                }).then(() => {
		                  	window.location.reload();  
		                	document.getElementById('eventModal').style.display = 'none';
		                });
		            } else {
		                Swal.fire({
		                    text: response.res_msg,
		                    icon: 'error',
		                    confirmButtonColor: '#B1C2DD',
		                    confirmButtonText: '확인'
		                });
		            } 
		        } 
		    }); 
		} 
		// 수정
		else {
			const isRecurring = document.getElementById('isRecurring').value;    
			const schedule_edit_type = eventData.schedule_type; 
			 
			// 예외 일정
		    if (isRecurring === "1") {   
		        openEventRepeatModal();
		    } else {  
		        submitEventUpdate(schedule_edit_type, isRecurring); 
		    }
		}
	});
	
	window.addEventListener('click', function(event) {
	    const modal = document.getElementById('eventViewModal');
	    if (event.target === modal) {
	        modal.style.display = 'none';
	    }
	});
	
	// 일정 상세
	function showEventModalById(calendar, eventId) {
	    const event = calendar.getEventById(eventId);   
	    if (!event) {
	        console.error("일정을 찾을 수 없습니다.");
	        return;
	    }
	 	console.log(event);
	    const modal = document.getElementById('eventViewModal');
	    const title = document.getElementById('eventViewTitle');
	    const dateRange = document.getElementById('eventViewDateRange');
	    const category = document.getElementById('eventViewCategory');
	    const comment = document.getElementById('eventViewComment');
	    const createdDate = document.getElementById('eventViewCreatedDate');
	    const repeatInfo = document.getElementById('eventViewRepeatInfo'); 
	    const hiddenEventId = document.getElementById('eventId'); 
 		const hiddenIsException = document.getElementById('isException'); 
 		hiddenIsException.value = 1;
	    title.textContent = event.title;
	    category.textContent = `[` + event.extendedProps.categoryName + `]`;
	  	
	    const startDate = pickStartDate;
	    const endDate = pickEndDate;
	    const startTime = event.extendedProps.startTime;
	    const endTime = event.extendedProps.endTime;
	 	const allDay = event.allDay; 
	 	
	    if(allDay && startDate === endDate) {
	    	dateRange.textContent = `${startDate}`; 
		} else if(!allDay) {  
			if(startTime === endTime) { 
				dateRange.textContent = `${startDate} ${startTime}`;				
			} 
			else {
				dateRange.textContent = `${startDate} ${startTime} ~ ${endTime}`;	
			}
		} else {
			dateRange.textContent = `${startDate} ~ ${endDate}`; 
		}
		 
		if (event.extendedProps.type === 'departmentResult' && event.extendedProps.member_no !== Number(memberNo)) {  
		    document.getElementById('editEventBtn').style.display = 'none';
		    document.getElementById('deleteEventBtn').style.display = 'none';
		} else if (event.extendedProps.type === 'scheduleDtos') {  
		    document.getElementById('editEventBtn').style.display = 'none';
		    document.getElementById('deleteEventBtn').style.display = 'none';
		} else if (event.extendedProps.type === 'participateResult' && event.extendedProps.member_no !== Number(memberNo)) {  
		    document.getElementById('editEventBtn').style.display = 'none';
		    document.getElementById('deleteEventBtn').style.display = 'none';
		}
		else {
		    document.getElementById('editEventBtn').style.display = 'block';
		    document.getElementById('deleteEventBtn').style.display = 'block'; 
		} 

		document.getElementById('isRecurring_view').value = 0;
	 	hiddenEventId.value = eventId;
	    comment.textContent = event.extendedProps.comment; 
	    createdDate.textContent = event.extendedProps.createDate.substr(0,10) + ` 등록`; 
	    repeatInfo.textContent = getRepeatInfoText(event.extendedProps.repeatType, event.extendedProps.repeatDay, event.extendedProps.repeatWeek , event.extendedProps.repeatDate , event.extendedProps.repeatMonth);
	    modal.style.display = 'block';  
	}  
	
	// 예외 상세 모달
	function showExceptionEventModal(event) {
	    const modal = document.getElementById('eventViewModal');
	    const title = document.getElementById('eventViewTitle');
	    const dateRange = document.getElementById('eventViewDateRange');
	    const category = document.getElementById('eventViewCategory');
	    const comment = document.getElementById('eventViewComment');
	    const createdDate = document.getElementById('eventViewCreatedDate');
	    const repeatInfo = document.getElementById('eventViewRepeatInfo'); 
	    const hiddenEventId = document.getElementById('eventId'); 
		const hiddenIsException = document.getElementById('isException'); 
		document.getElementById('isRecurring').value = 2;
		hiddenIsException.value = 0;
	    title.textContent = event.title;
	    category.textContent = `[${event.extendedProps.categoryName}]`;
	   
		const startDate = pickStartDate;
	    const endDate = pickEndDate;
	    const startTime = event.extendedProps.startTime;
	    const endTime = event.extendedProps.endTime;
	    document.getElementById('isRecurring_view').value = 2; 
	    
	    if(startDate === endDate) {
	    	dateRange.textContent = `${startDate}`; 
		} else if(endDate === null) {
			if(startTime === endTime) {
				dateRange.textContent = `${startDate} ${startTime}`;				
			} 
			else {
				dateRange.textContent = `${startDate} ${startTime} ~ ${endTime}`;	
			}
		} else {
			dateRange.textContent = `${startDate} ~ ${endDate}`; 
		}
			
	    hiddenEventId.value = event.extendedProps.exceptionNo; 
	    comment.textContent = event.extendedProps.comment; 
	    createdDate.textContent = event.extendedProps.createDate.substr(0,10) + ` 등록`; 
	    repeatInfo.textContent = '';
	
	    modal.style.display = 'block';  
	}
	
	function getRepeatInfoText(repeatType, repeatDay, repeatWeek, repeatDate, repeatMonth) { 
	    let info_type = '';
	    let info_day = ''; 
	
	    switch(repeatType) {
	        case 1:  
	            info_type = '매일 반복';
	            break;
	        case 2:  
	            info_type = '매주';
	            info_day = getDayInformation(repeatDay);  
	            break;
	        case 3:  
	            info_type = `매월 ${repeatDate}일 반복`;
	            break;
	        case 4:  
	            info_type = `매월 ${repeatWeek}번째 ${getDayInformation(repeatDay)} 반복`;
	            break;
	        case 5: 
	            info_type = `매년 ${repeatMonth}월 ${repeatDate}일 반복`;
	            break;
	        default:
	            info_type = '';
	            break;
	    }
	    
	    return info_type + (info_day ? ' ' + info_day + ' 반복' : '');
	}
	 
	function getDayInformation(day) {
	    const days = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
	    return days[day];
	} 
	
	
	// 수정
	document.getElementById('editEventBtn').addEventListener('click', function() { 
		const isException = document.getElementById('isException').value;
	    const eventId = document.getElementById('eventId').value;
	    const event = calendar.getEventById(eventId); 
	    if (isException === "0") {
	        openExceptionScheduleEditModal(eventId);
	    } else {
	        openScheduleEditModal(eventId);
	    }
	    
	});
	
	// 수정 모달
	function openScheduleEditModal(eventNo) {
	    var modal = document.getElementById('eventModal');
	    modal.style.display = 'block';
	    document.getElementById('eventViewModal').style.display = 'none';
		
		const submitButton = document.getElementById('create_modal_submit');
   		submitButton.textContent = '수정';
    	
    	const editTitle = document.getElementById('modal-title');
	    editTitle.textContent = '일정 수정'; 
	    
	    $.ajax({
	        url: '/schedule/edit/' + eventNo,
	        type: 'GET',
	        dataType: 'json',
	        success: function(data) {
				console.log("scheduleDto : ", data.schedule);  
        		console.log("scheduleRepeat : ", data.scheduleRepeat);  
 				 
	            $('#eventId').val(data.schedule.schedule_no);  
	            $('#isRecurring').val(data.schedule.schedule_repeat);  
	            $('#category').val(data.schedule.schedule_category_no);
	            $('#eventTitle').val(data.schedule.schedule_title);
	            $('#eventDate').val(pickStartDate);
	            document.getElementById('eventDate').dispatchEvent(new Event('change'));
	            
	            if (data.schedule.schedule_allday === 1) {
	                $('#allDay').prop('checked', true);
	                document.getElementById('allDay').dispatchEvent(new Event('change'));
	
	                $('#endDate').val(pickEndDate);
	                document.getElementById('endDate').dispatchEvent(new Event('change'));
	            } else {
	                $('#allDay').prop('checked', false);
	                document.getElementById('allDay').dispatchEvent(new Event('change')); 
	                $('#startTime').val(data.schedule.schedule_start_time);
	                $('#endTime').val(data.schedule.schedule_end_time);
	            }
	
	            $('#description').val(data.schedule.schedule_comment);
	             
	            if (data.schedule.schedule_repeat === 1) {
		            $('#repeatOption').val(data.scheduleRepeat.schedule_repeat_type);
		            document.getElementById('repeatOption').dispatchEvent(new Event('change'));
	                $('#repeatEndDate').val(data.scheduleRepeat.schedule_repeat_end_date);
	            } 		  
	            
	            if (data.schedule.schedule_type === 1) {
		            $('#department_schedule').prop('checked', true); 
		             document.getElementById('department_schedule').dispatchEvent(new Event('change'));
	            }  
	            
	            if (data.schedule.schedule_type === 2) { 
		            $('#department_schedule').prop('disabled', true); 
		            findParticipants(eventNo);            
	            }       
	        },
	        error: function(xhr, status, error) {
	            console.log("일정 정보를 불러오는 중 오류 발생: " + error);
	        }
	    });
	} 
	
	// 예외 수정 모달
	function openExceptionScheduleEditModal(eventId) {
	    var modal = document.getElementById('eventModal');
	    modal.style.display = 'block';
	    document.getElementById('eventViewModal').style.display = 'none';
		
	    const editTitle = document.getElementById('modal-title');
	    editTitle.textContent = '일정 수정';
	    
	    const submitButton = document.getElementById('create_modal_submit');
	    submitButton.textContent = '수정';
	     
	    document.getElementById('isRecurring').value = 2;
	    
	    $.ajax({
	        url: '/schedule/exception/edit/' + eventId,
	        type: 'GET',
	        dataType: 'json',
	        success: function(data) {
				console.log("exception scheduleDto : ", data.schedule);  
        		console.log("exception scheduleRepeat : ", data.scheduleRepeat);
        		 
	            $('#eventId').val(eventId);    
	            $('#category').val(data.schedule.schedule_category_no);
	            $('#eventTitle').val(data.schedule.schedule_exception_title);
	            $('#eventDate').val(data.schedule.schedule_exception_start_date);
	            document.getElementById('eventDate').dispatchEvent(new Event('change'));
	            
	            if (!data.schedule.schedule_exception_start_time && !data.schedule.schedule_exception_end_time) {
	                $('#allDay').prop('checked', true);
	                document.getElementById('allDay').dispatchEvent(new Event('change'));
	                $('#endDate').val(data.schedule.schedule_exception_end_date);
	            } else {
	                $('#allDay').prop('checked', false);
	                document.getElementById('allDay').dispatchEvent(new Event('change')); 
	                $('#startTime').val(data.schedule.schedule_exception_start_time);
	                $('#endTime').val(data.schedule.schedule_exception_end_time);
	            }
	
	            $('#description').val(data.schedule.schedule_exception_comment);
	            
    	        $('#repeatOption').val('0');
	            $('#repeatOption').prop('disabled', true).hide();
	            $('label[for="repeatOption"]').hide();
	            $('#repeatEndDate').val('');
	            $('#repeatEndDate').prop('disabled', true).hide();
	            
	            if (data.schedule.schedule_exception_type === 1) {
		            $('#department_schedule').prop('checked', true); 
		             document.getElementById('department_schedule').dispatchEvent(new Event('change'));
	            }  
	            
	            if (data.schedule.schedule_exception_type === 2) {  
		            $('#department_schedule').prop('disabled', true); 
		            findExceptionParticipants(eventId);            
	            }  
	             
	        } 
	    });
	}
	
	// 반복 일정 수정 모달 
	function openEventRepeatModal() { 
	    var repeatModal = document.getElementById('eventRepeatModal');
	    repeatModal.style.display = 'block';
	}
	
	document.getElementById('event_repeat_modal_btn').addEventListener('click', function() {
	    const eventId = document.getElementById('eventId').value; 
	    const repeatEditOption = document.querySelector('input[name="repeatEditOption"]:checked').value;
	 	const typeEventRepeat = document.getElementById('event_repeat_title').innerText; 
 
	 	const department_schedule = document.getElementById('department_schedule').checked; 
	 	const selectedMembers = document.getElementById('selectedMembers').value;
	 	 
	 	var schedule_edit_type = 0; 
	 	
	 	if (selectedMembers) { 
		    schedule_edit_type = 2;
		} else if (department_schedule) { 
		    schedule_edit_type = 1; 
		} else { 
		    schedule_edit_type = 0;
		}
		
	 	if(typeEventRepeat === "반복 일정 수정") {
		    handleRecurringEventUpdate(eventId, repeatEditOption, schedule_edit_type);			
		} else {
			repeatDelete(eventId, repeatEditOption);
		}
	 
	    document.getElementById('eventRepeatModal').style.display = 'none';
	}); 
	
	// 반복 일정 수정  
	function handleRecurringEventUpdate(eventId, repeatEditOption, schedule_edit_type) {
	    const eventData = getEventFormData();   
	    
	    console.log("eventData : ", eventData); 
	    
	    $.ajax({
	        type: "POST",
	        url: '/employee/schedule/edit/recurring/' + eventId + '/' + schedule_edit_type + '?editOption=' + repeatEditOption + '&pickStartDate=' + pickStartDate + '&pickEndDate=' + pickEndDate,
	        contentType: 'application/json',
	        data: JSON.stringify(eventData),
	        headers: {
	            'X-CSRF-TOKEN': csrfToken,
	        },
	        success: function(response) {
				 if (response.res_code === '200') {
	                Swal.fire({ 
		                text: response.res_msg,
		                icon: 'success',
		                confirmButtonText: '확인',
		                confirmButtonColor: '#B1C2DD',
		            }).then(function() {
		                window.location.reload();
		            });
	            } else {
	                Swal.fire('반복 일정', response.res_msg, 'error');
	            }   
            },
	        error: function () {
	            Swal.fire("서버 오류", "반복 일정 수정 중 오류가 발생했습니다.", "error");
	        }
	    });
	} 

	// 일정 수정 확인
	function submitEventUpdate(schedule_edit_type, isRecurring) {
	    const eventData = getEventFormData();
    	const isException = isRecurring === '0'; 
		const url = isException ? `/employee/schedule/edit/${document.getElementById('eventId').value}/${schedule_edit_type}`
					: `/employee/schedule/exception/edit/${document.getElementById('eventId').value}/${schedule_edit_type}`;
	     
	    $.ajax({
	        type: "POST",
	        url: url,
	        contentType: 'application/json',
	        data: JSON.stringify(eventData),
	        headers: {
	            'X-CSRF-TOKEN': csrfToken,
	        },
	        success: function(response) {
	            Swal.fire({
	                text: '일정이 수정되었습니다.',
	                icon: 'success',
	                confirmButtonText: '확인',
	                confirmButtonColor: '#B1C2DD',
	            }).then(function() {
	                window.location.reload();
	            });
	        },
	        error: function(xhr, status, error) {
	            console.error('일정 수정 오류: ', error);
	        }
	    });
	}
	
	// 수정용
	function getEventFormData() {
	    const title = document.getElementById('eventTitle').value.trim();
	    const category = document.getElementById('category').value;
	    const startDate = document.getElementById('eventDate').value;
	    const endDate = document.getElementById('endDate').value;
	    const description = document.getElementById('description').value.trim();
	    const allDay = document.getElementById('allDay').checked;
	    const startTime = document.getElementById('startTime').value;
	    const endTime = document.getElementById('endTime').value;
	    const repeatOption = document.getElementById('repeatOption').value;
	    const repeatEndDate = document.getElementById('repeatEndDate').value;
	
		const repeat_insert_date = document.getElementById('eventDate').value;
	    const repeatDayOfWeek = repeatOption === "2" || repeatOption === "4" ? getDayOfWeek() : null; // 요일
	    const repeatWeek = repeatOption === "4" ? getWeekNumber() : null; // 주차
	    const repeatDate = repeatOption === "3" || repeatOption === "5" ? new Date(repeat_insert_date).getDate() : null; // 특정 일
	    const repeatMonth = repeatOption === "5" ? new Date(repeat_insert_date).getMonth() + 1 : null; // 특정 월
		
		const department_schedule = document.getElementById('department_schedule').checked; 
	 	const selectedMembers = document.getElementById('selectedMembers').value;
	 	var schedule_type = 0;
	 	var departmentNo; 
	 	
	 	if (selectedMembers) { 
		    schedule_type = 2;
		} else if (department_schedule) { 
		    schedule_type = 1;
		    departmentNo = userDepartmentNo; 
		} else { 
		    schedule_type = 0;
		}
		
	    return {
	        title: title,
	        category: category,
	        startDate: startDate,
	        endDate : allDay ? endDate : null, 
	        startTime: allDay ? null : startTime,  
	        endTime: allDay ? null : endTime,
	        allDay: allDay,
	        repeat: repeatOption,
	        description: description,
	        repeatEndDate: repeatEndDate,
	        schedule_day_of_week: repeatDayOfWeek,
	        schedule_week: repeatWeek,
	        schedule_date: repeatDate,
	        schedule_month: repeatMonth,
	        schedule_type : schedule_type,
	        department_no : departmentNo,
	        selectedMembers: selectedMembers,
	        memberNo : memberNo
	    };
	}
	
	// 참여자 정보 가져오기
	function findParticipants(scheduleNo) {
		$.ajax({
	        url: '/employee/schedule/participate/' + scheduleNo,
	        type: 'GET',
	        dataType: 'json',
	        success: function(data) {   
	       		let filteredParticipants = data.participants.filter(p => (p.member_no).toString() !== memberNo);
	       		
	       		let selectedParticipants = filteredParticipants
	                .map(p => `<span class="selected-participants">${p.memberName} ${p.positionName}</span>`)
	                .join(' ');
	            $('.selected-participants-container').html(selectedParticipants);
	        	participantMembers = filteredParticipants.map(p => p.member_no); 
	            $('#selectedMembers').val(participantMembers.join(',')); 
	    		 
	            setSelectedParticipants(data.participants);  
	            var jstree = $('#organization-chart').jstree(true);
			    if (jstree) {
			        participants.forEach(function(memberNo) {
			            var nodeId = 'member_' + memberNo;
			            if (jstree.get_node(nodeId)) { 
			                jstree.check_node(nodeId);
			            }
			        });
			    } 
	        },
	        error: function(xhr, status, error) {
	            console.log(error);
	        }
	    });  
	}
	
	// 예외 참여자 정보 가져오기
	function findExceptionParticipants(scheduleNo) { 
		$.ajax({
	        url: '/employee/schedule/participate/exception/' + scheduleNo,
	        type: 'GET',
	        dataType: 'json',
	        success: function(data) {   
	       		let filteredParticipants = data.participants.filter(p => (p.member_no).toString() !== memberNo);
	       		
	       		let selectedParticipants = filteredParticipants
	                .map(p => `<span class="selected-participants">${p.memberName} ${p.positionName}</span>`)
	                .join(' ');
	            $('.selected-participants-container').html(selectedParticipants);
	        	participantMembers = filteredParticipants.map(p => p.member_no); 
	            $('#selectedMembers').val(participantMembers.join(',')); 
	    		 
	            setSelectedParticipants(data.participants);  
	            console.log("a :c " + data.participants);
	            var jstree = $('#organization-chart').jstree(true);
			    if (jstree) {
			        participants.forEach(function(memberNo) {
			            var nodeId = 'member_' + memberNo;
			            if (jstree.get_node(nodeId)) { 
			                jstree.check_node(nodeId);
			            }
			        });
			    } 
	        },
	        error: function(xhr, status, error) {
	            console.log(error);
	        }
	    });  
	}
	
    // 조직도에서 체크박스 선택 설정
    function setSelectedParticipants(participants) {
	    var jstree = $('#organization-chart').jstree(true);
	    if (jstree) {
	        participants.forEach(function(memberNo) {
	            var nodeId = 'member_' + memberNo;
	            if (jstree.get_node(nodeId)) { 
	                jstree.check_node(nodeId);
	            }
	        });
	    } 
	}
	
	// 삭제
	// 일정 삭제 버튼
	document.getElementById('deleteEventBtn').addEventListener('click', function() { 
		const isException = document.getElementById('isException').value;
		const isOne = document.getElementById('eventViewRepeatInfo').innerText.trim(); 
	    const eventId = document.getElementById('eventId').value;  
	    const isOneBoolean = !!isOne;  
	    
	    console.log("isException : " + isException);
	    console.log("isOne : " + isOne);
	    console.log("isOneBoolean : " + isOneBoolean);
	    
 		
 		// 반복 예외
	    if (isException === "0" && !isOneBoolean) { 
	        exceptionDelete(eventId);
	    } 
	    // 기본 일정 (반복X)
	    else if(!isOneBoolean){  
	        basicDelete(eventId);
	    }
	    else {  
			document.getElementById('event_repeat_title').innerText ='반복 일정 삭제';
			openEventRepeatModal(); 
		}
	    
	});
 
 	// 기본 삭제
 	function basicDelete(eventId) {
		Swal.fire({
            text: '일정을 삭제하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#EEB3B3',
            cancelButtonColor: '#C0C0C0',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    type: 'POST',  
                    url: '/company/schedule/delete',   
                    contentType: 'application/json',
                    data: JSON.stringify({ eventId: eventId }),  
                    headers: {
                        'X-CSRF-TOKEN': csrfToken
                    },
                    success: function(response) {
                        if (response.res_code === '200') {
                            Swal.fire({
                                text: response.res_msg,
                                icon: 'success',
                                confirmButtonColor: '#B1C2DD',
                                confirmButtonText: '확인'
                            }).then(() => {
                                 location.href = "/employee/schedule";  
                            });
                        } else {
                            Swal.fire({
                                text: response.res_msg,
                                icon: 'error',
                                confirmButtonColor: '#B1C2DD',
                                confirmButtonText: '확인'
                            });
                        }
                    },
                    error: function () {
                        Swal.fire('서버 오류', response.res_msg, 'error');
                    }
                });
            }
        }); 
	}
	
	// 예외 삭제
 	function exceptionDelete(eventId) {
		Swal.fire({
            text: '일정을 삭제하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#EEB3B3',
            cancelButtonColor: '#C0C0C0',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    type: 'POST',  
                    url: '/company/schedule/exception/delete',   
                    contentType: 'application/json',
                    data: JSON.stringify({ eventId: eventId }),  
                    headers: {
                        'X-CSRF-TOKEN': csrfToken
                    },
                    success: function(response) {
                        if (response.res_code === '200') {
                            Swal.fire({
                                text: response.res_msg,
                                icon: 'success',
                                confirmButtonColor: '#B1C2DD',
                                confirmButtonText: '확인'
                            }).then(() => {
                                 location.href = "/employee/schedule";  
                            });
                        } else {
                            Swal.fire({
                                text: response.res_msg,
                                icon: 'error',
                                confirmButtonColor: '#B1C2DD',
                                confirmButtonText: '확인'
                            });
                        }
                    },
                    error: function () {
                        Swal.fire('서버 오류', response.res_msg, 'error');
                    }
                });
            }
        }); 
	}
	
	// 반복 삭제  
 	function repeatDelete(eventId, repeatEditOption) {   
        $.ajax({
            type: 'POST',   
            url: '/company/schedule/repeat/delete/' + eventId + '?editOption=' + repeatEditOption + '&pickStartDate=' + pickStartDate + '&pickEndDate=' + pickEndDate,   
            contentType: 'application/json', 
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            success: function(response) {
                if (response.res_code === '200') {
                    Swal.fire({
                        text: response.res_msg,
                        icon: 'success',
                        confirmButtonColor: '#B1C2DD',
                        confirmButtonText: '확인'
                    }).then(() => {
                         location.href = "/employee/schedule";  
                    });
                } else {
                    Swal.fire({
                        text: response.res_msg,
                        icon: 'error',
                        confirmButtonColor: '#B1C2DD',
                        confirmButtonText: '확인'
                    });
                }
            },
            error: function () {
                Swal.fire('서버 오류', response.res_msg, 'error');
            }
        });
    } 
     
});