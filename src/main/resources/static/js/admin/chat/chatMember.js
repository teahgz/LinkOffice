document.addEventListener("DOMContentLoaded", function () {
    let selectedMembers = [];  // 선택된 사원을 저장할 배열
    let selectNames = [];
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

    document.getElementById('openChart').addEventListener('click', function() {
        $('#organizationChartModal').modal('show');
        loadOrganizationChart();
    });

    // 조직도 로딩
    function loadOrganizationChart() {
        fetch('/organizationChart/chart')
            .then(response => response.json())
            .then(data => {
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
            })
            .catch(error => {
                console.error('조직도 로딩 오류:', error);
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
        selectNames = [];

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
                selectNames.push(node.text);

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
    document.getElementById('confirmButton').addEventListener('click', function() {
        const currentMemberNo = document.getElementById("currentMemberNo").value;
        var csrfToken = document.querySelector('input[name="_csrf"]').value;
        const currentMemberName = document.getElementById("currentMemberName").value;


        fetch('/api/chat/memberAddRoom', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                members: selectedMembers,
                currentMemberNo: currentMemberNo,
                names : selectNames,
                currentMemberName:currentMemberName
            })
        })
        .then(response => response.json())
        .then(data => {
           console.log('Server Response:', data); // 서버 응답 확인

                  if (data.res_code === '200') {
                      Swal.fire({
                          icon: 'success',
                          title: '성공',
                          text: '선택한 사원이 저장되었습니다.',
                          confirmButtonText: '확인'
                      }).then((result) => {
                          if (result.isConfirmed) {
                              $('#organizationChartModal').modal('hide'); // 모달을 닫습니다
                              localStorage.removeItem('selectedMembers'); // 로컬 스토리지에서 선택한 사원 제거
                              $('.permission_pick_list').empty();
                              window.location.href = '/'; // 새로운 페이지로 리다이렉트
                          }
                      });
                  } else {
                      Swal.fire({
                          icon: 'error',
                          title: '오류',
                          text: '선택한 사원을 저장하는데 문제가 발생했습니다.',
                          confirmButtonText: '확인'
                      }).then((result) => {
                          if (result.isConfirmed) {
                              window.location.href = '/errorPage'; // 오류 페이지로 리다이렉트
                          }
                      });
                  }
        })
        .catch(error => {
            console.error('선택한 사원 저장 오류:', error);
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '선택한 사원을 저장하는데 오류가 발생했습니다.',
                confirmButtonText: '확인'
            });
        });
    });

    loadSelectedMembers();


});
