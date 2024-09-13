////chatMember
//document.addEventListener("DOMContentLoaded", function () {
//
//    let webSocket = null;
//
//    // 웹소켓 초기화 함수
//    function initWebSocket() {
//        // 웹소켓 연결 로직 구현
//        webSocket = new WebSocket("ws://localhost:8080/websocket/chat");
//
//        webSocket.onopen = function() {
//            console.log("웹소켓 연결됨");
//        };
//
//        webSocket.onclose = function() {
//            console.log("웹소켓 연결 종료됨");
//        };
//
//        webSocket.onerror = function(error) {
//            console.error("웹소켓 오류:", error);
//        };
//
//        webSocket.onmessage = function(event) {
//            console.log("웹소켓 메시지:", event.data);
//        };
//    }
//
//    let selectedMembers = [];  // 선택된 사원을 저장할 배열
//    let selectNames = [];
//    // 페이지 로드 시 이전 선택된 사원 로드
//    function loadSelectedMembers() {
//        const savedMembers = localStorage.getItem('selectedMembers');
//        if (savedMembers) {
//            selectedMembers = JSON.parse(savedMembers);
//        }
//    }
//
//    // 페이지 로드 시 기존 선택 상태를 복원
//    function restoreSelection(instance) {
//        selectedMembers.forEach(memberName => {
//            const node = instance.get_node(instance.get_container().find(`:contains('${memberName}')`).attr('id'));
//            if (node) {
//                instance.check_node(node);
//            }
//        });
//    }
//
//    document.getElementById('openChart').addEventListener('click', function() {
//        $('#organizationChartModal').modal('show');
//        loadOrganizationChart();
//    });
//
//    // 조직도 로딩
//    function loadOrganizationChart() {
//        fetch('/organizationChart/chart')
//            .then(response => response.json())
//            .then(data => {
//                console.log('조직도 데이터:', data);
//                $('#organization-chart').jstree({
//                    'core': {
//                        'data': data,
//                        'themes': {
//                            'icons': true,
//                            'dots': false,
//                        }
//                    },
//                    'plugins': ['checkbox', 'types', 'search'],
//                    'types': {
//                        'default': {
//                            'icon': 'fa fa-users'
//                        },
//                        'department': {
//                            'icon': 'fa fa-users'
//                        },
//                        'member': {
//                            'icon': 'fa fa-user'
//                        }
//                    }
//                }).on('ready.jstree', function (e, data) {
//                    restoreSelection(data.instance);
//                });
//
//                // 체크박스 변경 시 선택된 사원 업데이트
//                $('#organization-chart').on('changed.jstree', function (e, data) {
//                    updateSelectedMembers(data.selected, data.instance);
//                });
//
//                // 검색
//                $('#organization_search').on('keyup', function() {
//                    const searchString = $(this).val();
//                    $('#organization-chart').jstree(true).search(searchString);
//                });
//            })
//            .catch(error => {
//                console.error('조직도 로딩 오류:', error);
//            });
//    }
//
//    // 선택된 사원 업데이트
//    function updateSelectedMembers(selectedIds, instance) {
//        const selectedMembersContainer = $('#selected-members');
//        const permissionPickList = $('.permission_pick_list');
//        selectedMembersContainer.empty();
//        permissionPickList.empty();
//
//        const selectedNodes = instance.get_selected(true);
//        selectedMembers = [];
//        selectNames = [];
//
//        selectedNodes.forEach(function(node) {
//            if (node.original.type === 'member') {
//                const memberId = node.id;
//                const memberNumber = memberId.replace('member_', ''); // 사원 번호
//                const memberElement = $('<div class="selected-member"></div>');
//                const memberName = $('<span></span>').text(node.text);
//                const removeButton = $('<button class="remove-member">&times;</button>');
//
//                memberElement.append(memberName).append(removeButton);
//                selectedMembersContainer.append(memberElement);
//
//                selectedMembers.push(memberNumber);
//                selectNames.push(node.text);
//
//                removeButton.click(function() {
//                    instance.uncheck_node(node);
//                    memberElement.remove();
//                    const index = selectedMembers.indexOf(memberNumber);
//                    if (index !== -1) {
//                        selectedMembers.splice(index, 1);
//                    }
//
//                    localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
//
//                    permissionPickList.find(`.permission-item[data-name="${node.text}"]`).remove();
//                });
//
//                const permissionItem = $(`<div class="permission-item" data-name="${node.text}"></div>`);
//                permissionItem.text(node.text);
//                permissionPickList.append(permissionItem);
//            }
//        });
//
//        localStorage.setItem('selectedMembers', JSON.stringify(selectedMembers));
//    }
//
//// 확인 버튼
//    document.getElementById('confirmButton').addEventListener('click', function() {
//        const currentMemberNo = document.getElementById("currentMemberNo").value;
//        const csrfToken = document.querySelector('input[name="_csrf"]').value;
//        const currentMemberName = document.getElementById("currentMemberName").value;
//
//        fetch('/api/chat/memberAddRoom', {
//               method: 'POST',
//               headers: {
//                   'Content-Type': 'application/json',
//                   'X-CSRF-TOKEN': csrfToken
//               },
//               body: JSON.stringify({
//                   members: selectedMembers,
//                   currentMemberNo: currentMemberNo,
//                   names: selectNames,
//                   currentMemberName: currentMemberName
//               })
//           })
//           .then(response => response.json())
//           .then(data => {
//               console.log('서버 응답:', data);
//               // 웹소켓 다시 연결 (모달이 닫히면 이미 재연결되지만 필요시 추가)
//               if (webSocket && webSocket.readyState !== WebSocket.OPEN) {
//                   initWebSocket();
//               }
//           })
//           .catch(error => {
//               console.error('선택한 사원 저장 오류:', error);
//               Swal.fire({
//                   icon: 'error',
//                   title: '오류',
//                   text: '선택한 사원을 저장하는데 오류가 발생했습니다.',
//                   confirmButtonText: '확인',
//                   allowOutsideClick: false,
//                   allowEscapeKey: false,
//                   allowEnterKey: false
//               });
//           });
//       });
//
//    function updateChatList(updatedChatList) {
//        const chatListElement = document.getElementById('chatList');
//        chatListElement.innerHTML = ''; // Clear the existing chat list
//
//        updatedChatList.forEach(chat => {
//            const chatItem = document.createElement('div');
//            chatItem.classList.add('chatItem');
//            chatItem.setAttribute('onclick', `handleChatRoomClick(${chat.chat_room_no})`);
//
//            const chatName = document.createElement('h3');
//            const chatNameText = document.createElement('p');
//            chatNameText.textContent = chat.chat_member_room_name;
//            chatName.appendChild(chatNameText);
//
//            chatItem.appendChild(chatName);
//
//            const memberNoInput = document.createElement('input');
//            memberNoInput.type = 'hidden';
//            memberNoInput.id = 'memberNo';
//            memberNoInput.value = chat.member_no;
//
//            const chatRoomNoInput = document.createElement('input');
//            chatRoomNoInput.type = 'hidden';
//            chatRoomNoInput.id = 'chatRoomNo';
//            chatRoomNoInput.value = chat.chat_room_no;
//
//            chatItem.appendChild(memberNoInput);
//            chatItem.appendChild(chatRoomNoInput);
//
//            chatListElement.appendChild(chatItem);
//        });
//    }
//    / 페이지 로드 시 웹소켓 초기화
//    window.onload = function() {
//        initWebSocket();
//    };
//});
