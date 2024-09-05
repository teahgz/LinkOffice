// 폼 제출 시 처리
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("vacationForm");
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    form.addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출 동작을 막음

        const memberNo = document.getElementById('memberNo').value; // 멤버 번호 가져오기
        const yearInputs = document.querySelectorAll('[id^="year"]'); // 연차 입력폼 선택
        const lessThanOneYear = document.getElementById('lessThanOneYear').checked; // 1년 미만 체크박스 상태

        // 연차와 일수 데이터를 담을 객체 생성
        const vacationData = {};

        yearInputs.forEach(input => {
            const year = input.id.replace('year', ''); // Extract year number from id (e.g., "1" from "year1")
            const vacationDays = input.value; // Get input value

            // 연차와 일수를 vacationData 객체에 추가
            vacationData[year] = vacationDays.trim() === "" ? 0 : parseInt(vacationDays);
            console.log( vacationData[year]);
        });

        // 전송할 데이터 객체 생성
        const requestData = {
            memberNo: memberNo,
            vacationData: vacationData,
            lessThanOneYear: lessThanOneYear
        };

        fetch('/vacation/addVacationAction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(requestData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.res_code === '200') {
                Swal.fire({
                    icon: 'success',
                    title: '성공',
                    text: data.res_msg,
                    confirmButtonText: "닫기"
                }).then((result) => {
                    if (result.isConfirmed) {
                        location.href = `/home`;
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
});


    const forms = document.getElementById("vacationTypeForm");
    forms.addEventListener('submit', function(event) {

        event.preventDefault();

         const payload = new FormData(form);

        fetch('/vacation/addTypeVacation', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
             body: JSON.stringify(payload)
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
                         location.href = `/home`
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
