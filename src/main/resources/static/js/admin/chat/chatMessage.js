    // loadChatMessages 함수 정의
    function loadChatMessages(chatRoomNo, memberNo) {

        fetch(`/api/chat/messages/${chatRoomNo}/${memberNo}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                displayChatMessages(data);
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }

    function displayChatMessages(messages) {
        const chatContentDiv = document.getElementById("chatContent");
        // 기존 메시지 지우기
        chatContentDiv.innerHTML = '';

        // 새로운 메시지 표시
        messages.forEach(function(message) {
            const messageElement = document.createElement("div");
            messageElement.classList.add("messageItem");
            messageElement.innerHTML = `
                <p><strong>${message.chat_sender_no}:</strong> ${message.chat_content}</p>
                <span>${message.chat_message_create_date}</span>
            `;
            chatContentDiv.appendChild(messageElement);
        });
    }

