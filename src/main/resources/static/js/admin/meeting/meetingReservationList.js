document.addEventListener('DOMContentLoaded', function() {
    var startDateInput = document.getElementById('startDate');
    var endDateInput = document.getElementById('endDate');
 
    function updateMinEndDate() {
        var startDate = startDateInput.value;
        if (startDate) {
            endDateInput.setAttribute('min', startDate);
        } else {
            endDateInput.removeAttribute('min');
        }
    }
 
    startDateInput.addEventListener('change', updateMinEndDate);
 
    updateMinEndDate();
    
    // 예약 등록일
    const dateElements = document.querySelectorAll('td');

    dateElements.forEach(element => {
        const dateStr = element.textContent.trim();
         
        const formattedDate = dateStr.substring(0, 10);

        element.textContent = formattedDate;
    });
});