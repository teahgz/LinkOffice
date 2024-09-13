// chatMessage.js
(function() {
    let webSocket = null;
    let isWebSocketActive = false;
    let currentChatRoomNo = null;

    // 웹소켓 초기화 함수
    function initWebSocket() {
        if (webSocket) {
            return; // 이미 웹소켓이 초기화된 경우, 다시 초기화하지 않음
        }

        webSocket = new WebSocket("ws://localhost:8080/websocket/chat");

        webSocket.onopen = function() {
            console.log("웹소켓 연결됨");
            isWebSocketActive = true;
        };

        webSocket.onclose = function() {
            console.log("웹소켓 연결 종료됨");
            isWebSocketActive = false;
        };

        webSocket.onerror = function(error) {
            console.error("웹소켓 오류:", error);
        };

        webSocket.onmessage = function(event) {
            console.log("웹소켓 메시지:", event.data);
            handleMessage(event.data); // 메시지 처리 함수 호출
        };
    }

    function handleMessage(data) {
        const message = JSON.parse(data);
        const chatContentDiv = document.getElementById("chatContent");

        const messageElement = document.createElement("div");
        const now = new Date();
        const formattedTime = formatDateTime(now);

        const memberNo = parseInt(document.getElementById("memberNo").value, 10);
        const memberNoCheck = parseInt(message.chat_sender_no, 10);

        if (memberNoCheck === memberNo) {
            messageElement.classList.add("my-message", "messageItem");
            messageElement.innerHTML = `
                <p>${message.chat_content}</p>
                <span>${formattedTime}</span>
            `;
        } else {
            messageElement.classList.add("other-message", "messageItem");
            messageElement.innerHTML = `
                <p><strong>${message.chat_sender_name}:</strong> ${message.chat_content}</p>
                <span>${formattedTime}</span>
            `;
        }

        chatContentDiv.appendChild(messageElement);
        chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
    }

    function formatDateTime(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }

    document.addEventListener("DOMContentLoaded", function() {
        initWebSocket();

        let selectedMembers = [];  // 선택된 사원을 저장할 배열
        let selectNames = [];

        function loadSelectedMembers() {
            const savedMembers = localStorage.getItem('selectedMembers');
            if (savedMembers) {
                selectedMembers = JSON.parse(savedMembers);
            }
        }

        function restoreSelection(instance) {
            selectedMembers.forEach(memberName => {
                const node = instance.get_node(instance.get_container().find(`:contains('${memberName}')`).attr('id'));
                if (node) {
                    instance.check_node(node);
                }
            });
        }

        document.getElementById('openChart').addEventListener('click', function() {
            if (webSocket) {
                webSocket.close(); // 조직도 관련 작업 전 웹소켓 종료
            }
            $('#organizationChartModal').modal('show');
            loadOrganizationChart();
        });

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

                    $('#organization-chart').on('changed.jstree', function (e, data) {
                        updateSelectedMembers(data.selected, data.instance);
                    });

                    $('#organization_search').on('keyup', function() {
                        const searchString = $(this).val();
                        $('#organization-chart').jstree(true).search(searchString);
                    });
                })
                .catch(error => {
                    console.error('조직도 로딩 오류:', error);
                });
        }

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

        document.getElementById('confirmButton').addEventListener('click', function() {
            const currentMemberNo = document.getElementById("currentMemberNo").value;
            const csrfToken = document.querySelector('input[name="_csrf"]').value;
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
                    names: selectNames,
                    currentMemberName: currentMemberName
                })
            })
            .then(response => response.json())
            .then(data => {
                console.log('서버 응답:', data);
                // 웹소켓 비활성화
                if (webSocket) {
                    webSocket.close();
                }
            })
            .catch(error => {
                console.error('선택한 사원 저장 오류:', error);
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: '선택한 사원을 저장하는데 오류가 발생했습니다.',
                    confirmButtonText: '확인',
                    allowOutsideClick: false,
                    allowEscapeKey: false,
                    allowEnterKey: false
                });
            });
        });

        function updateChatList(updatedChatList) {
            const chatListElement = document.getElementById('chatList');
            chatListElement.innerHTML = ''; // Clear the existing chat list

            updatedChatList.forEach(chat => {
                const chatItem = document.createElement('div');
                chatItem.classList.add('chatItem');
                chatItem.setAttribute('onclick', `handleChatRoomClick(${chat.chat_room_no})`);

                const chatName = document.createElement('h3');
                const chatNameText = document.createElement('p');
                chatNameText.textContent = chat.chat_member_room_name;
                chatName.appendChild(chatNameText);

                chatItem.appendChild(chatName);

                const memberNoInput = document.createElement('input');
                memberNoInput.type = 'hidden';
                memberNoInput.id = 'memberNo';
                memberNoInput.value = chat.member_no;

                const chatRoomNoInput = document.createElement('input');
                chatRoomNoInput.type = 'hidden';
                chatRoomNoInput.id = 'chatRoomNo';
                chatRoomNoInput.value = chat.chat_room_no;

                chatItem.appendChild(memberNoInput);
                chatItem.appendChild(chatRoomNoInput);

                chatListElement.appendChild(chatItem);
            });
        }

        window.onload = function() {
            initWebSocket();
        };

        // 채팅방을 클릭했을 때 웹소켓 활성화
        window.handleChatRoomClick = function(roomNo) {
            currentChatRoomNo = roomNo;
            if (!isWebSocketActive) {
                initWebSocket();
            }
        };

        window.sendMessage = function() {
            const chat_sender_no = document.getElementById("currentMemberNo").value;
            const chat_sender_name = document.getElementById("currentMemberName").value;
            const chat_content = document.getElementById("chatContentInput").value;

            if (chat_sender_no && chat_sender_name && chat_content && currentChatRoomNo) {
                const message = JSON.stringify({
                    chat_sender_no,
                    chat_sender_name,
                    chat_room_no: currentChatRoomNo,
                    chat_content
                });

                if (webSocket && isWebSocketActive) {
                    webSocket.send(message);
                    document.getElementById("chatContentInput").value = ''; // 입력 필드 초기화
                } else {
                    console.error('웹소켓이 활성화되지 않았습니다.');
                }
            } else {
                console.error('메시지 전송에 필요한 정보가 부족합니다.');
            }
        };
    });
})();
