let yearCount = 0;

function addYear() {
    yearCount++;

    const container = document.getElementById("vacationFormContainer");

    const inputGroup = document.createElement("div");
    inputGroup.className = "input-group";
    inputGroup.id = `year${yearCount}`;

    const label = document.createElement("label");
    label.textContent = `${yearCount}년차 `;
    inputGroup.appendChild(label);

    const input = document.createElement("input");
    input.type = "number";
    input.name = `vacationData[${yearCount}]`; // Key를 통해 서버로 전달
    input.min = "0";
    inputGroup.appendChild(input);

    container.appendChild(inputGroup);
}

function removeYear() {
    if (yearCount > 0) {
        const container = document.getElementById("vacationFormContainer");
        const lastInputGroup = document.getElementById(`year${yearCount}`);
        if (lastInputGroup) {
            container.removeChild(lastInputGroup);
        }

        yearCount--;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("vacationForm");
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출 동작을 막음

        const payload = new FormData(form);
        const memberNo = document.getElementById('memberNo').value; // 멤버 번호 가져오기
    console.log(memberNo);
        fetch('/vacation/addVacationAction', {
            method: 'POST',
            body: payload
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
});


let typeCount = 0;

function addType() {
    typeCount++;

    const container = document.getElementById("vacationTypeFormContainer");

    const inputGroup = document.createElement("div");
    inputGroup.className = "input-group";
    inputGroup.id = `type${typeCount}`;

    const labelName = document.createElement("label");
    labelName.textContent = `휴가 종류 이름 `;
    inputGroup.appendChild(labelName);

    const inputName = document.createElement("input");
    inputName.type = "text";
    inputName.name = `vacationType[${typeCount}][name]`; // Key를 통해 서버로 전달
    inputGroup.appendChild(inputName);

    const labelDesc = document.createElement("label");
    labelDesc.textContent = `휴가 설명 `;
    inputGroup.appendChild(labelDesc);

    const inputDesc = document.createElement("input");
    inputDesc.type = "text";
    inputDesc.name = `vacationType[${typeCount}][description]`; // Key를 통해 서버로 전달
    inputGroup.appendChild(inputDesc);

    container.appendChild(inputGroup);
}

function removeType() {
    if (typeCount > 0) {
        const container = document.getElementById("vacationTypeFormContainer");
        const lastInputGroup = document.getElementById(`type${typeCount}`);
        if (lastInputGroup) {
            container.removeChild(lastInputGroup);
        }

        typeCount--;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const vacationTypeForm = document.getElementById("vacationTypeForm");
    vacationTypeForm.addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출 동작을 막음

        const payload = new FormData(vacationTypeForm);
        const memberNo = document.getElementById('memberNo').value; // 멤버 번호 가져오기
    console.log(memberNo);
        fetch('/vacation/addVacationTypeAction', {
            method: 'POST',
            body: payload
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
});