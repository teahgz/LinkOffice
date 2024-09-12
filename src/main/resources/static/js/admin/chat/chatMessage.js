
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
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);

    let hours = date.getHours();
    const minutes = ('0' + date.getMinutes()).slice(-2);
    const seconds = ('0' + date.getSeconds()).slice(-2);

    const ampm = hours >= 12 ? '오후' : '오전';
    hours = hours % 12;
    hours = hours ? hours : 12;

    return `${year}-${month}-${day}. ${ampm} ${hours}:${minutes}:${seconds} `;
}

window.sendMessage = function() {
    const chat_sender_no = document.getElementById("userNo").value;
    const chat_content = document.getElementById("messageInput").value;
    const chat_sender_name =  document.getElementById("userName").value;


    if (currentChatRoomNo === null) {
        console.error("방이 선택되지 않음");
        return;
    }

    const message = {
        chat_sender_no: chat_sender_no,
        chat_room_no: currentChatRoomNo,
        chat_content: chat_content,
        chat_sender_name : chat_sender_name,
    };
    socket.send(JSON.stringify(message));
    document.getElementById("messageInput").value = "";
};

socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
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
};
