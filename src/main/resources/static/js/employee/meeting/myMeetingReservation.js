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
});