document.addEventListener("DOMContentLoaded", function () {
	let currentReservations = [];
	// 조직도
    let selectedMembers = [];  // 선택된 사원을 저장할 배열
    
    const date = new Date();
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    const today = `${year}-${month}-${day}`;
	document.getElementById('pick_date_text').innerText = formatDate(today);
	
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
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    
    // 현재 시간
    function getCurrentTime() {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        return `${hours}:${minutes}`;
    }
    
    // 기본 예약 버튼
    document.getElementById('openReservationModal').addEventListener('click', function() {
        openReservationModal();
    });

    function openReservationModal() { 
        $('#reservationModal').modal('show');
        pick_username.innerText = memberNameValue + " " + memberPositionValue; 
        resetReservationForm();
    }

    function resetReservationForm() { 
        $('#reservation_room').val('');
        $('#reservation_date').val('');
        $('#reservation_start_time').val('');
        $('#reservation_end_time').val('');
        $('#reservation_purpose').val('');
 
        $('#reservation_start_time').prop('disabled', true).empty().append('<option value="">시작 시간</option>');
        $('#reservation_end_time').prop('disabled', true).empty().append('<option value="">종료 시간</option>');
         
        $('#reservation_date').attr('min', today);
    } 
    
    // 예약 현황 예약 버튼
    function showReservationModal(meeting, date, startTime) { 
        $(pick_room).val(meeting.meeting_no).trigger('change'); 
        $(pick_date).val(date).trigger('change', date);
         
        pick_username.innerText = memberNameValue + " " + memberPositionValue;
        $(pick_start_time).val(startTime).trigger('change', startTime);   
        
    	fetchRoomReservations(date, meeting.meeting_no, function(filteredReservations) { 
            disableReservedTimes(filteredReservations);
            updateEndTimeOptions(startTime);  
        });
        
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
    function initializeCalendar(data) { 
	    const mergedEvents = {};
	
	    data.forEach(function(reservation) {
	        const date = reservation.meeting_reservation_date.split('T')[0]; 
	
	        if (!mergedEvents[date]) {
	            mergedEvents[date] = {
	                id: reservation.meeting_reservation_no,  
	                title: '📌',  
	                start: date,  
	                count: 1, 
	                extendedProps: {
	                    meeting_reservations: [],
	               		className : 'meeting_ping'
	                }
	            };
	        } else {
	            mergedEvents[date].count += 1; 
	        }
	 
	        mergedEvents[date].extendedProps.meeting_reservations.push(reservation);
	    });
	 
	    const events = Object.values(mergedEvents).map(event => ({
	        id: event.id,
	        title: `${event.title}`,  
	        backgroundColor: 'transparent',
	        borderColor: 'transparent',
	        textColor: '#000000',
	        start: event.start,
	        extendedProps: event.extendedProps
	    }));
	
	    calendar = new FullCalendar.Calendar(calendarEl, {
	        initialView: 'dayGridMonth',
	        locale: 'ko', 
	        buttonText: {
	            today: '오늘' 
	        },
	        validRange: {
	            start: today 
	        },
	        events: events,
	        dateClick: function(info) {
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
	        eventClick: function(info) {
	            const eventDate = new Date(info.event.start);
	            eventDate.setDate(eventDate.getDate() + 1); 
	            const formattedDate = eventDate.toISOString().split('T')[0]; 
	
	            fetchReservations(formattedDate); 
	            highlightSelectedDate(formattedDate); 
	        },
	        // '일' 삭제
	        dayCellContent: function(info) {
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
	        eventDidMount: function(info) { 
	            info.el.style.cursor = 'pointer'; 
	        }, 
	    });
	
	    calendar.render();
	}
	
	function highlightSelectedDate(date) {
	    document.querySelectorAll('.fc-daygrid-day').forEach(cell => {
	        cell.style.backgroundColor = ''; 
	    });
	    const selectedCell = document.querySelector(`[data-date="${date}"]`);
	    
	    if (selectedCell) {
	        selectedCell.style.backgroundColor = '#a6bef7'; 
	        document.getElementById('pick_date_text').innerText = formatDate(date); 
	    } 
	}

    $.ajax({
        url: '/api/meetings',
        type: 'GET',
        success: function(data) {
            meetings = data;
            renderMeetingRooms(meetings); 
            fetchReservations(today); 
            populateRoomSelect(data); 
            data.forEach(room => {
                roomSelect.append(`<option value="${room.meeting_no}">${room.meeting_name}</option>`);
            });
        } 
    });

    // 회의실 정보
    function renderMeetingRooms(meetings) {
        const tableBody = $('#room-info-table tbody');
        tableBody.html('');
        
        if (meetings.length === 0) {
	        tableBody.append('<tr><td colspan="3">회의실 정보가 존재하지 않습니다.</td></tr>');
	        return;
	    }
    
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
    
    // 전체 예약 내역
    $.ajax({
        url: '/all/reservations',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            initializeCalendar(data); 
        } 
    });
    
	// 예약 현황 테이블
	function updateReservationTable(reservations, date) {
		const currentTime = getCurrentTime(); 
		  
	    const table = $('#reservation-table table');
	    table.find('thead tr').html('<th>회의실명</th>');
	     
	    timeSlots.forEach(time => {
	        table.find('thead tr').append(`<th>${time}</th>`);
	    });
	
	    const tbody = table.find('tbody').empty();
	
		if (meetings.length === 0) {
	        tbody.append('<tr><td colspan="' + (timeSlots.length + 1) + '">회의실 정보가 존재하지 않습니다.</td></tr>');
	        return;
	    }
    
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
						if (date === today && time < currentTime) { 
							 cell.removeClass('available').addClass('past-time');
			            } 
			            else {
		                    cell.on('click', function() {
		                        showReservationModal(meeting, date, time);
		                    });
	                    }
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
        closeMeetingInfoModal();
    };

    // 정보 상세 모달 닫기
    function closeMeetingInfoModal() { 
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
            const date = $('#reservation_date').val();
            
            if (date) {
                populateTimeSelect(startTime, endTime, 'start');
                populateTimeSelect(startTime, endTime, 'end');
                
                fetchRoomReservations(date, $(this).val(), function(filteredReservations) {
                    currentReservations = filteredReservations;
                    disableReservedTimes(filteredReservations); 
                });
                 
                $('#reservation_start_time').prop('disabled', false);
            } else {
                $('#reservation_start_time').prop('disabled', true).empty().append('<option value="">시작 시간</option>');
                $('#reservation_end_time').prop('disabled', true).empty().append('<option value="">종료 시간</option>');
            }
        } else {
            $('#reservation_start_time').prop('disabled', true).empty().append('<option value="">시작 시간</option>');
            $('#reservation_end_time').prop('disabled', true).empty().append('<option value="">종료 시간</option>');
        }
    });
	
	// 날짜 선택
	document.getElementById("reservation_date").setAttribute('min', today);
	$('#reservation_date').on('change', function() {
        const roomId = $('#reservation_room').val();
        const date = $(this).val();
        
        if (roomId && date) {
            const startTime = $('#reservation_room').find(':selected').data('start');
            const endTime = $('#reservation_room').find(':selected').data('end');
            
            populateTimeSelect(startTime, endTime, 'start');
            populateTimeSelect(startTime, endTime, 'end');
            
            fetchRoomReservations(date, roomId, function(filteredReservations) {
                currentReservations = filteredReservations;
                disableReservedTimes(filteredReservations);  
            });
             
            $('#reservation_start_time').prop('disabled', false);
        } else {
            $('#reservation_start_time').prop('disabled', true).empty().append('<option value="">시작 시간</option>');
            $('#reservation_end_time').prop('disabled', true).empty().append('<option value="">종료 시간</option>');
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
        const selectedDate = $('#reservation_date').val();
        const currentTime = getCurrentTime();
        
        select.empty().append(`<option value="">${type === 'start' ? '시작 시간' : '종료 시간'}</option>`);
        
        timeSlots.forEach(time => {
            let shouldDisable = false;
            if (selectedDate === today && time <= currentTime) {
                shouldDisable = true;
            }
            
            if (type === 'start' && time >= startTime && time < endTime) {
                select.append(`<option value="${time}" ${shouldDisable ? 'disabled' : ''}>${time}</option>`);
            } else if (type === 'end' && time > startTime && time <= endTime) {
                select.append(`<option value="${time}" ${shouldDisable ? 'disabled' : ''}>${time}</option>`);
            }
        });
    }
     
    // 예약된 시간 비활성화  
    function disableReservedTimes(reservations) {
        const startSelect = $('#reservation_start_time');
        const selectedDate = $('#reservation_date').val();
        const currentTime = getCurrentTime();
        
        startSelect.find('option').prop('disabled', false);
        
        if (selectedDate === today) {
            startSelect.find('option').filter(function() {
                return $(this).val() <= currentTime;
            }).prop('disabled', true);
        }
        
        reservations.forEach(reservation => {
            const start = reservation.meeting_reservation_start_time;
            const end = reservation.meeting_reservation_end_time;
            
            startSelect.find('option').filter(function() {
                return $(this).val() >= start && $(this).val() < end;
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
    $('.reservation_close').on('click', function () {
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
	 
	    $('#organization-chart').jstree("uncheck_all");
	 
	    const reservationArea = $('.selected-participants-container');  
	    reservationArea.find('.selected-participants').remove(); 
	    
	    selectedMembers = [];
	    localStorage.removeItem('selectedMembers');  
	    $('#selectedMembers').val('');
	}
 
	// 조직도 열기
    $('#openOrganizationChartButton').on('click', function() {
        openOrganizationChartModal();
    });
	
	function openOrganizationChartModal() {
	    selectedMembers = [];   
	
	    $('#organizationChartModal').modal('show');
	
	    loadOrganizationChart();
	}

    // 조직도 로딩
    function loadOrganizationChart() {
        $.ajax({
            url: '/meeting/chart',
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
                    restoreSelection(data.instance);
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
	 });
	 
	 
   // 예약 등록 
   $("#reservationForm").submit(function (e) {
    e.preventDefault();

    var reservationRoom = $('#reservation_room').val();
    var reservationDate = $('#reservation_date').val();
    var reservationStartTime = $('#reservation_start_time').val();
    var reservationEndTime = $('#reservation_end_time').val();
    var reservationPurpose = $('#reservation_purpose').val();
 
    if (!reservationRoom) {
        Swal.fire({
            text: '회의실을 선택해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }

    if (!reservationDate) {
        Swal.fire({
            text: '예약일을 선택해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }

    if (!reservationStartTime) {
        Swal.fire({
            text: '시작 시간을 선택해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }

    if (!reservationEndTime) {
        Swal.fire({
            text: '종료 시간을 선택해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }

    if (reservationEndTime <= reservationStartTime) {
        Swal.fire({
            text: '종료 시간은 시작 시간 이후로 설정해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }

    if (!reservationPurpose) {
        Swal.fire({
            text: '예약 목적을 입력해 주세요.',
            icon: 'warning',
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: '확인'
        });
        return;
    }
 
    var formData = new FormData(this);

    $.ajax({
        url: '/reservation/save',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
        success: function (response) {
            if (response.res_code === "200") {
                Swal.fire({
                    text: response.res_msg,
                    icon: 'success',
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: '확인'
                }).then(() => {
                    location.reload();
                });
            } else {
                Swal.fire({
                    text: response.res_msg,
                    icon: 'error',
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: '확인'
                });
            }
            $('#reservationModal').modal('hide');
        } 
    });
});

const location_text = document.getElementById('header_location_text');
location_text.innerHTML = '회의실&emsp;&gt;&emsp;회의실 예약';


});
