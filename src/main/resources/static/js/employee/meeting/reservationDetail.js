$(document).ready(function() { 
    $('#editReservationButton').on('click', function() { 
	    const reservationNo = $(this).data('reservation-no');
	    console.log(reservationNo);
	    
	    openReservationModal(reservationNo); 
	});
	
	// 수정 모달
	function openReservationModal(reservationNo) {
        var modal = document.getElementById('reservationModal');
        modal.style.display = 'block';
        currentReservationNo = reservationNo;
        
        $.ajax({
            url: '/employee/meeting/reservation/detail/modal/' + reservationNo,
            type: 'GET',
            dataType: 'json',
            success: function(data) {  
                console.log(data);
                
                $('#reservationId').val(data.reservation.meeting_reservation_no);
                $('#reservation_date').val(data.reservation.meeting_reservation_date);
                $('#reservation_purpose').val(data.reservation.meeting_reservation_purpose);
            
                let selectedParticipants = data.participants.map(p => `${p.departmentName} ${p.memberName} ${p.positionName}`).join('<br/>');
                $('.selected-participants-container').html(selectedParticipants);
                
                fetchMeetings(function() {
                    $('#reservation_room').val(data.reservation.meeting_no);
                    
                    const selectedRoom = $('#reservation_room').find(':selected');
                    const startTime = selectedRoom.data('start');
                    const endTime = selectedRoom.data('end');
                    
                    if (startTime && endTime) {
                        populateTimeSelect(startTime, endTime, 'start');
                        
                        $('#reservation_start_time').val(data.reservation.meeting_reservation_start_time);
                        
                        fetchRoomReservations(data.reservation.meeting_reservation_date, data.reservation.meeting_no, function(filteredReservations) {
                            currentReservations = filteredReservations;
                            disableReservedTimes(filteredReservations);
                             
                            updateEndTimeOptions(data.reservation.meeting_reservation_start_time);
                            $('#reservation_end_time').val(data.reservation.meeting_reservation_end_time);
                        });
                    }
                });
            } 
        });  
    }

    // 수정 모달 닫기
    $('#reservation_close').on('click', function() {
        $('#reservationModal').hide();
    });

    // 회의실 전체 정보
    function fetchMeetings(callback) {
        $.ajax({
            url: '/api/meetings',
            type: 'GET',
            success: function(data) {
                meetings = data; 
                populateRoomSelect(meetings);  
                if (callback) callback();
            } 
        });
    }
   
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
    const today = new Date().toISOString().split('T')[0];
    $('#reservation_date').attr('min', today);
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
            updateEndTimeOptions(startTime);
            $('#reservation_end_time').prop('disabled', false); 
        } else {
            $('#reservation_end_time').prop('disabled', true).html('<option value="">종료 시간</option>'); 
        }
    });
     
    let meetings = [];
    const timeSlots = [];
    for (let hour = 7; hour < 23; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            timeSlots.push(`${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`);
        }
    }
    timeSlots.push('23:00');
    
    // 시간 선택 옵션 
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
            if (reservation.meeting_reservation_no != currentReservationNo) {
                const start = reservation.meeting_reservation_start_time;
                const end = reservation.meeting_reservation_end_time;
                
                startSelect.find('option').filter(function() {
                    return $(this).val() >= start && $(this).val() <= end;
                }).prop('disabled', true);
            }
        });
    }

    // 종료 시간 옵션 업데이트  
    function updateEndTimeOptions(startTime) {
        const endSelect = $('#reservation_end_time');
        const roomEndTime = $('#reservation_room').find(':selected').data('end');
        
        endSelect.empty().append('<option value="">종료 시간</option>');

        let nextReservationStart = null;
        currentReservations.forEach(reservation => {
            if (reservation.meeting_reservation_no != currentReservationNo &&
                reservation.meeting_reservation_start_time > startTime &&
                (!nextReservationStart || reservation.meeting_reservation_start_time < nextReservationStart)) {
                nextReservationStart = reservation.meeting_reservation_start_time;
            }
        });

        timeSlots.forEach(time => {
            if (time > startTime && time <= roomEndTime) {
                const option = $(`<option value="${time}">${time}</option>`);
                
                if (nextReservationStart && time >= nextReservationStart) {
                    option.prop('disabled', true);
                }

                endSelect.append(option);
            }
        });
    }
    
    // 특정 회의실 예약 정보 
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
    
    $('#openOrganizationChartButton').on('click', function() {
        openOrganizationChartModal();
    });
	
	function openOrganizationChartModal() {
	    selectedMembers = [];  
	 
	    const displayElement = document.getElementById('selected-members');
	    if (displayElement) {
	        displayElement.innerHTML = '';
	    }
	
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
                    },
                    'checkbox': {
                    	tie_selection: false,
	                    whole_node: true,
	                    three_state: true
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
	    const reservationArea = $('.reservation_participate');  
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
});
