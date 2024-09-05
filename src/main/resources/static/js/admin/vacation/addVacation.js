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
