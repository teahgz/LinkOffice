document.addEventListener("DOMContentLoaded", function () {
	let currentReservations = [];
	
	const memberNoInput = document.getElementById("memberNo"); 
    const memberNoValue = memberNoInput.value;
    const memberNameInput = document.getElementById("memberName"); 
    const memberNameValue = memberNameInput.value; 
    const memberPositionInput = document.getElementById("memberPosition"); 
    const memberPositionValue = memberPositionInput.value; 

    const modal = $('#reservationModal');
    const reservation_form = $('#reservationForm'); 
    const pick_room = document.getElementById("reservation_room");	  
    const pick_date = document.getElementById("reservation_date");	  
    const pick_username = document.getElementById("reservation_name");
    const pick_start_time = document.getElementById('reservation_start_time');  
    
    function showReservationModal(meeting, date, startTime) {
		console.log(startTime);
        $(pick_room).val(meeting.meeting_no).trigger('change'); 
        $(pick_date).val(date).trigger('change', date);
         
        pick_username.innerText = memberNameValue + " " + memberPositionValue;
        $(pick_start_time).val(startTime).trigger('change', startTime);   
       
        modal.modal('show');
    }
    
	function formatDate(selectedDate) { 
	    const [year, month, day] = selectedDate.split('-');
	     
	    return `${year}년 ${parseInt(month)}월 ${parseInt(day)}일`;
	} 

	let selectedDate = null;
	
	// 예약 현황 시간대
    let meetings = [];
    const timeSlots = [];
    for (let hour = 7; hour < 23; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            timeSlots.push(`${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`);
        }
    }
    timeSlots.push('23:00');

    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko', 
        buttonText: {
            today: '오늘' 
        },
        dateClick: function (info) {
            selectedDate = info.dateStr;  
            fetchReservations(selectedDate);
            
            document.querySelectorAll('.fc-daygrid-day').forEach(cell => {
                cell.style.backgroundColor = ''; 
            });
            const selectedCell = document.querySelector(`[data-date="${selectedDate}"]`);
            
            if (selectedCell) {
                selectedCell.style.backgroundColor = '#a6bef7';
                document.getElementById('pick_date_text').innerText = '';
                document.getElementById('pick_date_text').innerText = formatDate(selectedDate);
            } 
        },
        // '일' 삭제
        dayCellContent: function (info) {
		    var number = document.createElement("a");
		    number.classList.add("fc-daygrid-day-number");
		    number.innerHTML = info.dayNumberText.replace("일", '').replace("日","");
		    if (info.view.type === "dayGridMonth") {
		      return {
		        html: number.outerHTML
		      };
		    }
		    return {
		      domNodes: []
		    };
		},
    });
    calendar.render();

    const date = new Date();
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    const today = `${year}-${month}-${day}`;
	document.getElementById('pick_date_text').innerText = formatDate(today);
	
    // 회의실 전체 정보
    $.ajax({
        url: '/api/meetings',
        type: 'GET',
        success: function(data) {
            meetings = data;
            renderMeetingRooms(meetings); 
            fetchReservations(today);
            console.log(meetings);
            populateRoomSelect(data);
            const roomSelect = $('#reservation_room');
            data.forEach(room => {
                roomSelect.append(`<option value="${room.meeting_no}">${room.meeting_name}</option>`);
            });
        } 
    });

    // 회의실 정보
    function renderMeetingRooms(meetings) {
        const tableBody = $('#room-info-table tbody');
        tableBody.html('');
        meetings.forEach(meeting => {
            tableBody.append(`
                <tr onclick="fetchMeetingDetails(${meeting.meeting_no})" data-id="${meeting.meeting_no}">
                    <td>${meeting.meeting_name}</td>
                    <td>${meeting.meeting_max}</td>
                    <td>${meeting.meeting_available_start} ~ ${meeting.meeting_available_end}</td>
                </tr>
            `);
        });
    }

    // 예약 정보  
    function fetchReservations(date) {
        $.ajax({
            url: '/date/reservations',
            type: 'GET',
            data: { date: date },
            success: function(reservations) {  
                updateReservationTable(reservations, date);
            } 
        });
    }
	
	// 예약 현황 테이블
	function updateReservationTable(reservations, date) {
	    const table = $('#reservation-table table');
	    table.find('thead tr').html('<th>회의실명</th>');
	     
	    timeSlots.forEach(time => {
	        table.find('thead tr').append(`<th>${time}</th>`);
	    });
	
	    const tbody = table.find('tbody').empty();
	
	    meetings.forEach(meeting => { 
	        const row = $('<tr>').append(`
			    <td onclick="fetchMeetingDetails(${meeting.meeting_no})" data-id="${meeting.meeting_no}">
			        ${meeting.meeting_name}
			    </td>
			`);

	        
	        let skipCells = 0;  
	        timeSlots.forEach((time, index) => {
	            if (skipCells > 0) { 
	                skipCells--;
	                return;
	            }
	
	            const cell = $('<td>').addClass('available');
	
	            if (time < meeting.meeting_available_start || time >= meeting.meeting_available_end) {
	                cell.removeClass('available').addClass('unavailable');
	                row.append(cell);
	            } else {
	                const reservation = reservations.find(r => 
	                    r.meeting_no === meeting.meeting_no &&
	                    time >= r.meeting_reservation_start_time &&
	                    time < r.meeting_reservation_end_time
	                );
	
	                if (reservation) { 
						console.log(reservation);
	                    const startTimeIndex = timeSlots.indexOf(reservation.meeting_reservation_start_time);
	                    const endTimeIndex = timeSlots.indexOf(reservation.meeting_reservation_end_time);
	                    const colspan = endTimeIndex - startTimeIndex + 1; 
	                    
	                    const reservationCell = $('<td>')
	                        .attr('colspan', colspan) 
	                        .addClass('reserved') 
	                        .text(`${reservation.member_name} ${reservation.position_name} (${reservation.meeting_reservation_start_time} ~ ${reservation.meeting_reservation_end_time})`)
	                        .attr('title', `${reservation.member_name} ${reservation.position_name} (${reservation.meeting_reservation_start_time} ~ ${reservation.meeting_reservation_end_time})`); 
	
	                    row.append(reservationCell);
	                    skipCells = colspan - 1;  
	                } else { 
	                    cell.on('click', function() {
	                        showReservationModal(meeting, date, time);
	                    });
	                    row.append(cell);
	                }
	            }
	        });
	
	        tbody.append(row);
	    });
	}
 
 	// 회의실 세부 정보 
    window.fetchMeetingDetails = function(meetingNo) { 
        $.ajax({
            url: `/api/meetings/${meetingNo}`,  
            type: 'GET',
            success: function(data) {  
                showModalWithDetails(data);
            } 
        });
    };

    // 정보 상세 모달
    function showModalWithDetails(meeting) {
        document.getElementById('roomName').innerText = meeting.meeting_name;
        document.getElementById('roomCapacity').innerText = meeting.meeting_max + '명';
        document.getElementById('roomTime').innerText = meeting.meeting_available_start + ' ~ ' + meeting.meeting_available_end;
        document.getElementById('roomDescription').innerText = meeting.meeting_comment;
        document.getElementById('roomImage').src = `/linkOfficeImg/meeting/${meeting.meeting_new_image}`;
        
        var modal = document.getElementById('info_meetingroom_modal');
        modal.style.display = 'block';
    }

    document.getElementById('info_close_btn').onclick = function () {
        closeInfoModal();
    };

    // 정보 상세 모달 닫기
    function closeInfoModal() {
        var modal = document.getElementById('info_meetingroom_modal');
        modal.style.display = 'none';
    }
    
    
    // 예약 모달
    // 회의실 선택 옵션  
    function populateRoomSelect(rooms) {
        const roomSelect = $('#reservation_room');
        roomSelect.empty().append('<option value="">회의실 선택</option>');
        rooms.forEach(room => {
            roomSelect.append(`<option value="${room.meeting_no}" data-start="${room.meeting_available_start}" data-end="${room.meeting_available_end}">${room.meeting_name}</option>`);
        });
    }

    // 회의실 선택 
	$('#reservation_room').on('change', function() {
        const selectedRoom = $(this).find(':selected');
        const startTime = selectedRoom.data('start');
        const endTime = selectedRoom.data('end');
        
        if (startTime && endTime) {
            populateTimeSelect(startTime, endTime, 'start');
            populateTimeSelect(startTime, endTime, 'end');
             
            const date = $('#reservation_date').val();
            fetchRoomReservations(date, $(this).val(), function(filteredReservations) {
                currentReservations = filteredReservations;
                disableReservedTimes(filteredReservations); 
            });
        } else {
            $('#reservation_start_time, #reservation_end_time').prop('disabled', true);
        }
    });
	
	// 날짜 선택
	$('#reservation_date').on('change', function() {
        const roomId = $('#reservation_room').val();
        const date = $(this).val();
        
        if (roomId) {
            const startTime = $('#reservation_room').find(':selected').data('start');
            const endTime = $('#reservation_room').find(':selected').data('end');
            
            populateTimeSelect(startTime, endTime, 'start');
            populateTimeSelect(startTime, endTime, 'end');
            
            fetchRoomReservations(date, roomId, function(filteredReservations) {
                currentReservations = filteredReservations;
                disableReservedTimes(filteredReservations);  
            });
        } else {
            $('#reservation_start_time, #reservation_end_time').prop('disabled', true);
        }
    });

    // 시작 시간 선택 
    $('#reservation_start_time').on('change', function() {
        const startTime = $(this).val(); 
        if (startTime) {
            const roomEndTime = $('#reservation_room').find(':selected').data('end');
            populateTimeSelect(startTime, roomEndTime, 'end');
            updateEndTimeOptions(startTime);
            $('#reservation_end_time').prop('disabled', false); 
        } else {
            $('#reservation_end_time').prop('disabled', true).html('<option value="">종료 시간</option>'); 
        }
    });
    
    // 시간 선택 옵션 생성 
    function populateTimeSelect(startTime, endTime, type) {
        const select = type === 'start' ? $('#reservation_start_time') : $('#reservation_end_time');
        
        select.empty().append(`<option value="">${type === 'start' ? '시작 시간' : '종료 시간'}</option>`);
        
        timeSlots.forEach(time => {
            if (type === 'start' && time >= startTime && time < endTime) {
                select.append(`<option value="${time}">${time}</option>`);
            } else if (type === 'end' && time > startTime && time <= endTime) {
                select.append(`<option value="${time}">${time}</option>`);
            }
        });
    }
     
    // 예약된 시간 비활성화  
    function disableReservedTimes(reservations) {
        const startSelect = $('#reservation_start_time');
        
        startSelect.find('option').prop('disabled', false);
        
        reservations.forEach(reservation => {
            const start = reservation.meeting_reservation_start_time;
            const end = reservation.meeting_reservation_end_time;
            
            startSelect.find('option').filter(function() {
                return $(this).val() >= start && $(this).val() <= end;
            }).prop('disabled', true);
        });
    }

	// 종료 시간 옵션 업데이트  
     function updateEndTimeOptions(startTime) {
        const endSelect = $('#reservation_end_time');
        
        endSelect.find('option').prop('disabled', false);

        let nextReservationStart = null;
        currentReservations.forEach(reservation => {
            if (reservation.meeting_reservation_start_time > startTime && 
                (!nextReservationStart || reservation.meeting_reservation_start_time < nextReservationStart)) {
                nextReservationStart = reservation.meeting_reservation_start_time;
            }
        });

        if (nextReservationStart) {
            endSelect.find('option').filter(function() {
                return $(this).val() >= nextReservationStart;
            }).prop('disabled', true);
        }
    }
     
     
 	// 특정 회의실의 예약 정보 
	function fetchRoomReservations(date, roomId, callback) {
	    $.ajax({
	        url: '/date/reservations',
	        type: 'GET',
	        data: { date: date },
	        success: function(reservations) {
	            const filteredReservations = [];
	
	            for (var i = 0; i < reservations.length; i++) {
	                if ((reservations[i].meeting_reservation_date === date) && 
	                    (reservations[i].meeting_no == roomId)) {
	                    filteredReservations.push(reservations[i]);
	                }
	            } 
	            callback(filteredReservations);
	        } 
	    });
	} 
     

    // 모달 닫기  
    $('#reservation_close').on('click', function () {
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
	            resetReservationModal();
	        }
        });
    });
	    
	function resetReservationModal() {
	    reservation_form[0].reset();  
	    $('#reservationModal').modal('hide');  
	}

    $("#reservationForm").submit(function (e) {
        e.preventDefault(); 
        modal.style.display = "none";
    });
});
