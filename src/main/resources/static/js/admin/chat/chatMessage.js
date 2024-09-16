document.addEventListener("DOMContentLoaded", function() {
    const firstChatRoom = document.querySelector('.chatItem');
    console.log(firstChatRoom);

    if (firstChatRoom) {

        const chatRoomNo = firstChatRoom.querySelector('input[id="chatRoomNo"]').value;

        if (chatRoomNo) {
            handleChatRoomClick(chatRoomNo);
        }
    }
});

(function() {
    let currentChatRoomNo = null;
    let socket = null;
    if (socket && socket.readyState === WebSocket.OPEN) {
        console.log("WebSocket already connected.");
        return;
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
        $.ajax({
            url: '/chat/chart',
            method: 'GET',
            success: function(data) {
                $('#organization-chart').jstree('destroy').empty();
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
                }).on('changed.jstree', function (e, data) {
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
        const permissionPickList = $('.permission_pick_list');
        selectedMembersContainer.empty();
        permissionPickList.empty();

        const selectedNodes = instance.get_selected(true);
        selectedMembers = [];
        selectNames = [];

        selectedNodes.forEach(function(node) {
            if (node.original.type === 'member') {
                const memberId = node.id;
                const memberNumber = memberId.replace('member_', '');
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

        // 메시지 타입에 따른 처리
        if (message.type === "chatRoomCreation") {
            if (message.chatRoomNo) {

                // 새로운 채팅방 정보를 사용하여 채팅방 목록 업데이트
                const newChatItem = document.createElement("div");
                newChatItem.classList.add("chatItem");
                newChatItem.setAttribute("onclick", `handleChatRoomClick(${message.chatRoomNo})`);
                newChatItem.innerHTML = `
                    <h3><p>${message.names}</p></h3>
                    <input type="hidden" id="memberNo" value="${document.getElementById('memberNo').value}"/>
                    <input type="hidden" id="chatRoomNo" value="${message.chatRoomNo}" />
                `;

                const chatList = document.getElementById('chatList');
                // 목록 맨 위에 새로운 항목을 삽입
                chatList.insertBefore(newChatItem, chatList.firstChild);

                // 선택된 항목 초기화 및 모달 닫기
                resetSelectedMembers();
                $('#organizationChartModal').modal('hide');
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

            // 상대방 채팅방 목록에 채팅방이 없을 경우 자동 추가
            const chatRoomExists = document.querySelector(`input[value="${message.chat_room_no}"]`);
            if (!chatRoomExists) {
                const newChatItem = document.createElement("div");
                newChatItem.classList.add("chatItem");
                newChatItem.setAttribute("onclick", `handleChatRoomClick(${message.chat_room_no})`);
                newChatItem.innerHTML = `
                    <h3><p>${message.names}</p></h3>
                    <input type="hidden" value="${message.chat_room_no}" />
                `;
                const chatList = document.getElementById('chatList');
                chatList.insertBefore(newChatItem, chatList.firstChild);
            }
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

    window.confirmButton = function() {
        const currentMemberNo = document.getElementById("currentMemberNo").value;
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const currentMemberName = document.getElementById("currentMemberName").value;

        // 참여자 인원수를 확인
        if (selectedMembers.length > 1) {
            // 그룹 채팅방 이름 입력 모달 띄우기
            $('#groupChatNameModal').modal('show');
        } else {
            // 바로 채팅방 생성 요청
            createChatRoom(currentMemberNo, currentMemberName, csrfToken);
        }
    };

    function createChatRoom(currentMemberNo, currentMemberName, csrfToken) {
        const groupChatName = selectedMembers.length > 1 ? document.getElementById('groupChatNameInput').value : null;

        const message = {
            type: "chatRoomCreation",
            members: selectedMembers,
            currentMemberNo: currentMemberNo,
            names: selectNames,
            currentMemberName: currentMemberName,
            groupChatName: groupChatName,  // 그룹 채팅방 이름 포함
            csrfToken: csrfToken  // 필요 시 추가
        };

        event.preventDefault();
        socket.send(JSON.stringify(message));

        // 그룹 채팅방 이름 모달 닫기
        $('#groupChatNameModal').modal('hide');
    }

    document.getElementById('confirmButton').addEventListener('click', function(event) {
        event.preventDefault();  // 기본 동작을 막아 URL이 변경되지 않도록 설정
        window.confirmButton();
    });

    document.getElementById('confirmGroupChatName').addEventListener('click', function(event) {
        event.preventDefault();  // 기본 동작을 막아 URL이 변경되지 않도록 설정
        createChatRoom(document.getElementById("currentMemberNo").value, document.getElementById("currentMemberName").value, document.querySelector('input[name="_csrf"]').value);
    });
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

    function resetSelectedMembers() {
        selectedMembers = [];
        selectNames = [];

        // jstree의 모든 체크된 노드 해제
        $('#organization-chart').jstree(true).uncheck_all();

        // 선택된 멤버와 권한 리스트 비우기
        $('#selected-members').empty();
        $('.permission_pick_list').empty();

        // 로컬스토리지에서 선택된 사원 정보 초기화
        localStorage.removeItem('selectedMembers');
    }

})();
