document.getElementById('vacationStandard').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const designatedCheckbox = document.getElementById('designated');
    const joinedCheckbox = document.getElementById('joined');
    const designatedDateInput = document.getElementById('designatedDate');

    let formData = {};

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
                confirmButtonText: "닫기"
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

