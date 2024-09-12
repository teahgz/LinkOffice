document.addEventListener("DOMContentLoaded", function () {
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
        
        dateClick: function (info) {
            var selectedDate = info.dateStr;
            fetchReservations(selectedDate);
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

    // 회의실 전체 정보 
    $.ajax({
        url: '/api/meetings',
        type: 'GET',
        success: function(data) {
            meetings = data;
            renderMeetingRooms(meetings); 
            fetchReservations(today);
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
	        const row = $('<tr>').append(`<td>${meeting.meeting_name}</td>`);
	        
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
	                        .text(`${reservation.member_name} (${reservation.meeting_reservation_start_time} ~ ${reservation.meeting_reservation_end_time})`)
	                        .attr('title', `${reservation.member_name} (${reservation.meeting_reservation_start_time} ~ ${reservation.meeting_reservation_end_time})`); 
	
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



    // 예약 모달 표시
    function showReservationModal(meeting, date, startTime) {
        const modal = $('#reservationModal');
        const form = $('#reservationForm');
        
        form.find('#room').val(meeting.meetingName);
        form.find('#date').val(date);
        
        const startTimeSelect = form.find('#startTime').empty();
        const endTimeSelect = form.find('#endTime').empty();
        
        timeSlots.forEach(time => {
            if (time >= meeting.meetingAvailableStart && time < meeting.meetingAvailableEnd) {
                startTimeSelect.append(`<option value="${time}">${time}</option>`);
                endTimeSelect.append(`<option value="${time}">${time}</option>`);
            }
        });
        
        startTimeSelect.val(startTime);
        endTimeSelect.val(timeSlots[timeSlots.indexOf(startTime) + 1]);
        
        modal.css('display', 'block');
    }

    // 모달 닫기
    $('.close').on('click', function() {
        $('#reservationModal').css('display', 'none');
    }); 

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

    $("#schedule").on("click", "td.available", function () {
        var selectedCell = this;
        var modal = document.getElementById("reservationModal");
        modal.style.display = "block"; 
    });

    $("#reservationForm").submit(function (e) {
        e.preventDefault(); 
        modal.style.display = "none";
    });
});
