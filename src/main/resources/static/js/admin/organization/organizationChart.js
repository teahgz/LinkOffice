document.addEventListener("DOMContentLoaded", function () {
    let selectedMembers = [];  // 선택된 사원을 저장할 배열

    // 페이지 로드 시 이전 선택된 사원 로드
    function loadSelectedMembers() {
        const savedMembers = localStorage.getItem('selectedMembers');
        if (savedMembers) {
            selectedMembers = JSON.parse(savedMembers);
        }
    }

    // 페이지 로드 시 기존 선택 상태를 복원
    function restoreSelection(instance) {
        selectedMembers.forEach(memberName => {
            const node = instance.get_node(instance.get_container().find(`:contains('${memberName}')`).attr('id'));
            if (node) {
                instance.check_node(node);
            }
        });
    }

    $('#openChart').click(function() {
        $('#organizationChartModal').modal('show');
        loadOrganizationChart();
    });

    // 조직도 로딩
    function loadOrganizationChart() {
        $.ajax({
            url: '/organizationChart/chart',
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
	    selectedMembers = [];
	
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
	
	    localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
}

    // 확인 버튼 
	$(document).ready(function() {
	    $("#reservationForm").submit(function(e) {
	        e.preventDefault();
	        
	        var formData = new FormData(this);
	        
	        // 선택된 참여자 정보 추가
	        var selectedMembersString = $("#selectedMembers").val();
	        formData.append("selectedMembers", selectedMembersString);
	        
	        $.ajax({
	            url: '/api/reservation/save',
	            type: 'POST',
	            data: formData,
	            processData: false,
	            contentType: false,
	            success: function(response) {
	                console.log('예약이 성공적으로 저장되었습니다:', response);
	                alert('예약이 성공적으로 저장되었습니다.');
	                $('#reservationModal').modal('hide');
	            },
	            error: function(xhr, status, error) {
	                console.error('예약 저장 중 오류 발생:', error);
	                alert('예약을 저장하는 중 오류가 발생했습니다.');
	            }
	        });
	    });
	});

    loadSelectedMembers();
});
