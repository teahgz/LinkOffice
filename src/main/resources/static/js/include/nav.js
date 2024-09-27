let currentMember = parseInt(document.getElementById("currentMember").value, 10);
//알림 출력을 위한 멤버값

document.addEventListener("DOMContentLoaded", function () {
    const dropdownToggles = document.querySelectorAll(".dropdown-toggle");
    const dropdowns = document.querySelectorAll(".dropdown");
    let currentDropdown = null;

    function saveDropdownState(dropdownId) {
        localStorage.setItem("activeDropdown", dropdownId);
    }

    function loadDropdownState() {
        const activeDropdownId = localStorage.getItem("activeDropdown");
        if (activeDropdownId) {
            const activeDropdown = document.querySelector(`.dropdown[data-dropdown-id="${activeDropdownId}"]`);
            if (activeDropdown) {
                openDropdown(activeDropdown);
            }
        }
    }

    function openDropdown(dropdown) {
        closeAllDropdowns();
        dropdown.style.display = "block";
        currentDropdown = dropdown;
    }

    function closeAllDropdowns() {
        dropdowns.forEach(function (dropdown) {
            dropdown.style.display = "none";
        });
        currentDropdown = null;
    }

    dropdownToggles.forEach(function (toggle) {
        toggle.addEventListener("click", function (e) {
            e.preventDefault();
            const dropdownId = this.nextElementSibling.getAttribute("data-dropdown-id");
            const dropdown = document.querySelector(`.dropdown[data-dropdown-id="${dropdownId}"]`);

            if (dropdown === currentDropdown) {
                closeAllDropdowns();
                localStorage.removeItem("activeDropdown");
            } else {
                openDropdown(dropdown);
                saveDropdownState(dropdownId);
            }
        });
    });

    document.addEventListener("click", function (e) {
        if (!e.target.closest(".dropdown-toggle") && !e.target.closest(".dropdown")) {
            closeAllDropdowns();
            localStorage.removeItem("activeDropdown");
        }
    });

    loadDropdownState();
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
