// 폼 제출 시 처리
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("vacationForm");
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const memberNo = document.getElementById('memberNo').value;
        const yearInputs = document.querySelectorAll('[id^="year"]');
        const lessThanOneYear = document.getElementById('lessThanOneYear').checked;
        const countVacation =document.getElementById('countVacation').value;

      const vacationPkElements = document.querySelectorAll('[id^="vacationPk"]');

        let count = 1;
        const vacationData = {};
        const vacationPkData = [];
        if (countVacation > 0) {
                vacationPkElements.forEach(input => {
                    if (input.value) {
                        vacationPkData.push(input.value);
                    }
                });
           yearInputs.forEach(input => {
           const year = input.id.replace('year', '');
           const vacationDays = input.value;

           vacationData[count] = vacationDays.trim() === "" ? 0 : parseInt(vacationDays);
           console.log("확인용sss:"+vacationData[count]);
           count++;
            });
            console.log(typeof(vacationPkData[0]));
            const requestData = {
                 memberNo: memberNo,
                 vacationData: vacationData,
                lessThanOneYear: lessThanOneYear,
                countVacation : countVacation,
                count : count,
                vacationPkData :vacationPkData
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
        }else {
             yearInputs.forEach(input => {
             const year = input.id.replace('year', '');
             const vacationDays = input.value;

             vacationData[year] = vacationDays.trim() === "" ? 0 : parseInt(vacationDays);
             console.log("확인용:"+vacationData[year]);
            });

            const requestData = {
                        memberNo: memberNo,
                        vacationData: vacationData,
                        lessThanOneYear: lessThanOneYear,
                        countVacation : countVacation,
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
        }



    });
});

//휴가 종류 생성
document.addEventListener('DOMContentLoaded', () => {
    const forms = document.getElementById("vacationTypeForm");
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    forms.addEventListener('submit', function(event) {

        event.preventDefault();

       const vacationType = document.getElementById('vacationType').value;
       const vacationValue = document.getElementById('vacationValue').value;

        fetch('/vacation/addTypeVacation', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
             body: JSON.stringify({
                vacationType:vacationType,
                vacationValue :vacationValue
             })
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
});

document.addEventListener('DOMContentLoaded', function () {
    // 수정 버튼 클릭 이벤트 처리
    document.querySelectorAll('.edit-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            const row = this.closest('tr'); // 클릭된 버튼이 속한 기본 행
            const editRow = row.nextElementSibling; // 숨겨진 수정용 행
            console.log(row);
            console.log(editRow);
            // 기본 행 숨기기
            row.style.display = 'none';

            // 수정 행 보이기
            editRow.style.display = 'table-row';

            editRow.querySelector('.cancel-btn').addEventListener('click', function () {
                // 기본 행 다시 보이기
                row.style.display = 'table-row';

                // 수정 행 숨기기
                editRow.style.display = 'none';
            });


        });
    });
});

document.addEventListener('DOMContentLoaded', function () {
 const csrfToken = document.querySelector('input[name="_csrf"]').value;
    document.querySelectorAll('.save-btn').forEach(function (button) {
        button.addEventListener('click', function () {

            const vacationTypeNo = document.getElementById('editVacationTypeNo').value;
            const vacationType = document.getElementById('editVacationTypeName').value;
            const vacationValue = document.getElementById('editVacationTypeCalculate').value;
            const countType = document.getElementById('countType').value;

            // 서버로 보낼 데이터 준비
            const data = {
                vacationTypeNo: vacationTypeNo,
                vacationType: vacationType,
                vacationValue: vacationValue,
                countType : countType
            };

            // Fetch API를 사용하여 데이터를 전송
            fetch('/vacation/addTypeVacation', {
                method: 'POST', // 또는 PUT, 백엔드에서 처리할 방식에 맞게 설정
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(data) // 데이터를 JSON으로 변환하여 전송
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('네트워크 응답이 올바르지 않습니다.');
            })
            .then(result => {
                console.log('저장 성공:', result);
                // 성공 후 필요한 처리 (예: 화면 갱신)
            })
            .catch(error => {
                console.error('저장 중 오류 발생:', error);
            });
        });
    });
});