// 초기 세션 시간 (초) 설정
let remainingTime = 1800;
const headerCurrentMember = document.getElementById('headerCurrentMember').value; //현재 로그인한 사용자 정보
const csrfToken = document.querySelector('input[name="_csrf"]').value;

// 페이지 로드 시 호출
document.addEventListener("DOMContentLoaded", function () {
    fetchSessionTime();
    bellUnreadCount();
});
function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(remainingSeconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
}

function updateSessionTime() {
    remainingTime--;
    const formattedTime = formatTime(remainingTime);
    document.getElementById('session-time').innerText = `${formattedTime}`;

    if (remainingTime <= 0) {
        alert('세션이 만료되었습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/';
    }
}
//함수로 따로 설정
function fetchSessionTime() {
    fetch('/session-time')
        .then(response => response.json())
        .then(timeLeft => {
            remainingTime = timeLeft;
            setInterval(updateSessionTime, 1000);
        });
}

document.getElementById('userImage').addEventListener('click', function(event) {
    var dropdownMenu = document.getElementById('dropdownMenu');
    var notificationModal = document.getElementById('notification-bell-modal');

    if (notificationModal.classList.contains('show')) {
        notificationModal.classList.remove('show');
    }
    dropdownMenu.classList.toggle('show');
    event.stopPropagation();
});

document.getElementById('notification-bell').addEventListener('click', function(event) {
    var notificationModal = document.getElementById('notification-bell-modal');
    var dropdownMenu = document.getElementById('dropdownMenu');


    if (dropdownMenu.classList.contains('show')) {
        dropdownMenu.classList.remove('show');
    }


    // 알림 모달 내용을 가져오기
    unreadNoficationList();

    notificationModal.classList.toggle('show');
    event.stopPropagation();
});


document.addEventListener('click', function(event) {
    var dropdownMenu = document.getElementById('dropdownMenu');
    var notificationModal = document.getElementById('notification-bell-modal');

    if (!event.target.closest('.user_image')) {
        dropdownMenu.classList.remove('show');
    }

    if (!event.target.closest('.bell')) {
        notificationModal.classList.remove('show');
    }
});

//통합알림 개수
function bellUnreadCount() {
       fetch(`/api/nofication/unread/${headerCurrentMember}`)
           .then(response => response.json())
           .then(data => {
                const notificationBell = document.getElementById('notification-bell');

                const existingCount = document.getElementById('unread-bell-count');
                console.log(data.unreadCount);

                if (existingCount) {
                    notificationBell.removeChild(existingCount);
                }

                if (data.unreadCount > 0) {
                    const unreadBellCount = document.createElement('span');
                    unreadBellCount.id = 'unread-bell-count';
                    unreadBellCount.className = 'badge';
                    unreadBellCount.textContent = data.unreadCount;

                    notificationBell.appendChild(unreadBellCount);
                }
           });
}
const noficationTypeUrl={
     1: `/api/chat/${headerCurrentMember}`
}

//통합 알림 리스트
function unreadNoficationList() {
    const notificationModal = document.getElementById('notification-bell-modal');
    const unreadCountElement = document.getElementById('unread-bell-count');

    notificationModal.innerHTML = '';
    fetch(`/api/nofication/unread/list/${headerCurrentMember}`)
        .then(response => response.json())
        .then(data => {
            const notificationModal = document.getElementById('notification-bell-modal');
            if (data.unreadList.length === 0) {
                notificationModal.innerHTML = '';
                const noNotificationsItem = document.createElement('li');
                noNotificationsItem.textContent = '알림이 존재하지 않습니다.';
                noNotificationsItem.style.textAlign = 'center';
                notificationModal.appendChild(noNotificationsItem);
            } else {
                notificationModal.innerHTML = `
                    <li id="mark-as-read" class="mark-as-read" style="font-size: 10px; text-align: right; color: gray;">일괄읽음</li>
                `;
                addMarkAsReadListener();
                data.unreadList.forEach(notification => {

                    const listItem = document.createElement('li');
                    listItem.setAttribute('data-notification-no', notification.nofication_no);
                    listItem.setAttribute('data-notification-type', notification.nofication_type);
                    const date = new Date(notification.nofication_create_date);
                    const formattedDate = date.toLocaleDateString('ko-KR', {
                        year: '2-digit',
                        month: '2-digit',
                        day: '2-digit'
                    });
                    const formattedTime = date.toLocaleTimeString('ko-KR', {
                        hour: '2-digit',
                        minute: '2-digit',
                        hour12: true
                    });
                    listItem.innerHTML = `
                        <strong style="margin-bottom: 5px;">${notification.nofication_title}</strong>
                        <p>${notification.nofication_content}</p>
                        <em style="display: block; margin-bottom: 5px; float: right;">${formattedDate} ${formattedTime}</em>
                        <hr style="border: none; margin: 10px 0;">
                    `;

                    listItem.addEventListener('click', () => {
                        const notificationType = listItem.getAttribute('data-notification-type');
                         const notificationTypePk = listItem.getAttribute('data-notification-type-pk');
                        if (notificationType === '1') {
                            window.location.href = noficationTypeUrl[1];
                        } else {

                            console.log('다른 타입의 알림 클릭:', noficationType);
                        }
                    });

                    notificationModal.appendChild(listItem);

                });
            }
        })
        .catch(error => {
            console.error('알림을 가져오는 중 오류 발생:', error);
        });
}
function addMarkAsReadListener() {
    const markAsReadButton = document.getElementById('mark-as-read');
    if (markAsReadButton) {
        markAsReadButton.removeEventListener('click', handleMarkAsRead); // 기존 리스너 제거
        markAsReadButton.addEventListener('click', handleMarkAsRead); // 새로운 리스너 추가
    }
}
function handleMarkAsRead() {
        const memberNo = headerCurrentMember;
        const notificationNos = [];

        const notificationItems = document.querySelectorAll('#notification-bell-modal li');
        notificationItems.forEach(item => {
            const notificationNo = item.getAttribute('data-notification-no');
            if (notificationNo) {
                notificationNos.push(notificationNo);
            }
        });

        fetch(`/api/notification/read`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN':csrfToken
            },
            body: JSON.stringify({
                memberNo: memberNo,
                notificationNos: notificationNos
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.read) {
                document.getElementById('notification-bell-modal').innerHTML = '';
                document.getElementById('unread-bell-count').textContent = '0';
                const unreadCountElement = document.getElementById('unread-bell-count');
                if (unreadCountElement) {
                     unreadCountElement.remove();
                }
            } else {
                console.error('읽음 처리 실패');
            }
        })
        .catch(error => {
            console.error('읽음 처리 중 오류 발생:', error);
        });


}