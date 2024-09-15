//chatMember
document.addEventListener("DOMContentLoaded", function () {


// 확인 버튼
    document.getElementById('confirmButton').addEventListener('click', function(event) { 
    // 'event' 객체를 함수 인자로 받아야 합니다.
    event.preventDefault(); // 기본 동작을 막습니다.

    const currentMemberNo = document.getElementById("currentMemberNo").value;
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value; // CSRF 토큰 선택 (없으면 undefined 처리)
    const currentMemberName = document.getElementById("currentMemberName").value;

    fetch('/api/chat/memberAddRoom', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...(csrfToken && { 'X-CSRF-TOKEN': csrfToken }) // CSRF 토큰이 있으면 헤더에 추가
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
        if (webSocket && webSocket.readyState !== WebSocket.OPEN) {
            initWebSocket();
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

});
