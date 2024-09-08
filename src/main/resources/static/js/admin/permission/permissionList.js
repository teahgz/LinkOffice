let selectedMembers = [];
let selectedMenuNo = null;
let assignedMemberNos = []; 

const defaultMenuNo = 2; 
const defaultPermissionLink = document.querySelector(`.permission_Lists a[data-id="${defaultMenuNo}"]`);

if (defaultPermissionLink) {
    fetchPermissionMembers(defaultPermissionLink);
}

function fetchPermissionMembers(element) {
    selectedMenuNo = element.getAttribute('data-id');
    const menuNo = selectedMenuNo;
    const functionName = element.textContent;
    const sectionTitle = `${functionName} 권한자 목록`;
    document.getElementById('sectionTitle').textContent = sectionTitle; 

    $.ajax({
        url: `/permission/members?menuNo=${menuNo}`,
        method: 'GET',
        success: function(data) {
            const memberListTableBody = document.getElementById('memberList').getElementsByTagName('tbody')[0];
            memberListTableBody.innerHTML = ''; 
            selectedMembers = []; 

            if (data.length === 0) {
                const row = memberListTableBody.insertRow();
                row.insertCell().colSpan = 4;
                row.cells[0].textContent = '등록된 권한자가 없습니다.';
            } else {
                data.forEach(member => {
                    const row = memberListTableBody.insertRow();
                    const checkboxCell = row.insertCell();
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.className = 'member-checkbox';
                    checkbox.dataset.memberNo = member[0];
                    checkboxCell.appendChild(checkbox);
                    
                    row.insertCell().textContent = member[2]; // 부서
                    row.insertCell().textContent = `${member[1]} ${member[3]}`; // 사원명 + 직위명
                    row.insertCell().textContent = new Date(member[4]).toLocaleDateString(); // 권한 등록일
                });

                updateDeleteButtonState();
            }

            if ($('#organization-chart').jstree(true)) {
                updateOrganizationChartCheckboxes();
            }
        },
        error: function(xhr, status, error) {
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '권한자 목록을 가져오는 중 오류가 발생했습니다.',
                confirmButtonText: '닫기'
            });
        }
    });
} 

function openOrganizationChartModal() {
    selectedMembers = []; // 선택된 멤버 목록 초기화
 
    const displayElement = document.getElementById('selected-members');
    if (displayElement) {
        displayElement.innerHTML = '';
    }

    $('#organizationChartModal').modal('show');

    loadOrganizationChart();
}

function loadOrganizationChart() {
    $.ajax({
        url: '/permission/chart',
        method: 'GET',
        data: {
            selectedMemberNos: selectedMembers.map(member => member.no),
            menuNo: selectedMenuNo
        },
        success: function(data) {
            $('#organization-chart').jstree('destroy');
            $('#organization-chart').jstree({ 
                'core': {
                    'data': data.chartData,
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
                updateOrganizationChartCheckboxes(data.instance);
                $.ajax({
                    url: '/permission/assigned-members',
                    method: 'GET',
                    data: { menuNo: selectedMenuNo },
                    success: function(assignedMembers) {
                        assignedMemberNos = assignedMembers;  
                        disableCheckedMembers(assignedMembers);
                    } 
                });
            });

            $('#organization-chart').on('check_node.jstree uncheck_node.jstree', function (e, data) {
                updateSelectedMembers(data.instance);
            });

            $('#organization_search').on('keyup', function() { 
                const searchString = $(this).val();
                $('#organization-chart').jstree(true).search(searchString);
            });
        },
        error: function(xhr, status, error) {
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '조직도를 불러오는 중 오류가 발생했습니다.',
                confirmButtonText: '닫기'
            });
        }
    });
}

// 등록된 권한자 체크박스 비활성화
function disableCheckedMembers(assignedMemberNos) {
    var jstree = $('#organization-chart').jstree(true);
    if (jstree) {
        assignedMemberNos.forEach(function(memberNo) {
            var nodeId = 'member_' + memberNo;
            if (jstree.get_node(nodeId)) {
                jstree.disable_node(nodeId);
                jstree.check_node(nodeId);
            }
        });
    }
}

