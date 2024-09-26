let currentMember = parseInt(document.getElementById("currentMember").value, 10);
//알림 출력을 위한 멤버값

document.querySelectorAll('#nav_wrap > ul > li > a').forEach(anchor => {
    anchor.addEventListener('click', function(event) {

        const dropdown = this.nextElementSibling;
        const parentLi = this.parentElement;
        const isParentActive = parentLi.classList.contains('active');

        closeDropdowns();

        if (!isParentActive) {
            parentLi.classList.add('active');

            if (dropdown) {
                dropdown.style.display = 'block';
            }
        } else {
            parentLi.classList.remove('active');
            if (dropdown) {
                dropdown.style.display = 'none';
            }
        }

        const childAnchors = dropdown?.querySelectorAll('li > a') || [];
        childAnchors.forEach(childAnchor => {
            childAnchor.removeEventListener('click', handleChildClick);
            childAnchor.addEventListener('click', handleChildClick);
        });

        function handleChildClick(event) {
            event
            event.stopPropagation();
            const childDropdown = this.nextElementSibling;
            const childLi = this.parentElement;
            const isChildDropdownOpen = childDropdown && childDropdown.style.display === 'block';

            if (childDropdown) {
                childDropdown.style.display = isChildDropdownOpen ? 'none' : 'block';
            }

            if (!isChildDropdownOpen) {
                childLi.classList.add('active');
            } else {
                childLi.classList.remove('active');
            }
        }
    });
});

document.addEventListener('click', function(event) {
    if (!event.target.closest('#nav_wrap')) {
        closeDropdowns();
    }
});

function closeDropdowns() {
    document.querySelectorAll('.dropdown').forEach(dropdown => {
        dropdown.style.display = 'none';
    });
    document.querySelectorAll('#nav_wrap > ul > li').forEach(li => {
        li.classList.remove('active');
    });
}

//알림 모달
const modal = document.getElementById("notification-modal");
const closeButton = document.querySelector(".close-notification-modal");

function showNotification(title, content, memberNo) {
    console.log(typeof(currentMember));
    console.log(typeof(memberNo));
    if(memberNo === currentMember){
        const notificationContainer = document.getElementById("notificationContainer");
        const notificationModal = document.createElement("div");

        notificationModal.classList.add("notification-modal");

        notificationModal.innerHTML = `
            <div class="notification-modal-content">
                <strong>${title}</strong>
                <p>${content}</p>
                <input type="hidden" name="memberNo" value="${memberNo}">
                <span class="close-notification-modal">&times;</span>
            </div>
        `;

        notificationContainer.appendChild(notificationModal);

        setTimeout(() => {
            notificationModal.classList.add("show");
        }, 10);

        setTimeout(() => {
            notificationModal.classList.remove("show");
            setTimeout(() => {
                notificationModal.remove();
            }, 400);
        }, 7000);

        notificationModal.querySelector('.close-notification-modal').addEventListener('click', function() {
            notificationModal.classList.remove("show");
            setTimeout(() => {
                notificationModal.remove();
            }, 400);
        });


    }

}
window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

// 웹소켓 선언 (공용)
// 전역 웹소켓 변수
let alarmSocket;

function connectWebSocket() {
    if (!alarmSocket || alarmSocket.readyState === WebSocket.CLOSED) {
        alarmSocket = new WebSocket(`ws://localhost:8080/websocket/notifications`);

        alarmSocket.onopen = function() {
            console.log("웹소켓이 연결되었습니다.");
        };

        alarmSocket.onclose = function(event) {
            console.log("웹소켓 연결이 해제되었습니다.", event);
            // 필요시 재연결 로직
        };

        alarmSocket.onerror = function(error) {
            console.error("에러 발생", error);
        };

        alarmSocket.onmessage = function(event) {
            const message = JSON.parse(event.data);
            if (message.type === 'chatAlarm') {
                const title = message.title;
                const content = message.content;
                message.data.forEach(function(item) {
                    showNotification(title, content, item.memberNo);
                });

            }
        };


    }
}
if (!alarmSocket) {
    connectWebSocket();
}
//------ 수정불가 ------
