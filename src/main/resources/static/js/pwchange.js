// 입력 시 다음으로
const frontInput = document.getElementById('national_number_front');
const midInput = document.getElementById('national_number_mid');
const backInput = document.getElementById('national_number_back');
const newPw = document.getElementById('new_password');


frontInput.addEventListener('input', function() {
    if (frontInput.value.length === 6) {
        midInput.focus();
    }
});

midInput.addEventListener('input', function() {
    if (midInput.value.length === 1) {
        backInput.focus();
    }
});

backInput.addEventListener('input', function() {
    if (backInput.value.length === 6) {
        newPw.focus();
    }
});

// 비밀번호 보이기 안보이기
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

// 비밀번호 변경 폼
const pwChangeFrm = document.getElementById('pwchangeFrm');

pwChangeFrm.addEventListener('submit',(e)=>{
	e.preventDefault();
	
	let vali_check = false;
	let vali_text = "";
	
	if(pwChangeFrm.user_id.value == ""){
		vali_text += '아이디(사번)을 입력하세요.';
		pwChangeFrm.user_id.focus();
	} else if(pwChangeFrm.national_number_front == ""){
		vali_text += '주민번호를 입력하세요.';
		form.national_number_front.focus();
	} else if(pwChangeFrm.national_number_back == ""){
		vali_text += '주민번호를 입력하세요.';
		pwChangeFrm.national_number_back.focus();
	} else if(pwChangeFrm.new_password == ""){
		vali_text += '새로운 비밀번호를 입력하세요.';
		pwChangeFrm.new_password.focus();
	} else {
		vali_check = true;
	}
	
	const csrfToken = document.getElementById('csrf_token').value;
	const userId = pwChangeFrm.user_id.value;
	const naNum1 = pwChangeFrm.national_number_front.value;
	const naNum2 = pwChangeFrm.national_number_back.value;
	const newpw = pwChangeFrm.new_password.value;
	if(vali_check == false){
		Swal.fire({
			icon : 'error',
			text : vali_text,
			confirmButtonColor: '#C0C0C0',
			confirmButtonText : "닫기"
		});
	} else{
		const formData = new FormData(pwChangeFrm);
		
		fetch('/pwchange',{
			method :'put',
			headers : {
				'X-CSRF-TOKEN' : csrfToken
			},
			body : formData
		})
		.then(response => response.json())
		.then(data => {
			console.log(data);
			if(data.res_code == '200'){
				Swal.fire({
				icon : 'success',
				text : data.res_msg,
				confirmButtonColor: '#C0C0C0',
				confirmButtonText : "닫기"
		}).then((result) => {
			location.href = ("/login");
		});
			} else{
				Swal.fire({
				icon : 'error',
				text : data.res_msg,
				confirmButtonColor: '#C0C0C0',
				confirmButtonText : "닫기"
			})
			}
		})
	}
})