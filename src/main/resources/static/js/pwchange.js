const frontInput = document.getElementById('registration-number-front');
const backInput = document.getElementById('registration-number-back');

frontInput.addEventListener('input', function() {
	if (frontInput.value.length === 6) {
		backInput.focus();
	}
});

backInput.addEventListener('input', function() {
	let value = backInput.value;
	if (value.length > 0) {
		backInput.value = value.charAt(0) + '*'.repeat(value.length - 1);
	} else {
		backInput.value = '';
	}
});