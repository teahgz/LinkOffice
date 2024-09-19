document.addEventListener("DOMContentLoaded", function() {
    // 페이지 로드 시 첫 번째 채팅방 자동 클릭
    const firstChatRoom = document.querySelector('.chatItem');
    if (firstChatRoom) {
         const chatRoomNo = firstChatRoom.querySelector('input[id="chatRoomNo"]').value;
        if (chatRoomNo) {
            handleChatRoomClick(chatRoomNo);
        }
    }

    // 메시지 입력 필드와 전송 버튼 가져오기
  const messageInput = document.getElementById('messageInput');
  const sendButton = document.getElementById('sendButton');

  if (sendButton && messageInput) {
      function toggleSendButton() {
          sendButton.disabled = messageInput.value.trim() === '';
          if (messageInput.value.trim() !== '') {
              sendButton.classList.remove("btn-disabled");
          } else {
              sendButton.classList.add("btn-disabled");
          }
      }

      messageInput.addEventListener('input', toggleSendButton);
      toggleSendButton(); // 초기 상태 업데이트
  } else {
      console.error("버튼 또는 입력 필드를 찾을 수 없습니다.");
  }
    // 채팅방 자동완성 기능
    document.getElementById('searchInput').oninput = searchMem;
    function searchMem() {
        let input = document.getElementById('searchInput').value.toLowerCase().replace(/\s+/g, '');
        let chatItems = document.getElementsByClassName('chatItem');
        for (let i = 0; i < chatItems.length; i++) {
            let chatName = chatItems[i].getElementsByTagName('p')[0].innerText.toLowerCase().replace(/\s+/g, '');
            chatItems[i].style.display = chatName.includes(input) ? "" : "none";
        }
    }
    window.toggleSearch = function() {
        let searchContainer = document.getElementById("searchContainer");
        let searchChatButton = document.getElementById("searchChatButton");
        if (searchContainer.style.display === "none") {
            searchContainer.style.display = "flex";
            searchChatButton.classList.remove("btn-primary");
            searchChatButton.classList.add("btn-secondary");
        } else {
            searchContainer.style.display = "none";
            searchChatButton.classList.remove("btn-secondary");
            searchChatButton.classList.add("btn-primary");
            searchInput.value = '';
           searchMem();
        }
    };
       function closeEditChat() {
           const editChatRoomModal = document.getElementById('editChatRoomModal');
          $('#editChatRoomModal').modal('hide');
       }
       const closeButton = document.querySelector('.modal-content-element button');
       if (closeButton) {
           closeButton.addEventListener('click', closeEditChat);
       } else {
           console.error("닫기 버튼을 찾을 수 없습니다.");
       }


    const editChatRoomNameInput = document.getElementById('chatRoomNameInput');
    const confirmEditButton = document.getElementById('confirmEditButton');

    function toggleConfirmEditButton() {
        confirmEditButton.disabled = editChatRoomNameInput.value.trim() === '';
         if (confirmEditButton.disabled) {
                confirmEditButton.classList.add('button-disabled');
            } else {
                confirmEditButton.classList.remove('button-disabled');
            }
    }

    editChatRoomNameInput.addEventListener('input', toggleConfirmEditButton);
    toggleConfirmEditButton();

        $('#editChatRoomModal').on('show.bs.modal', function () {
            toggleConfirmEditButton();
        });

      $('#editChatRoomModal').on('hide.bs.modal', function () {
          editChatRoomNameInput.value = '';
          confirmEditButton.disabled = true;
          confirmEditButton.classList.add('update-button');
      });
});

