document.getElementById('vacationStandard').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const designatedCheckbox = document.getElementById('designated');
    const joinedCheckbox = document.getElementById('joined');
    const designatedDateInput = document.getElementById('designatedDate');

    let formData = {};

    // 체크박스가 둘 다 선택되지 않은 경우 경고 메시지 띄우기
    if (!designatedCheckbox.checked && !joinedCheckbox.checked) {
        Swal.fire({
            icon: 'warning',
            text: '지정일 또는 입사일 기준을 선택해 주세요.',
            confirmButtonText: "확인",
            customClass: {
                confirmButton: 'custom-confirm-button'
            }
        });
        return; // 폼 제출 중단
    }

    // 지정일이 선택되었으나 날짜가 입력되지 않은 경우 경고 메시지 띄우기
    if (designatedCheckbox.checked && !designatedDateInput.value) {
        Swal.fire({
            icon: 'warning',
            text: '지정일 날짜를 입력해 주세요.',
            confirmButtonText: "확인",
            customClass: {
                        confirmButton: 'custom-confirm-button'
                    }
        });
        return; // 폼 제출 중단
    }

    // 체크박스에 따라 formData 구성
    if (designatedCheckbox.checked) {
        formData = {
            type: 'designated',
            designatedDate: designatedDateInput.value
        };
    } else if (joinedCheckbox.checked) {
        formData = {
            type: 'joined'
        };
    }

    fetch('/vacation/checkStandard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log(data); // 응답 데이터 확인
        if (data.res_code === '200') {
            Swal.fire({
                icon: 'success',
                title: '성공',
                text: data.res_msg,
                confirmButtonText: "확인",
                customClass: {
                    confirmButton: 'custom-confirm-button'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    location.reload();
                }
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: '실패',
                text: data.res_msg,
                confirmButtonText: "닫기"
            });
        }
    })
    .catch(error => {
        Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '서버와의 통신 중 오류가 발생했습니다.',
            confirmButtonText: "닫기"
        });
    });
});

document.getElementById('designated').addEventListener('change', function() {
    const designatedCheckbox = document.getElementById('designated');
    const joinedCheckbox = document.getElementById('joined');
    const designatedDateInput = document.getElementById('designatedDate');

    if (designatedCheckbox.checked) {
        joinedCheckbox.checked = false;
        designatedDateInput.disabled = false;
    } else {
        designatedDateInput.disabled = true;
    }
});

document.getElementById('joined').addEventListener('change', function() {
    const designatedCheckbox = document.getElementById('designated');
    const joinedCheckbox = document.getElementById('joined');
    const designatedDateInput = document.getElementById('designatedDate');

    if (joinedCheckbox.checked) {
        designatedCheckbox.checked = false;
        designatedDateInput.disabled = true;
    }
});

window.onload = function() {
    document.getElementById('designatedDate').disabled = true;
};
