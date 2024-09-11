let currentChatRoomNo = null;

function handleChatRoomClick(element) {
    currentChatRoomNo = element;
    console.log('Selected Chat Room No:', element);
    loadChatMessages(element);
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
    chatContentDiv.innerHTML = ''; // Clear previous messages

    messages.forEach(function(message) {
        const messageElement = document.createElement("div");
        messageElement.classList.add("messageItem");
        messageElement.innerHTML = `
            <p><strong>${message.senderName}:</strong> ${message.chatContent}</p>
            <span>${new Date(message.chatMessageCreateDate).toLocaleString()}</span>
        `;
        chatContentDiv.appendChild(messageElement);
    });

    chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
}

const socket = new WebSocket(`ws://localhost:8080/websocket/chat`);

socket.onopen = function() {
    console.log("WebSocket connection established.");
};

socket.onclose = function(event) {
    console.log("WebSocket connection closed:", event);
};

socket.onerror = function(error) {
    console.error("WebSocket error:", error);
};

window.sendMessage = function() {
    const chat_sender_no = document.getElementById("userNo").value;
    const chat_content = document.getElementById("messageInput").value;

    if (currentChatRoomNo === null) {
        console.error("No chat room selected.");
        return;
    }

    const message = {
        chat_sender_no: chat_sender_no,
        chat_room_no: currentChatRoomNo,
        chat_content: chat_content
    };
    console.log(message);
    socket.send(JSON.stringify(message));
    document.getElementById("messageInput").value = "";
};

socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log("Received WebSocket message:", message);

    const chatContentDiv = document.getElementById("chatContent");

    const messageElement = document.createElement("div");
    messageElement.classList.add("messageItem");
    messageElement.innerHTML = `
        <p><strong>${message.chat_sender_no}:</strong> ${message.chat_content}</p>
        <span>${new Date(message.chat_message_create_date).toLocaleString()}</span>
    `;
    chatContentDiv.appendChild(messageElement);

    chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
};
