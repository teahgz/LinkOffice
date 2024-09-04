// 주소 api
    function sample4_execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {

                var roadAddr = data.roadAddress; 
                var extraRoadAddr = '';

                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraRoadAddr += data.bname;
                }
                if(data.buildingName !== '' && data.apartment === 'Y'){
                   extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if(extraRoadAddr !== ''){
                    extraRoadAddr = ' (' + extraRoadAddr + ')';
                }

                document.getElementById('sample4_postcode').value = data.zonecode;
                document.getElementById("sample4_roadAddress").value = roadAddr;
                document.getElementById("sample4_jibunAddress").value = data.jibunAddress;
                
                if(roadAddr !== ''){
                    document.getElementById("sample4_extraAddress").value = extraRoadAddr;
                } else {
                    document.getElementById("sample4_extraAddress").value = '';
                }

                var guideTextBox = document.getElementById("guide");
                if(data.autoRoadAddress) {
                    var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
                    guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
                    guideTextBox.style.display = 'block';

                } else if(data.autoJibunAddress) {
                    var expJibunAddr = data.autoJibunAddress;
                    guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
                    guideTextBox.style.display = 'block';
                } else {
                    guideTextBox.innerHTML = '';
                    guideTextBox.style.display = 'none';
                }
            }
        }).open();
    }
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