(function() {
    let currentChatRoomNo = null;
    let socket = null;

    if (!socket || socket.readyState !== WebSocket.OPEN) {
        socket = new WebSocket(`ws://localhost:8080/websocket/chat`);
    }

    socket.onopen = function() {
        console.log("웹소켓이 연결되었습니다.");
    };

    socket.onclose = function(event) {
        console.log("웹소켓 연결이 해제되었습니다.", event);
    };

    socket.onerror = function(error) {
        console.error("에러 발생", error);
    };

    let selectedMembers = [];
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

    document.getElementById('addChatItem').addEventListener('click', function(){
        $('#organizationAddModal').modal('show');
        loadOrganizationAddChart();
           fetchExistingMembers().then(existingMembers => {
           disableCheckedMembers(existingMembers); // 가져온 멤버 데이터를 jstree에 반영
       });
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
        const selectedMembersList = $('#selectedMembersList');
        selectedMembersContainer.empty();
        permissionPickList.empty();
        selectedMembersList.empty();

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
                selectedMembersList.append(`<p><i class="fa-solid fa-user"></i> ${node.text}</p>`);
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
                const newChatItem = document.createElement("div");
                newChatItem.classList.add("chatItem");
                newChatItem.setAttribute("onclick", `handleChatRoomClick(${message.chatRoomNo})`);
                newChatItem.innerHTML = `
                    <h3><p>${message.names}</p></h3>
                    <input type="hidden" id="memberNo" value="${document.getElementById('memberNo').value}"/>
                    <input type="hidden" id="chatRoomNo" value="${message.chatRoomNo}" />
                `;
                const chatList = document.getElementById('chatList');
                chatList.insertBefore(newChatItem, chatList.firstChild);

                resetSelectedMembers();
                $('#organizationChartModal').modal('hide');
            }
        } else if(message.type === "chatRoomUpdate"){
            const updatedChatRoomNo = message.roomNo;
            const updatedChatRoomName = message.updatedChatRoomName;
            const chatItem = document.querySelector(`input[value="${updatedChatRoomNo}"]`);
            console.log(updatedChatRoomName);
            if (chatItem) {
                const chatItemDiv = chatItem.closest('.chatItem');
                const chatNameElement = chatItemDiv.querySelector('h3 p');
                chatNameElement.textContent = updatedChatRoomName;
            }
           const currentChatRoomNo = document.getElementById('chatRoomNo').value;
           const chatRoomTitleElement = document.getElementById("chatRoomTitle");
           chatRoomTitleElement.textContent = updatedChatRoomName;

           document.getElementById('chatRoomNameInput').value = '';
        } else {
            const messageElement = document.createElement("div");
            const now = new Date();
            const formattedTime = formatDateTime(now);

            const memberNo = parseInt(document.getElementById("memberNo").value, 10);
            const memberNoCheck = parseInt(message.chat_sender_no, 10);

            if (memberNoCheck === memberNo) {
                 messageElement.classList.add("my-message", "messageItem");
                            messageElement.innerHTML = `
                                <div class="message-ele">
                                    <span class="message-time">${formattedTime}</span>
                                    <div class="message-content">
                                        <p>${message.chat_content}</p>
                                    </div>
                                </div>
                            `;
            } else {
                messageElement.classList.add("other-message", "messageItem");
                  messageElement.innerHTML = `
                                <div class="message-sender">
                                    <strong>${message.chat_sender_name}</strong>
                                </div>
                                <div class="message-ele">
                                    <div class="message-content">
                                        <p>${message.chat_content}</p>
                                    </div>
                                    <span class="message-time">${formattedTime}</span>
                                </div>
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
        const sendButton = document.getElementById("sendButton");
        if (currentChatRoomNo === null) {
            console.error("방이 선택되지 않음");
            return;
        }
        if (chat_content.trim() === '') {
            console.error("메시지 내용이 비어 있음");
            return;
        }
        sendButton.disabled = true;
        sendButton.classList.add("btn-disabled");

        const message = {
            chat_sender_no: chat_sender_no,
            chat_room_no: currentChatRoomNo,
            chat_content: chat_content,
            chat_sender_name: chat_sender_name,
        };
        socket.send(JSON.stringify(message));
        document.getElementById("messageInput").value = "";
         toggleSendButton();
    };

    window.confirmButton = function() {
        const currentMemberNo = document.getElementById("currentMemberNo").value;
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const currentMemberName = document.getElementById("currentMemberName").value;

        if (selectedMembers.length > 1) {
            $('#groupChatNameModal').modal('show');
        } else {
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
            groupChatName: groupChatName,
            csrfToken: csrfToken
        };

        socket.send(JSON.stringify(message));
        $('#groupChatNameModal').modal('hide');
    }

    document.getElementById('confirmButton').addEventListener('click', function(event) {
        event.preventDefault();
        window.confirmButton();
    });

    document.getElementById('confirmGroupChatName').addEventListener('click', function(event) {
        event.preventDefault();
        createChatRoom(document.getElementById("currentMemberNo").value, document.getElementById("currentMemberName").value, document.querySelector('input[name="_csrf"]').value);
    });

    document.getElementById('editChatRoomButton').addEventListener('click', function(event) {
            event.preventDefault();
            $('#editChatRoomModal').modal('show');
        });

    document.getElementById('openDrop').addEventListener('click', function(event) {
        event.preventDefault();
        const dropdownMenu = document.getElementById('chatDropdownMenu');
        dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block';
    });

    // 채팅방 이름 수정 함수
    document.getElementById('confirmEditButton').addEventListener('click', function(event) {
        event.preventDefault();

        const currentMemberNo = document.getElementById("currentMemberNo").value;
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const chatRoomName = document.getElementById('chatRoomNameInput').value;

        if (!chatRoomName.trim()) {
            alert("채팅방 이름을 입력하세요.");
            return;
        }

        // 채팅방 수정 함수 호출
        updateChatRoom(currentMemberNo, currentChatRoomNo, csrfToken, chatRoomName);
    });

       function updateChatRoom(currentMemberNo, roomNo, csrfToken, chatRoomName) {

           if (!roomNo) {
               console.error("채팅방이 선택되지 않았습니다.");
               return;
           }

           const message = {
               type: "chatRoomUpdate",
               roomNo: roomNo,
               currentMemberNo: currentMemberNo,
               chatRoomName: chatRoomName,
               csrfToken: csrfToken
           };

           // 웹소켓을 통해 서버로 메시지 전송
           socket.send(JSON.stringify(message));

           $('#editChatRoomModal').modal('hide'); // 모달 닫기
       }
    window.addEventListener('click', function(e) {
        const dropdownMenu = document.getElementById('chatDropdownMenu');

        if (!e.target.matches('#openDrop') && !e.target.closest('.chatDropdown')) {
            dropdownMenu.style.display = 'none';
        }
    });

function formatDateTime(date) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
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
            })
            .catch(error => {
                console.error('전송 중 오류 발생', error);
            });
    }
    //채팅방 타입
    function getChatRoomType(chatRoomNo) {
        return fetch(`/api/chat/roomType/${chatRoomNo}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("채팅방 타입을 찾을 수 없습니다.");
                }
                return response.text();
            })
            .catch(error => {
                console.error("error", error);
                return null;
            });
    }
    function displayChatMessages(messages) {
        const chatContentDiv = document.getElementById("chatContent");
        chatContentDiv.innerHTML = '';

        const memberNo = parseInt(document.getElementById("memberNo").value, 10);

        messages.forEach(function(message) {
            const messageElement = document.createElement("div");
            const formattedTime = new Date(message.chatMessageCreateDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

             if (message.senderNo === memberNo) {
                        messageElement.classList.add("my-message", "messageItem");
                        messageElement.innerHTML = `
                            <div class="message-ele">
                                <span class="message-time">${formattedTime}</span>
                                <div class="message-content">
                                    <p>${message.chatContent}</p>
                                </div>
                            </div>
                        `;
                    } else {
                        messageElement.classList.add("other-message", "messageItem");
                        messageElement.innerHTML = `
                            <div class="message-sender">
                                <strong>${message.senderName}</strong>
                            </div>
                            <div class="message-ele">
                                <div class="message-content">
                                    <p>${message.chatContent}</p>
                                </div>
                                <span class="message-time">${formattedTime}</span>
                            </div>
                        `;
                    }
            chatContentDiv.appendChild(messageElement);
        });

        chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
    }

    window.handleChatRoomClick = function(element) {
            currentChatRoomNo = element;
            console.log(currentChatRoomNo);
                let chatItems = document.getElementsByClassName('chatItem');
                for (let i = 0; i < chatItems.length; i++) {
                    chatItems[i].classList.remove('selected');
                }

                let selectedChatItem = document.querySelector(`input[value="${element}"]`).closest('.chatItem');
                if (selectedChatItem) {
                    selectedChatItem.classList.add('selected');
                }

            getChatRoomName(element).then(chatRoomName => {
                if (chatRoomName) {
                    document.getElementById('chatRoomTitle').innerText = chatRoomName;
                }
                loadChatMessages(element);
            }).catch(error => {
                console.error('error', error);
            });
             getChatRoomType(element).then(chatRoomType => {
              const dropdownMenu = document.getElementById('chatDropdownMenu');
              const editChatItem = document.getElementById('editChatRoomButton');
              const addChatItem = document.getElementById('addChatItem');
              if (chatRoomType === '1') {
                   editChatItem.style.display = 'block';
                   addChatItem.style.display = 'block';
              } else {
                   editChatItem.style.display = 'none';
                   addChatItem.style.display = 'none';
              }
              }).catch(error => {
                    console.error('error', error);
              });
    }
    function getChatRoomName(chatRoomNo) {
        const memberNo = document.getElementById('memberNo').value;
        return fetch(`/api/chat/roomName/${chatRoomNo}/${memberNo}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("채팅방 이름을 찾지 못했습니다.");
                }
                return response.text();
            })
            .catch(error => {
                console.error("error", error);
                return null;
            });
    }

    function resetSelectedMembers() {
        selectedMembers = [];
        selectNames = [];

        $('#organization-chart').jstree(true).uncheck_all();
        $('#selected-members').empty();
        $('.permission_pick_list').empty();

        localStorage.removeItem('selectedMembers');
    }

//    //추가
//function loadOrganizationChartAdd(existingMembers) {
//    $.ajax({
//        url: '/chat/chart',
//        method: 'GET',
//        success: function(data) {
//
//            $('#organization-chart-add').jstree('destroy').empty();
//            $('#organization-chart-add').jstree({
//                'core': {
//                    'data': data,
//                    'themes': {
//                        'icons': true,
//                        'dots': false,
//                    }
//                },
//                'plugins': ['checkbox', 'types', 'search'],
//                'types': {
//                    'default': {
//                        'icon': 'fa fa-users'
//                    },
//                    'department': {
//                        'icon': 'fa fa-users'
//                    },
//                    'member': {
//                        'icon': 'fa fa-user'
//                    }
//                }
//            }).on('ready.jstree', function (e, data) {
//                restoreSelection(data.instance);
//                disableCheckedMembers(assignedMembers);// 기존에 선택된 노드 복원 로직
//            }).on('changed.jstree', function (e, data) {
//                updateSelectedMembersAdd(data.selected, data.instance); // 새로운 모달에 맞춘 선택 업데이트 로직
//            });
//        },
//                error: function(xhr, status, error) {
//                    console.error('조직도 로딩 오류:', error);
//                }
//            });
//        }
//
//        function updateSelectedMembersAdd(selectedIds, instance) {
//            const selectedMembersContainer = $('#selected-member'); // 새로운 모달에서 선택된 멤버를 출력하는 곳
//            selectedMembersContainer.empty();
//
//            const selectedNodes = instance.get_selected(true);
//            selectedMembers = [];
//            selectNames = [];
//
//            selectedNodes.forEach(function(node) {
//                if (node.original.type === 'member') {
//                    const memberId = node.id;
//                    const memberNumber = memberId.replace('member_', '');
//                    const memberElement = $('<div class="selected-member"></div>');
//                    const memberName = $('<span></span>').text(node.text);
//                    const removeButton = $('<button class="remove-member">&times;</button>');
//
//                    memberElement.append(memberName).append(removeButton);
//                    selectedMembersContainer.append(memberElement);
//                    selectedMembers.push(memberNumber);
//                    selectNames.push(node.text);
//
//                    removeButton.click(function() {
//                        instance.uncheck_node(node);
//                        memberElement.remove();
//                        const index = selectedMembers.indexOf(memberNumber);
//                        if (index !== -1) {
//                            selectedMembers.splice(index, 1);
//                        }
//                    });
//                }
//            });
//
//            localStorage.setItem('selectedMembersAdd', JSON.stringify(selectedMembers)); // 새로운 모달에서 선택된 멤버
//        }
//        // 등록된 사원 체크박스 비활성화
//        function disableCheckedMembers(assignedMemberNos) {
//            var jstree = $('#organization-chart-add').jstree(true);
//            if (jstree) {
//                assignedMemberNos.forEach(function(memberNo) {
//                    var nodeId = 'member_' + memberNo;
//                    if (jstree.get_node(nodeId)) {
//                        jstree.disable_node(nodeId);
//                        jstree.check_node(nodeId);
//                    }
//                });
//            }
//        }

})();
