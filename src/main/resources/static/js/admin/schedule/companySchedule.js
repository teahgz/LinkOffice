document.addEventListener('DOMContentLoaded', function() {
	var csrfToken = document.querySelector('input[name="_csrf"]').value;
	var createCompanyScheduleForm = document.getElementById("eventForm");
	const startInput = document.getElementById('startTime');
    const endInput = document.getElementById('endTime'); 
    
    var calendarEl = document.getElementById('calendar');
	var calendar;
	var categoryColors = {};
	var categoryNames = {};
	
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
            fetchSchedules();
        }
    });
    
    function fetchSchedules() {
	    $.ajax({
	        url: '/api/company/schedules',
	        method: 'GET',
	        contentType: 'application/json',
	        headers: {
	            'X-CSRF-TOKEN': csrfToken
	        }
	    })
	    .done(function(schedules) {
	        $.ajax({
	            url: '/api/repeat/schedules',
	            method: 'GET',
	            contentType: 'application/json',
	            headers: {
	                'X-CSRF-TOKEN': csrfToken
	            }
	        })
	        .done(function(repeats) {
	            var events = [];
	
	            schedules.forEach(function(schedule) {
	                if (schedule.schedule_repeat === 0) { 
	                    events.push(createEvent(schedule));
	                } else {
	                    // 반복 일정
	                    var repeatInfo = repeats.find(r => r.schedule_no === schedule.schedule_no);
	                    if (repeatInfo) {
	                        var startDate = new Date(schedule.schedule_start_date);
	                        var endDate = repeatInfo.schedule_repeat_end_date ? new Date(repeatInfo.schedule_repeat_end_date) : new Date(startDate.getFullYear() + 1, startDate.getMonth(), startDate.getDate());
	                        var currentDate = new Date(startDate);
	
	                        while (currentDate <= endDate) {
	                            events.push(createEvent(schedule, currentDate, repeatInfo)); 
	                            
	                            switch (repeatInfo.schedule_repeat_type) {
	                                case 1: // 매일
	                                    currentDate.setDate(currentDate.getDate() + 1);
	                                    break;
	                                case 2: // 매주 n요일
	                                    currentDate.setDate(currentDate.getDate() + 7);
	                                    break;
	                                case 3: // 매월 n일
	                                    currentDate.setMonth(currentDate.getMonth() + 1); 
	                                    while (currentDate.getDate() !== repeatInfo.schedule_repeat_date) {
	                                        if (currentDate.getDate() > repeatInfo.schedule_repeat_date) {
	                                            currentDate.setDate(1);
	                                            currentDate.setMonth(currentDate.getMonth() + 1);
	                                        } else {
	                                            currentDate.setDate(currentDate.getDate() + 1);
	                                        }
	                                    }
	                                    break;
	                                case 4: // 매월 n번째 n요일
	                                    do {
	                                        currentDate.setDate(currentDate.getDate() + 1);
	                                    } while (
	                                        currentDate.getDay() !== repeatInfo.schedule_repeat_day ||
	                                        Math.floor((currentDate.getDate() - 1) / 7) + 1 !== repeatInfo.schedule_repeat_week
	                                    );
	                                    break;
	                                case 5: // 매년
	                                    currentDate.setFullYear(currentDate.getFullYear() + 1);
	                                    break;
	                            }
	                        }
	                    }
	                }
	            }); 
	            initializeCalendar(events);
	        }) 
	    }) 
	}
	
	// DB 일정 생성
	function createEvent(schedule, currentDate, repeatInfo) { 
	    var eventStart = currentDate || new Date(schedule.schedule_start_date);
	    var eventEnd = schedule.schedule_end_date ? 
	        new Date(new Date(schedule.schedule_end_date).getTime() + 24 * 60 * 60 * 1000) :  
	        null;
	
	    if (eventEnd) { 
	        if (currentDate) {
	            var duration = eventEnd.getTime() - new Date(schedule.schedule_start_date).getTime();
	            eventEnd = new Date(eventStart.getTime() + duration);
	        }
	    }
	
	    return {
	        id: schedule.schedule_no,
	        title: schedule.schedule_title,
	        start: formatDate(eventStart) + (schedule.schedule_start_time ? 'T' + schedule.schedule_start_time : ''),
	        end: eventEnd ? formatDate(eventEnd) + (schedule.schedule_end_time ? 'T' + schedule.schedule_end_time : '') : null,
	        allDay: schedule.schedule_allday === 1,
	        backgroundColor: categoryColors[schedule.schedule_category_no] || '#3788d8',  
	        borderColor: categoryColors[schedule.schedule_category_no] || '#3788d8',
	        extendedProps: {
	            categoryName: categoryNames[schedule.schedule_category_no],
	            comment: schedule.schedule_comment,
	            repeatOption: schedule.schedule_repeat,
	            createDate: schedule.schedule_create_date,
	            startDate: eventStart ? schedule.schedule_start_date : null,
	            endDate: eventEnd ? schedule.schedule_end_date : null,  
	            startTime: schedule.schedule_allday === 0 ? schedule.schedule_start_time : null,
	            endTime: schedule.schedule_allday === 0 ? schedule.schedule_end_time : null,  
	            repeatType: repeatInfo ? repeatInfo.schedule_repeat_type : null,
	            repeatDay : repeatInfo ? repeatInfo.schedule_repeat_day : null,
	            repeatWeek: repeatInfo ? repeatInfo.schedule_repeat_week : null,
	            repeatDate : repeatInfo ? repeatInfo.schedule_repeat_date : null,
	            repeatMonth: repeatInfo ? repeatInfo.schedule_repeat_month : null,
	        } 
	    };
	}
	
	function initializeCalendar(events) {
        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            events: events,
            eventClick: function(info) {
                // google calendar 연결 막기
                // info.jsEvent.preventDefault();
                
                const eventId = info.event.id; 
            	showEventModalById(calendar, eventId); 
            },
            googleCalendarApiKey: 'AIzaSyBaQi-ZLyv7aiwEC6Ca3C19FE505Xq2Ytw',
            eventSources: [
                {
                    googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                    color: 'transparent',
                    textColor: 'red' 
                }
            ],
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
                if (info.view.type === "dayGridMonth") {
                    return {
                        html: number.outerHTML
                    };
                }
                return {
                    domNodes: []
                };
            }
        });

        calendar.render();
    }
    
    function showEventModalById(calendar, eventId) {
	    const event = calendar.getEventById(eventId);  
		console.log("extendedProps 전체: ", event.extendedProps);
	    if (!event) {
	        console.error("해당 ID로 이벤트를 찾을 수 없습니다.");
	        return;
	    }
	
	    // 모달 요소
	    const modal = document.getElementById('eventViewModal');
	    const title = document.getElementById('eventViewTitle');
	    const dateRange = document.getElementById('eventViewDateRange');
	    const category = document.getElementById('eventViewCategory');
	    const comment = document.getElementById('eventViewComment');
	    const createdDate = document.getElementById('eventViewCreatedDate');
	    const repeatInfo = document.getElementById('eventViewRepeatInfo'); 
 
	    title.textContent = event.title;
	    category.textContent = `[` + event.extendedProps.categoryName + `]`;
	 
	    const startDate = event.extendedProps.startDate;
	    const endDate = event.extendedProps.endDate;
	    const startTime = event.extendedProps.startTime;
	    const endTime = event.extendedProps.endTime;
	    if(startDate === endDate) {
	    	dateRange.textContent = `${startDate}`; 
		} else if(endDate == null) {
			dateRange.textContent = `${startDate} ${startTime} ~ ${endTime}`;
		} else {
			dateRange.textContent = `${startDate} ~ ${endDate}`; 
		}
	 
	    comment.textContent = event.extendedProps.comment; 
	    createdDate.textContent = event.extendedProps.createDate.substr(0,10) + ` 등록`; 
	    repeatInfo.textContent = getRepeatInfoText(event.extendedProps.repeatType, event.extendedProps.repeatDay, event.extendedProps.repeatWeek , event.extendedProps.repeatDate , event.extendedProps.repeatMonth);
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
 
    document.getElementById('closeScheduleModalBtn').addEventListener('click', function() {
        document.getElementById('eventViewModal').style.display = 'none';
    });
 	
	function formatDate(date) {
	    return date.getFullYear() + '-' + 
	           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
	           String(date.getDate()).padStart(2, '0');
	}
 
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

    function resetForm(form) {
        form.reset(); 
    }
    
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
				resetForm(createCompanyScheduleForm);
			}
		});
	});
	
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
				resetForm(createCompanyScheduleForm);
			}
		});
	});
	

	document.getElementById('eventDate').addEventListener('change', function() {
        const selectedDate = this.value;
        const endDateInput = document.getElementById('endDate');
        endDateInput.value = '';
        endDateInput.min = selectedDate; 
    });
    
	// 종일 체크박스 선택  
	document.getElementById('allDay').addEventListener('change', function() {
        const isChecked = this.checked;
        document.getElementById('timeGroup').style.display = isChecked ? 'none' : 'block';
        document.getElementById('endTimeGroup').style.display = isChecked ? 'none' : 'block';
        document.getElementById('endDateGroup').style.display = isChecked ? 'block' : 'none';
    });
	 
	// 카테고리 옵션
	$.ajax({
        url: '/categories', 
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
	    } else {
	        repeatEndGroup.style.display = 'none';  
	        repeatEndDate.value = '';   
	    }
	});
	  

	// 일정 등록 
	document.getElementById('eventForm').addEventListener('submit', function(event) {
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
	
	        if (endTime <= startTime) {
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
	    
	    if (repeatOption != 0 && !repeatEndDate) {
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
	        schedule_month: repeatMonth
	    };
	
	    console.log(eventData);  
	    
	    $.ajax({
	        type: "POST",
	        url: '/company/schedule/save',   
	        contentType: 'application/json',
	        data: JSON.stringify(eventData),
	        headers: {
	            'X-CSRF-TOKEN': csrfToken
	        },
	        success: function(response) {
	            Swal.fire({
	                text: '일정이 저장되었습니다.',
	                icon: 'success',
	                confirmButtonText: '확인',
	                confirmButtonColor: '#B1C2DD'
	            }).then(function() {
	                window.location.reload();  
	                document.getElementById('eventModal').style.display = 'none';
	            });
	        } 
	    }); 
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
	
	// 메시지
	function showAlert(message) {
	    Swal.fire({
	        text: message,
	        icon: 'warning',
	        confirmButtonColor: '#B1C2DD',
	        confirmButtonText: '확인',
	    });
	}
	
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



});