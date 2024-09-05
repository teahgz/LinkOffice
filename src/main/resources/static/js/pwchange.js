// 주민등록번호 

const frontInput = document.getElementById('registration_number_front');
const backInput = document.getElementById('registration_number_back');

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
// 비밀번호 
$(document).ready(function() {
	$('#pwHidden').on('click', function() {
		$(this).hide(); 
		$('#pwShow').show(); 
		$('#new_password').attr('type', 'text'); 
	});

	$('#pwShow').on('click', function() {
		$(this).hide(); 
		$('#pwHidden').show(); 
		$('#new_password').attr('type', 'password'); 
	});
});