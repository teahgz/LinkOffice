document.addEventListener("DOMContentLoaded", function() {
    const vacationMemberNo = document.getElementById('attendance_memberNo').value;

    fetch(`/user/vacationCount/${vacationMemberNo}`)
        .then(response => response.json())
        .then(data => {
              const vacationDaysElement = document.getElementById('vacationRemainCount');
              vacationDaysElement.textContent = `${data.remainingVacationDays}일`;

               const vacationCount = document.getElementById('vacationCount');
               vacationCount.textContent = `${data.count}일`;
        })
        .catch(error => {
            console.error('Error fetching vacation data:', error);
            document.getElementById('vacationDays').textContent = '남은 휴가 개수를 불러오지 못했습니다.';
        });
});