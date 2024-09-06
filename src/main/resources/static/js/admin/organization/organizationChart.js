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
            url: '/api/organization/chart',
            method: 'GET',
            success: function(data) {
                console.log('조직도 데이터:', data);
                $('#organization-chart').jstree({
                    'core': {
                        'data': data,
                        'themes': {
                            'icons': false,
                            'dots': false
                        }
                    },
                    'plugins': ['checkbox', 'types', 'search'],
                    'types': {
                        'default': {
                            'icon': 'fa fa-folder'
                        },
                        'file': {
                            'icon': 'fa fa-file'
                        }
                    },
                    checkbox: {
                        keep_selected_style: false
                    }
                }).on('ready.jstree', function (e, data) {
                    restoreSelection(data.instance);
                });

                // 체크박스 변경 시 선택된 사원 업데이트
                $('#organization-chart').on('changed.jstree', function (e, data) {
                    updateSelectedMembers(data.selected, data.instance);
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
        selectedMembersContainer.empty();

        const selectedNodes = instance.get_selected(true);
        selectedMembers = [];

        selectedNodes.forEach(function(node) {
            if (node.original.type === 'member') {
                const memberElement = $('<div class="selected-member"></div>');
                const memberName = $('<span></span>').text(node.text);
                const removeButton = $('<button class="remove-member">&times;</button>');

                memberElement.append(memberName).append(removeButton);
                selectedMembersContainer.append(memberElement);

                selectedMembers.push(node.text);

                removeButton.click(function() {
                    instance.uncheck_node(node);
                    memberElement.remove();
                    const index = selectedMembers.indexOf(node.text);
                    if (index !== -1) {
                        selectedMembers.splice(index, 1);
                    }
                    localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
                });
            }
        });

        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
    }

    // 확인 버튼 클릭 이벤트 핸들러
    $('#confirmButton').click(function() {
        const permissionPickList = $('.permission_pick_list');
        permissionPickList.empty(); // 기존 출력 내용 초기화

        // 선택된 사원 출력
        selectedMembers.forEach(function(member) {
            permissionPickList.append(`<div>${member}</div>`);
        });

        // 모달 닫기
        $('#organizationChartModal').modal('hide');
    });

    // 페이지 로드 시 선택된 사원 정보 로드
    loadSelectedMembers();
});
