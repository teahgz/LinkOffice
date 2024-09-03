$(document).ready(function() {
	$('#pwHidden').on('click', function() {
		$(this).hide(); 
		$('#pwShow').show(); 
		$('#member_pw').attr('type', 'text'); 
	});

	$('#pwShow').on('click', function() {
		$(this).hide(); 
		$('#pwHidden').show(); 
		$('#member_pw').attr('type', 'password'); 
	});
});