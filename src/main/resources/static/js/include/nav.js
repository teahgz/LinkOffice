let currentMember = parseInt(document.getElementById("currentMember").value, 10);
//알림 출력을 위한 멤버값
document.addEventListener('DOMContentLoaded', () => {
    const dropdownToggles = document.querySelectorAll('.dropdown-toggle');
    const links = document.querySelectorAll('.link');

    // 페이지 로드 시 드롭다운 상태 복원
    dropdownToggles.forEach(toggle => {
        const dropdownId = toggle.nextElementSibling ? toggle.nextElementSibling.id : null;
        if (dropdownId && sessionStorage.getItem(dropdownId) === 'open') {
            toggle.nextElementSibling.classList.add('show'); // 드롭다운 열기
        }
    });

    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', (event) => {
            event.preventDefault(); // 링크 기본 동작 방지
            const dropdown = toggle.nextElementSibling; // 해당 드롭다운 찾기

            // 현재 클릭한 드롭다운 열기
            if (dropdown) {
                dropdown.classList.toggle('show'); // 드롭다운 토글
                const dropdownId = dropdown.id;

                // 현재 드롭다운 상태 저장
                if (dropdown.classList.contains('show')) {
                    sessionStorage.setItem(dropdownId, 'open'); // 드롭다운 열림
                } else {
                    sessionStorage.removeItem(dropdownId); // 드롭다운 닫힘
                }

                // 부모 드롭다운이 열린 상태에서 클릭한 경우
                if (!dropdown.classList.contains('show')) {
                    // 열린 드롭다운 모두 닫기
                    dropdownToggles.forEach(otherToggle => {
                        const otherDropdown = otherToggle.nextElementSibling;
                        if (otherDropdown && otherDropdown !== dropdown) {
                            otherDropdown.classList.remove('show'); // 닫기
                            sessionStorage.removeItem(otherDropdown.id); // 상태 삭제
                        }
                    });
                }
            }
        });
    });

    // 전체 문서에 클릭 이벤트 추가
    document.addEventListener('click', (event) => {
        const target = event.target;
        let isDropdown = false;

        dropdownToggles.forEach(toggle => {
            const dropdown = toggle.nextElementSibling;
            if (dropdown && dropdown.contains(target)) {
                isDropdown = true; // 드롭다운 내부 클릭
            }
            if (toggle.contains(target)) {
                isDropdown = true; // 드롭다운 토글 클릭
            }
        });

        if (!isDropdown) {
            // 열린 드롭다운 모두 닫기
            dropdownToggles.forEach(toggle => {
                const dropdown = toggle.nextElementSibling;
                if (dropdown) {
                    dropdown.classList.remove('show'); // 닫기
                    sessionStorage.removeItem(dropdown.id); // 상태 삭제
                }
            });
        }
    });

    // 홈으로 이동할 때 모든 드롭다운 닫기
    links.forEach(link => {
        link.addEventListener('click', (event) => {
            if (link.classList.contains('home-link')) { // 홈 링크 클래스 확인
                dropdownToggles.forEach(toggle => {
                    const dropdown = toggle.nextElementSibling;
                    if (dropdown) {
                        dropdown.classList.remove('show'); // 닫기
                        sessionStorage.removeItem(dropdown.id); // 상태 삭제
                    }
                });
            }
        });
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

            } else if(message.type === 'documentAlarm'){
				const title = message.title;
                const content = message.content;
                message.data.forEach(function(item) {
			        if (Number(item.memberNo) === currentMember) {
		                showNotification(title, content, item.memberNo);
			        }
                });
			}
        };


    }
}
if (!alarmSocket) {
    connectWebSocket();
}
//------ 수정불가 ------
