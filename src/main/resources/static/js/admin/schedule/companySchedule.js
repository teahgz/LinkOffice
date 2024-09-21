document.addEventListener('DOMContentLoaded', function() {
	var csrfToken = document.querySelector('input[name="_csrf"]').value;
	var createCompanyScheduleForm = document.getElementById("eventForm");
	const startInput = document.getElementById('startTime');
    const endInput = document.getElementById('endTime'); 
	
	var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		initialView: 'dayGridMonth',
		locale: 'ko',
		headerToolbar: {
			left: 'prev,next today',
			center: 'title',
			right: 'dayGridMonth,timeGridWeek,timeGridDay'
		},
		googleCalendarApiKey: 'AIzaSyBaQi-ZLyv7aiwEC6Ca3C19FE505Xq2Ytw',
		events: {
			googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
			color: 'transparent',
			textColor: 'red'
		},
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
	    const repeatEndCheckbox = document.getElementById('repeatEndCheckbox');
	    const repeatEndDate = document.getElementById('repeatEndDate');
	
	    if (this.value != 0) {
	        repeatEndGroup.style.display = 'block';
	    } else {
	        repeatEndGroup.style.display = 'none';
	        repeatEndCheckbox.checked = false;
	        repeatEndDate.disabled = true;
	        repeatEndDate.value = '';   
	    }
	});
	 
	document.getElementById('repeatEndCheckbox').addEventListener('change', function() {
	    const repeatEndDate = document.getElementById('repeatEndDate');
	    
	    if (this.checked) {
	        repeatEndDate.disabled = false;
	    } else {
	        repeatEndDate.disabled = true;
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
	    const repeatEnd = document.getElementById('repeatEndCheckbox').checked;
	    const repeatEndDate = repeatEnd 
	                          ? document.getElementById('repeatEndDate').value 
	                          : null;
	
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
	    
	    if (repeatEnd && !repeatEndDate) {
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