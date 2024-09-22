document.addEventListener("DOMContentLoaded", function () {
    let selectedMembers = [];
    let approvers = [];
    let references = [];
    let reviewers = [];

    let approverNames = [];
    let referenceNames = [];
    let reviewerNames = [];

    function loadSelectedMembers() {
        const savedMembers = localStorage.getItem('selectedMembers');
        if (savedMembers) {
            selectedMembers = JSON.parse(savedMembers);
        }
    }

    function restoreSelection(instance) {
        selectedMembers.forEach(memberId => {
            const node = instance.get_node(memberId);
            if (node) {
                instance.check_node(node);
            }
        });
    }

    $('#openChart').click(function () {
        $('#organizationChartModal').modal('show');
        loadOrganizationChart();
    });

    function loadOrganizationChart() {
        $.ajax({
            url: '/organizationChart/chart',
            method: 'GET',
            success: function (data) {
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

                $('#organization-chart').on('changed.jstree', function (e, data) {
                    updateSelectedMembers(data.selected, data.instance);
                });

                $('#organization_search').on('keyup', function () {
                    const searchString = $(this).val();
                    $('#organization-chart').jstree(true).search(searchString);
                });
            },
            error: function (xhr, status, error) {
                console.error('조직도 로딩 오류:', error);
            }
        });
    }

    function updateSelectedMembers(selectedIds, instance) {
        const selectedNodes = instance.get_selected(true);
        selectedMembers = [];

        selectedNodes.forEach(function (node) {
            if (node.original.type === 'member') {
                const memberId = node.id;
                const memberNumber = memberId.replace('member_', '');
                selectedMembers.push(memberNumber);
            }
        });
        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
    }

    function addMemberToBox(node, boxId, array, nameArray) {
        const memberId = node.id;
        const memberNumber = memberId.replace('member_', '');
        const memberElement = $('<div class="selected-member"></div>');
        const memberName = $('<span></span>').text(node.text).data('member-number', memberNumber);
        const removeButton = $('<button class="remove-member">&times;</button>');
        const checkBox = $('<input type="checkbox" class="remove-checkbox">');
        memberElement.append(checkBox).append(memberName).append(removeButton);
        $(`#${boxId}`).append(memberElement);

        nameArray.push(node.text);

        removeButton.click(function () {
            removeMemberFromBox(memberNumber, memberElement, boxId, array, nameArray);
        });
    }

    function removeMemberFromBox(memberNumber, memberElement, boxId, array, nameArray) {
        const index = array.indexOf(memberNumber);
        if (index > -1) {
            array.splice(index, 1);
        }
        const nameIndex = nameArray.indexOf(memberElement.find('span').text());
        if (nameIndex > -1) {
            nameArray.splice(nameIndex, 1);
        }

        console.log(`${boxId} 배열:`, array);

        memberElement.remove();

        const nodeId = 'member_' + memberNumber;
        $('#organization-chart').jstree(true).enable_node(nodeId);
    }

function moveToList(event,targetId, array, nameArray) {
    event.preventDefault();

    const selectedNodes = $('#organization-chart').jstree(true).get_selected(true);

    // 결재자에 대해서만 6명 제한
    if (targetId === 'approver-list') {
        const currentApproverCount = array.length;

        if (currentApproverCount + selectedNodes.length > 6) {
            Swal.fire({
                icon: 'warning',
                text: '결재자는 최대 6명까지 선택할 수 있습니다.',
            });
            return;
        }
    }

    selectedNodes.forEach(function (node) {
        if (node.original.type === 'member' && !$('#organization-chart').jstree(true).is_disabled(node)) {
            const memberId = node.id;
            const memberNumber = memberId.replace('member_', '');

            if (!array.includes(memberNumber)) {
                addMemberToBox(node, targetId, array, nameArray);
                $('#organization-chart').jstree(true).disable_node(node);
                array.push(memberNumber);
                console.log(`${targetId} 배열:`, array);
            }
        }
    });
}

    function moveFromList(event,boxId, array, nameArray) {
        event.preventDefault();

        $(`#${boxId} .selected-member .remove-checkbox:checked`).each(function () {
            const memberElement = $(this).closest('.selected-member');
            const memberNumber = memberElement.find('span').data('member-number');
            removeMemberFromBox(memberNumber, memberElement, boxId, array, nameArray);
        });
    }

    $('.move-to-approver').click(function (event) {
        moveToList(event,'approver-list', approvers, approverNames);
    });

    $('.move-from-approver').click(function (event) {
        moveFromList(event,'approver-list', approvers, approverNames);
    });

    $('.move-to-reference').click(function (event) {
        moveToList(event,'reference-list', references, referenceNames);
    });

    $('.move-from-reference').click(function (event) {
        moveFromList(event,'reference-list', references, referenceNames);
    });

    $('.move-to-reviewer').click(function (event) {
        moveToList(event,'reviewer-list', reviewers, reviewerNames);
    });

    $('.move-from-reviewer').click(function (event) {
        moveFromList(event,'reviewer-list', reviewers, reviewerNames);
    });

function updateApproversDisplay() {
    const approverCells = $('#approvalLineTable td div');

     if (approvers.length > 0 || references.length > 0 || reviewers.length > 0) {
        approvers.forEach(function (memberNumber, index) {
            const fullName = approverNames[index];
            const [name, position] = fullName.split(' '); 
            const listItem = $('<span></span>').text(`${name}`); 
            const positionItem = $('<span></span>').text(`${position}`); 
            approverCells.eq(index + 1).append(positionItem);
            
            const targetRow = $('#approvalLineTable tr').eq(2); 

            targetRow.find('td').eq(index+1).append(listItem);
            
            $('<input type="hidden">').attr('id', 'approverNumbers').val(memberNumber).appendTo(approverCells.eq(index));
        });

        references.forEach(function (memberNumber, index) {
            const fullName = referenceNames[index];
            const [name] = fullName.split(' '); 
            const listItem = $('<span></span>').text(`${name} `);
            $('#referencestCell').append(listItem);
            $('<input type="hidden">').attr('id', 'referenceNumbers').val(memberNumber).appendTo('#referencestCell');
        });

        reviewers.forEach(function (memberNumber, index) {
            const fullName = reviewerNames[index];
            const [name] = fullName.split(' '); 
            const listItem = $('<span></span>').text(`${name} `); 
            $('#referenceCell').append(listItem);
            $('<input type="hidden">').attr('id', 'reviewerNumbers').val(memberNumber).appendTo('#referenceCell');
        });
    }
}

$('#confirmButton').click(function (event) {
    event.preventDefault();
    updateApproversDisplay(); 
    $('#organizationChartModal').modal('hide');
    localStorage.removeItem('selectedMembers');
    $('.permission_pick_list').empty();
});

    loadSelectedMembers();
});
