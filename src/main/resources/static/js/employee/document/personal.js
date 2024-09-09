$(function () {
    // jsTree 인스턴스 생성
    $('#jstree').jstree({
        'core': {
            'data': [
                {
                    "text": "최상위 폴더",
                    "id": "node_1",
                    "icon": "fa fa-folder",
                    "state": { "opened": true },
                    "children": [
                        {
                            "text": "자식 폴더",
                            "id": "subNode_1",
                            "icon": "fa fa-folder",
                            "state": { "opened": true },
                            "children": [
                                {
                                    "text": "손자 폴더",
                                    "id": "childNode_1",
                                    "icon": "fa fa-folder",
                                    "state": { "opened": true },
                                    "children": [
                                        {
                                            "text": "손자의 자식 폴더",
                                            "id": "grandChildNode_1",
                                            "state": { "opened": true },
                                            "icon": "fa fa-folder"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ],
            'themes': {
                'icons': true,
                'dots': false,
            }
        },
        'plugins': ['checkbox', 'types'],
        'types': {
            'default': {
                'icon': 'fa fa-folder'
            },
            'file': {
                'icon': 'fa fa-file'
            }
        }
    }).on('ready.jstree', function (e, data) {
        restoreSelection(data.instance);
    });

    // 체크박스 클릭 시 이벤트 핸들링
    $('#jstree').on('changed.jstree', function (e, data) {
        if (data.action === 'select_node' || data.action === 'deselect_node') {
            // Check/uncheck all child nodes when a node is checked/unchecked
            if (data.node) {
                // Handle the checkbox state change
                if (data.node.state.selected) {
                    $('#jstree').jstree(true).check_node(data.node);
                } else {
                    $('#jstree').jstree(true).uncheck_node(data.node);
                }
                // Update state for all child nodes
                updateChildNodes(data.node, data.node.state.selected);
            }
        }
    });

    // 폴더 추가 버튼 클릭 시 이벤트
    $('#add_folder_button').on('click', function() {
        alert('폴더 추가 버튼 클릭됨');
        // 폴더 추가 로직을 여기에 추가
    });

    // 폴더 이름 클릭 시 체크박스 상태 변경 방지
    $('#jstree').on('click', 'li a', function (e) {
        e.stopPropagation();
        e.preventDefault();
    });

    // 자식 노드의 체크박스 상태 업데이트
    function updateChildNodes(node, isChecked) {
        var instance = $('#jstree').jstree(true);
        var children = instance.get_node(node).children;
        children.forEach(function(child) {
            instance[isChecked ? 'check_node' : 'uncheck_node'](child);
            // Recursively update children
            updateChildNodes(instance.get_node(child), isChecked);
        });
    }

    // 선택된 노드를 저장하는 함수 예시
    function restoreSelection(instance) {
        // 예시로, 선택된 노드를 복원하는 로직을 추가할 수 있습니다.
        // 여기에 실제 복원 로직을 구현해야 합니다.
    }
});
