document.addEventListener("DOMContentLoaded", function() {
    const vacationMemberNo = document.getElementById('attendance_memberNo').value;
    console.log(vacationMemberNo);
    fetch(`/user/vacationCount/${vacationMemberNo}`)
        .then(response => response.json())
        .then(data => {
              const vacationDaysElement = document.getElementById('vacationRemainCount');
              vacationDaysElement.textContent = `${data.remainingVacationDays}일`;
              if(data.date !== null){
                  const vacationYear = document.getElementById('vacationYear');
                  console.log(data.count);
                  vacationYear.textContent = `${data.date} 기준`;
              }
              if(data.yearSinceJoin < 1){

                  const vacationCount = document.getElementById('vacationCount');
                  const vacationTitle = document.getElementById('vacationTitle');
                  const vacationTitle2 = document.getElementById('vacationTitle2');
                  vacationTitle.style.display = "none";
                  vacationCount.style.display = "none";
                  vacationTitle2.textContent = "월차 개수";
              }
               const vacationCount = document.getElementById('vacationCount');
               vacationCount.textContent = `${data.count}일`;
        })
        .catch(error => {
            console.error('Error fetching vacation data:', error);
            document.getElementById('vacationDays').textContent = '남은 휴가 개수를 불러오지 못했습니다.';
        });
});