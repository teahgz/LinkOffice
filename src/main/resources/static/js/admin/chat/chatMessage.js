(function() {
    let currentChatRoomNo = null;
    let socket = null;
    if (socket && socket.readyState === WebSocket.OPEN) {
        console.log("WebSocket already connected.");
    return; // 이미 연결된 상태이므로 아무 작업도 하지 않음
    }

    socket = new WebSocket(`ws://localhost:8080/websocket/chat`);

    socket.onopen = function() {
        console.log("WebSocket connection established.");
    };

    socket.onclose = function(event) {
        console.log("WebSocket connection closed:", event);
    };

    socket.onerror = function(error) {
        console.error("WebSocket error:", error);
    };

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
        fetch('/chat/chart')
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

    socket.onmessage = function(event) {
        const message = JSON.parse(event.data);
        const chatContentDiv = document.getElementById("chatContent");
        console.log(message.type);
        // 메시지 타입에 따른 처리
        if (message.type === "chatRoomCreation") {
            if (message.chatRoomNo) {
                console.log(`채팅방 생성 완료. 채팅방 번호: ${message.chatRoomNo}`);

                // 새로운 채팅방 정보를 사용하여 채팅방 목록 업데이트
                const newChatItem = document.createElement("div");
                newChatItem.classList.add("chatItem");
                newChatItem.setAttribute("onclick", `handleChatRoomClick(${message.chatRoomNo})`);
                newChatItem.innerHTML = `
                    <h3><p>${message.currentMemberName}</p></h3>
                    <input type="hidden" id="memberNo" value="${document.getElementById('memberNo').value}"/>
                    <input type="hidden" id="chatRoomNo" value="${message.chatRoomNo}" />
                `;
                document.getElementById('chatList').appendChild(newChatItem);

            }
        } else {
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

    };

    window.sendMessage = function() {
        const chat_sender_no = document.getElementById("userNo").value;
        const chat_content = document.getElementById("messageInput").value;
        const chat_sender_name = document.getElementById("userName").value;

        if (currentChatRoomNo === null) {
            console.error("방이 선택되지 않음");
            return;
        }

        const message = {
            chat_sender_no: chat_sender_no,
            chat_room_no: currentChatRoomNo,
            chat_content: chat_content,
            chat_sender_name: chat_sender_name,
        };
        socket.send(JSON.stringify(message));
        document.getElementById("messageInput").value = "";
    };

    // 전역에서 사용할 confirmButton 함수 정의
    window.confirmButton = function() {
        const currentMemberNo = document.getElementById("currentMemberNo").value;
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const currentMemberName = document.getElementById("currentMemberName").value;

        const message = {
            type: "chatRoomCreation",
            members: selectedMembers,
            currentMemberNo: currentMemberNo,
            names: selectNames,
            currentMemberName: currentMemberName,
            csrfToken: csrfToken // 필요 시 추가
        };

        socket.send(JSON.stringify(message));
    };

    // 버튼 클릭 이벤트와 window.confirmButton 함수 연결
    document.getElementById('confirmButton').addEventListener('click', window.confirmButton);

    function formatDateTime(date) {
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        let hours = date.getHours();
        const minutes = ('0' + date.getMinutes()).slice(-2);
        const seconds = ('0' + date.getSeconds()).slice(-2);

        const ampm = hours >= 12 ? '오후' : '오전';
        hours = hours % 12;
        hours = hours ? hours : 12;

        return `${year}-${month}-${day}. ${ampm} ${hours}:${minutes}:${seconds}`;
    }

    function loadChatMessages(chatRoomNo) {
        fetch(`/api/chat/messages/${chatRoomNo}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버가 응답하지 않음" + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                displayChatMessages(data);
                console.log(data);
            })
            .catch(error => {
                console.error('전송 중 오류 발생', error);
            });
    }

    function displayChatMessages(messages) {
        const chatContentDiv = document.getElementById("chatContent");
        chatContentDiv.innerHTML = '';

        const memberNo = parseInt(document.getElementById("memberNo").value, 10);

        messages.forEach(function(message) {
            const messageElement = document.createElement("div");

            if (message.senderNo === memberNo) {
                messageElement.classList.add("my-message");
                messageElement.classList.add("messageItem");
                messageElement.innerHTML = `
                    <p>${message.chatContent}</p>
                    <span>${new Date(message.chatMessageCreateDate).toLocaleString()}</span>
                `;
            } else {
                messageElement.classList.add("other-message");
                messageElement.innerHTML = `
                    <p><strong>${message.senderName}:</strong> ${message.chatContent}</p>
                    <span>${new Date(message.chatMessageCreateDate).toLocaleString()}</span>
                `;
            }

            chatContentDiv.appendChild(messageElement);
        });

        chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
    }

    window.handleChatRoomClick = function(element) {
        currentChatRoomNo = element;
        loadChatMessages(element);
    };
}());
