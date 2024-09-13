document.addEventListener("DOMContentLoaded", function () {
    let selectedMembers = [];  // 선택된 사원을 저장할 배열
	
	
	let approvers = [];  // 결재자 배열
    let references = []; // 참조자 배열
    let reviewers = [];  // 검토자 배열
    // 페이지 로드 시 이전 선택된 사원 로드
    function loadSelectedMembers() {
        const savedMembers = localStorage.getItem('selectedMembers');
        if (savedMembers) {
            selectedMembers = JSON.parse(savedMembers);
        }
    }

    // 페이지 로드 시 기존 선택 상태를 복원
    function restoreSelection(instance) {
        selectedMembers.forEach(memberId => {
            const node = instance.get_node(memberId);
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
        const selectedNodes = instance.get_selected(true);
        selectedMembers = [];

        selectedNodes.forEach(function(node) {
            if (node.original.type === 'member') {
                const memberId = node.id;  
                const memberNumber = memberId.replace('member_', ''); // 사원 번호
                selectedMembers.push(memberNumber);
            }
        });
      console.log(selectedMembers);
        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
    }

$(document).ready(function() {
   approvers = [];  // 결재자 배열
    references = []; // 참조자 배열
    reviewers = [];  // 검토자 배열
    
    // 오른쪽 화살표 버튼 클릭 시 사원 이동
    function moveToList(targetId, array) {
        event.preventDefault();

        const selectedNodes = $('#organization-chart').jstree(true).get_selected(true);

        selectedNodes.forEach(function(node) {
            if (node.original.type === 'member') {
                addMemberToBox(node, targetId, array);
                $('#organization-chart').jstree(true).disable_node(node);

                const memberId = node.id;
                const memberNumber = memberId.replace('member_', '');
                array.push(memberNumber);

                console.log(`${targetId} 배열:`, array);
            }
        });
    }

    // 왼쪽 화살표 버튼 클릭 시 사원 제거
    function moveFromList(targetId, array) {
        event.preventDefault();

        $(`#${targetId} .selected-member`).each(function() {
            const memberName = $(this).find('span').text();
            const memberNumber = $(this).data('member-number');

            // 배열에서 제거
            const index = array.indexOf(memberNumber);
            if (index > -1) {
                array.splice(index, 1);
            }

            console.log(`${targetId} 배열:`, array);

            // 화면에서 제거
            $(this).remove();
        });
    }

    // 사원 항목을 추가하는 함수
    function addMemberToBox(node, boxId, array) {
        const memberId = node.id;
        const memberNumber = memberId.replace('member_', '');
        const memberElement = $('<div class="selected-member"></div>');
        const memberName = $('<span></span>').text(node.text).data('member-number', memberNumber);
        const removeButton = $('<button class="remove-member">&times;</button>');

        memberElement.append(memberName).append(removeButton);
        $(`#${boxId}`).append(memberElement);

        // X 버튼 클릭 시 사원 제거
        removeButton.click(function() {
            const memberNumber = $(this).prev('span').data('member-number');

            // 배열에서 제거
            const index = array.indexOf(memberNumber);
            if (index > -1) {
                array.splice(index, 1);
            }

            console.log(`${boxId} 배열:`, array);

            // 화면에서 제거
            memberElement.remove();
        });
    }

    // 결재자 이동
    $('.move-to-approver').click(function(event) {
        moveToList('approver-list', approvers);
    });

    $('.move-from-approver').click(function(event) {
        moveFromList('approver-list', approvers);
    });

    // 참조자 이동
    $('.move-to-reference').click(function(event) {
        moveToList('reference-list', references);
    });

    $('.move-from-reference').click(function(event) {
        moveFromList('reference-list', references);
    });

    // 검토자 이동
    $('.move-to-reviewer').click(function(event) {
        moveToList('reviewer-list', reviewers);
    });

    $('.move-from-reviewer').click(function(event) {
        moveFromList('reviewer-list', reviewers);
    });
});





















    // 목록에 사원 추가 함수
    function addMemberToBox(node, boxId) {
        const memberId = node.id;  
        const memberNumber = memberId.replace('member_', ''); // 사원 번호
        const memberElement = $('<div class="selected-member"></div>');
        const memberName = $('<span></span>').text(node.text);
        const removeButton = $('<button class="remove-member">&times;</button>');

        memberElement.append(memberName).append(removeButton);
        $(`#${boxId}`).append(memberElement);

        removeButton.click(function() {
            $('#organization-chart').jstree(true).check_node(node);
            memberElement.remove();
        });

        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
    }

    // 확인 버튼 
    $('#confirmButton').click(function(event) {
        event.preventDefault();  // 폼 제출 방지
        console.log("결재:", approvers);
        console.log("합의:", references);
        console.log("참조:", reviewers);
        alert("결재 사원: " + approvers.join(", "));
        alert("합의 사원: " + references.join(", "));
        alert("참조 사원: " + reviewers.join(", "));

        var csrfToken = document.querySelector('input[name="_csrf"]').value; 

        $.ajax({
            url: '/api/organization/saveSelectedMembers',
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            contentType: 'application/json',
            data: JSON.stringify({ members: selectedMembers }),
            success: function(response) {
                console.log('선택한 사원 저장 성공:', response);
                alert('선택한 사원이 저장되었습니다.');
                
                $('#organizationChartModal').modal('hide');

                localStorage.removeItem('selectedMembers');
                
                $('.permission_pick_list').empty();
            },
            error: function(xhr, status, error) {
                console.error('선택한 사원 저장 오류:', error);
                alert('선택한 사원을 저장하는데 오류가 발생했습니다.');
            }
        });
    });

    loadSelectedMembers();
});
