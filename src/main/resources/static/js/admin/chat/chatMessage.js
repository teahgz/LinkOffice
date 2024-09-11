
let currentChatRoomNo = null;

function handleChatRoomClick(element) {
    currentChatRoomNo = element;
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
    chatContentDiv.innerHTML = '';

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
function formatDateTime(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
window.sendMessage = function() {
    const chat_sender_no = document.getElementById("userNo").value;
    const chat_content = document.getElementById("messageInput").value;
    const chat_sender_name =  document.getElementById("userName").value;
//    const now = new Date();
//    const formattedTime = formatDateTime(now);
//    console.log(formattedTime);

    if (currentChatRoomNo === null) {
        console.error("No chat room selected.");
        return;
    }

    const message = {
        chat_sender_no: chat_sender_no,
        chat_room_no: currentChatRoomNo,
        chat_content: chat_content,
        chat_sender_name : chat_sender_name,
        //chat_message_create_date: formattedTime
    };
    socket.send(JSON.stringify(message));
    document.getElementById("messageInput").value = "";
};

socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    const chatContentDiv = document.getElementById("chatContent");

    const messageElement = document.createElement("div");
    messageElement.classList.add("messageItem");
    const formattedDate = formatDateTime(new Date(message.chat_message_create_date));

    messageElement.innerHTML = `
        <p><strong>${message.chat_sender_name}:</strong> ${message.chat_content}</p>
        <span>${formattedDate}</span>
    `;
    chatContentDiv.appendChild(messageElement);

    chatContentDiv.scrollTop = chatContentDiv.scrollHeight;
};
