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

// 전화번호 
document.addEventListener('DOMContentLoaded', function() {
    const mobile1 = document.getElementById('mobile1');
    const mobile2 = document.getElementById('mobile2');
    const mobile3 = document.getElementById('mobile3');
	const internal1 = document.getElementById('internal1');
	
    mobile1.addEventListener('input', function() {
        if (mobile1.value.length === 3) {
            mobile2.focus();
        }
    });

    mobile2.addEventListener('input', function() {
        if (mobile2.value.length === 4) {
            mobile3.focus();
        }
    });
    
    mobile3.addEventListener('input',function(){
		if(mobile3.value.length === 4){
			internal1.focus();
		}
	});
});

// 이미지 등록 
function previewImage(event) {
    const file = event.target.files[0];

    if (file) {
        const fileType = file.type;
        const validTypes = ['image/png', 'image/jpeg'];

        if (validTypes.includes(fileType)) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.getElementById('profileImg');
                img.src = e.target.result;
            }

            reader.readAsDataURL(file);
        } else {
            Swal.fire({
                icon: 'error',
                title: '파일 형식 오류',
                text: 'PNG 또는 JPG 파일만 등록 가능합니다.',
                confirmButtonText: '확인'
            });

            document.getElementById('profile_image').value = "";
        }
    }
}


