// 연차 개수 제출 시 처리
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("vacationForm");
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        Swal.fire({
            title: '확인',
            text: '입력되지 않은 연차의 개수는 0으로 지정됩니다. 계속하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#B1C2DD',
            cancelButtonColor: '#EEB3B3',
            confirmButtonText: '확인',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                // 사용자가 확인했을 때 처리 진행
                const memberNo = document.getElementById('memberNo').value;
                const yearInputs = document.querySelectorAll('[id^="year"]');
                const countVacation = document.getElementById('countVacation').value;
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

                        // 0으로 지정
                        vacationData[count] = vacationDays.trim() === "" ? 0 : parseInt(vacationDays);
                        count++;
                    });

                    const requestData = {
                        memberNo: memberNo,
                        vacationData: vacationData,
                        lessThanOneYear: lessThanOneYear,
                        countVacation: countVacation,
                        count: count,
                        vacationPkData: vacationPkData
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
                } else {
                    yearInputs.forEach(input => {
                        const year = input.id.replace('year', '');
                        const vacationDays = input.value;

                        vacationData[year] = vacationDays.trim() === "" ? 0 : parseInt(vacationDays);
                    });

                    const requestData = {
                        memberNo: memberNo,
                        vacationData: vacationData,
                        lessThanOneYear: lessThanOneYear,
                        countVacation: countVacation
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
            }
        });
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
//휴가 카테고리 수정
document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    document.querySelectorAll('.save-btn').forEach(function (button) {
        button.addEventListener('click', function () {

            const form = button.closest('form');
            if (!form) return;

            const vacationTypeNo = form.querySelector('#editVacationTypeNo').value;
            const vacationTypeName = form.querySelector('#editVacationTypeName').value;
            const vacationTypeCalculate = form.querySelector('#editVacationTypeCalculate').value;

            const data = {
                vacationTypeNo: vacationTypeNo,
                vacationTypeName: vacationTypeName,
                vacationTypeCalculate: vacationTypeCalculate
            };
            // Fetch API를 사용하여 데이터를 전송
            fetch('/vacation/updateVacation', {
                method: 'POST', // 또는 PUT, 백엔드에서 처리할 방식에 맞게 설정
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(data) // 데이터를 JSON으로 변환하여 전송
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

        });
    });
});

/*삭제버튼-상태값 변화*/
document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    document.querySelectorAll('.delete-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            const row = button.closest('tr');
            if (!row) return;

            const vacationTypeNo = row.querySelector('#vacationTypeNo').value;
            const vacationTypeName = row.querySelector('#vacationTypeName').value;
            const vacationTypeCal = row.querySelector('#vacationTypeCal').value;

            fetch('/vacation/deleteVacation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    vacationTypeNo : vacationTypeNo,
                    vacationTypeName : vacationTypeName,
                    vacationTypeCal : vacationTypeCal
                })
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
                            row.remove(); // 성공 시 행 제거
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
            .catch(() => {
                Swal.fire({
                    icon: 'error',
                    title: '오류 발생',
                    text: '서버와의 통신 중 오류가 발생했습니다.',
                    confirmButtonText: "닫기"
                });
            });
        });
    });
});

//1년미만 여부
document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    document.getElementById('oneUnderForm').addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출을 막음

        Swal.fire({
            title: '정말 등록하시겠습니까?',
            text: "이 작업은 취소할 수 없습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#B1C2DD',
            cancelButtonColor: '#EEB3B3',
            confirmButtonText: '네, 등록합니다',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                // 사용자가 확인 버튼을 눌렀을 때 진행
                const isChecked = document.getElementById('lessThanOneYear').checked;

                fetch('/vacation/checkOneYear', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify({
                        isChecked: isChecked
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
            }
        });
    });
});
