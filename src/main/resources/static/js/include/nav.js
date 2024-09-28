let currentMember = parseInt(document.getElementById("currentMember").value, 10);
//알림 출력을 위한 멤버값



document.addEventListener("DOMContentLoaded", function () {
    const dropdownToggles = document.querySelectorAll(".dropdown-toggle");
    const dropdowns = document.querySelectorAll(".dropdown");
    const links = document.querySelectorAll(".dropdown a"); // Select all links in dropdowns
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
        dropdown.closest('li').classList.add('active');
        currentDropdown = dropdown;
        const toggle = dropdown.previousElementSibling;
        toggle.classList.add('active-toggle');
    }

    function closeAllDropdowns() {
        dropdowns.forEach(function (dropdown) {
            dropdown.style.display = "none";
        });
        dropdownToggles.forEach(function (toggle) {
            toggle.closest('li').classList.remove('active');
            toggle.classList.remove('active-toggle');
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

    const nestedDropdownToggles = document.querySelectorAll(".dropdown > .dropdown-toggle");
    nestedDropdownToggles.forEach(function (toggle) {
        toggle.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();

            const dropdownId = this.nextElementSibling.getAttribute("data-dropdown-id");
            const nestedDropdown = document.querySelector(`.dropdown[data-dropdown-id="${dropdownId}"]`);

            if (nestedDropdown.style.display === "block") {
                nestedDropdown.style.display = "none";
            } else {
                nestedDropdown.style.display = "block";
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

    links.forEach(function (link) {
        link.addEventListener("click", function () {

            links.forEach(l => l.classList.remove('active-link'));
            this.classList.add('active-link');

            localStorage.setItem("activeLink", this.getAttribute("href"));
        });
    });

    const activeLinkHref = localStorage.getItem("activeLink");
    if (activeLinkHref) {
        const activeLink = document.querySelector(`.dropdown a[href="${activeLinkHref}"]`);
        if (activeLink) {
            activeLink.classList.add('active-link'); // Add active class to the saved link
        }
    }
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

function showNotification(title, content, memberNo, time) {

    if(memberNo === currentMember){
        const notificationContainer = document.getElementById("notificationContainer");
        const notificationModal = document.createElement("div");

        notificationModal.classList.add("notification-modal");

        notificationModal.innerHTML = `
            <div class="notification-modal-content">
                <strong>${title}</strong>
                <p>${content}</p>
                <input type="hidden" name="memberNo" value="${memberNo}">
                <span class="notification-time">${time}</span>
                <span class="close-notification-modal">&times;</span>
            </div>
        `;

        notificationContainer.appendChild(notificationModal);
        let unreadCountElement = document.getElementById('unread-bell-count');
        if (!unreadCountElement) {
            unreadCountElement = document.createElement('span');
            unreadCountElement.id = 'unread-bell-count';
            unreadCountElement.className = 'badge';
            document.getElementById('notification-bell').appendChild(unreadCountElement);
        }
        // 현재 카운트 가져오기 및 증가
        const currentCount = parseInt(unreadCountElement.textContent) || 0;
        unreadCountElement.textContent = currentCount + 1;
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
            const currentType = message.nofication_type;
            if (message.type === 'chatAlarm') {
                const title = message.title;
                const content = message.content;
                if(currentType === window.functionType){
                    markNotificationsAsRead(window.functionType);
                }else {
                    message.data.forEach(function(item) {
                        showNotification(title, content, item.memberNo, message.timestamp);

                    });
                }
            } else if(message.type === 'documentAlarm'){
                const title = message.title;
                const content = message.content;
                message.data.forEach(function(item) {
                  if (Number(item.memberNo) === currentMember) {
                        showNotification(title, content, item.memberNo,  message.timestamp);
                  }
                });
            } else if(message.type === 'vacationApprovalAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'vacationApprovalReviewsAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'vacationAppApproveAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'vacationAppRejectAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo, message.timestamp);
                      });
            } else if(message.type === 'approvalAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'approvalReviewsAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'appApproveAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            } else if(message.type === 'appRejectAlarm'){
                      const title = message.title;
                      const content = message.content;
                      message.data.forEach(function(item) {
                          showNotification(title, content, item.memberNo,  message.timestamp);
                      });
            }
        };


    }
}
if (!alarmSocket) {
    connectWebSocket();

}

// 안읽음을 읽음으로 처리하는 함수(공동 사용)
function markNotificationsAsRead(functionType) {
    fetch(`/api/nofication/type/read/${currentMember}/${functionType}`)
        .then(response => response.json())
        .then(data => {
            if (data.read) {
                console.log('읽음 처리 완료');
                bellUnreadCount();
            } else {
                console.error('읽음 처리 실패');
            }
        })
        .catch(error => {
            console.error('읽음 처리 중 오류 발생:', error);
        });
}
//------ 수정불가 ------