// 등록된 권한자 체크 상태
function updateOrganizationChartCheckboxes(jstreeInstance) {
    if (jstreeInstance) {
        jstreeInstance.uncheck_all();
        selectedMembers.forEach(function(member) {
            var nodeId = 'member_' + member.no;
            jstreeInstance.check_node(nodeId);
        });
    }
}

function updateSelectedMembers(instance) {
    selectedMembers = [];
    var checkedNodes = instance.get_checked(true);
    checkedNodes.forEach(function(node) {
        if (node.type === 'member' && !assignedMemberNos.includes(parseInt(node.id.split('_')[1]))) {
            selectedMembers.push({ 
                no: node.id.split('_')[1], 
                name: node.text 
            });
        }
    });
    updateSelectedMembersDisplay();
}
 
function updateSelectedMembersDisplay() {
    const displayElement = document.getElementById('selected-members');
    displayElement.innerHTML = ''; 
    
    if (selectedMembers.length > 0) {
        selectedMembers.forEach(member => { 
            const memberDiv = document.createElement('div');
            memberDiv.className = 'selected-member';
 
            const memberSpan = document.createElement('span');
            memberSpan.textContent = `${member.name} (${member.no})`;
 
            const removeButton = document.createElement('button');
            removeButton.textContent = '×';
            removeButton.className = 'remove-member';
            removeButton.onclick = function() { 
                const nodeId = `member_${member.no}`;
                const instance = $('#organization-chart').jstree(true);
                if (instance) {
                    instance.uncheck_node(nodeId);
                }
 
                selectedMembers = selectedMembers.filter(m => m.no !== member.no);
                updateSelectedMembersDisplay();
 
                $('.permission_pick_list').find(`.permission-item[data-name="${member.name}"]`).remove();
 
                localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
            };
 
            memberDiv.appendChild(memberSpan);
            memberDiv.appendChild(removeButton);
 
            displayElement.appendChild(memberDiv);
        });
    } 
}


$('#confirmButton').click(function() {  
    var csrfToken = document.querySelector('input[name="_csrf"]').value; 
    
    $.ajax({
        url: '/permission/addMembers',
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': csrfToken,
            'Content-Type': 'application/json'
        }, 
        data: JSON.stringify({
            menuNo: selectedMenuNo,
            memberNos: selectedMembers.map(member => member.no)
        }),
        success: function(response) {
            if (response.res_code === "200") {
                Swal.fire('권한자 등록', response.res_msg, 'success').then((result) => {
                    if (result.isConfirmed) {
                        $('#organizationChartModal').modal('hide');
                         fetchPermissionMembersByMenuNo(selectedMenuNo); 
                    }
                });
            } else {
                Swal.fire('권한자 등록', response.res_msg, 'error');
            }
        },
        error: function () {
            Swal.fire("서버 오류", response.res_msg, "error");
        }
    });
});

function fetchPermissionMembersByMenuNo(menuNo) {
    const element = document.querySelector(`.permission_Lists a[data-id="${menuNo}"]`);
    if (element) {
        fetchPermissionMembers(element);
    }
}

// 삭제
$(document).on('change', '.member-checkbox', function() {
    updateDeleteButtonState();
});

function updateDeleteButtonState() {
    const anyChecked = $('.member-checkbox:checked').length > 0;
    $('#deleteButton').prop('disabled', !anyChecked);
}

$('#deleteButton').click(function() {
    const selectedMemberNos = $('.member-checkbox:checked').map(function() {
        return $(this).data('memberNo');
    }).get();

    if (selectedMemberNos.length === 0) {
        Swal.fire('삭제 오류', '선택된 항목이 없습니다.', 'error');
        return;
    }

    const menuNo = Number(selectedMenuNo); 
    
    if (!menuNo) {
        Swal.fire('삭제 오류', '현재 메뉴 번호를 찾을 수 없습니다.', 'error');
        return;
    }

    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    $.ajax({
        url: '/permission/deleteMembers',
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': csrfToken,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({ 
            memberNos: selectedMemberNos,
            menuNo: menuNo  
        }),
        success: function(response) {
            if (response.res_code === "200") {
                Swal.fire('권한자 삭제', response.res_msg, 'success').then(() => {
                    fetchPermissionMembersByMenuNo(menuNo);
                });
            } else {
                Swal.fire('권한자 삭제', response.res_msg, 'error');
            }
        }
    });
});
