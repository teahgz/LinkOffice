document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById('editModal');
    var addModal = document.getElementById('addModal');
    var span = document.getElementsByClassName('close');
    var editButtons = document.querySelectorAll('.btn-details'); 
    var createForm = document.getElementById("addForm");
    var addButton = document.getElementById('addMeetingButton');
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    
    const startInput = document.getElementById('meetingAvailableStart');
    const endInput = document.getElementById('meetingAvailDableEnd');

	const imageInput = document.getElementById('meetingImage');
    const imagePreview = document.getElementById('imagePreview');
            
	function roundToNearest30Minutes(timeStr) {
        const [hours, minutes] = timeStr.split(':').map(Number);
        const roundedMinutes = Math.round(minutes / 30) * 30;
        const adjustedHours = hours + Math.floor(roundedMinutes / 60);
        const adjustedMinutes = roundedMinutes % 60;

        return `${String(adjustedHours).padStart(2, '0')}:${String(adjustedMinutes).padStart(2, '0')}`;
    }

    function handleTimeChange(event) {
        const input = event.target;
        const originalValue = input.value;

        if (originalValue) {
            input.value = roundToNearest30Minutes(originalValue);
        }
    }
    
    startInput.addEventListener('change', handleTimeChange);
    endInput.addEventListener('change', handleTimeChange);
   
   // 이미지 미리보기ㅍ
   function previewImage(event) {
	    const file = event.target.files[0];
	    
	    if (file) {
	        const fileType = file.type;
	        const validTypes = ['image/png', 'image/jpeg'];
	        
	        if (validTypes.includes(fileType)) {
	            const reader = new FileReader();
	            reader.onload = function(e) {
	                imagePreview.src = e.target.result;
	                imagePreview.style.display = 'block';
	            }
	            reader.readAsDataURL(file);
	        } else {
	            Swal.fire({
	                icon: 'error',
	                title: '파일 형식 오류',
	                text: 'PNG 또는 JPG 파일만 등록 가능합니다.',
	                confirmButtonText: '확인'
	            });
	
	            // 입력 필드와 미리보기 초기화
	            event.target.value = "";
	            imagePreview.src = '';
	            imagePreview.style.display = 'none';
	        }
	    } else {
	        // 파일이 선택되지 않았을 경우 미리보기 숨김
	        imagePreview.src = '';
	        imagePreview.style.display = 'none';
	    }
	} 
	imageInput.addEventListener('change', previewImage);

            
    // 상세보기  
    editButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            var meetingId = this.getAttribute('data-id');

            $.ajax({
                type: 'GET',
                url: `/meetingroomList/${meetingId}`,
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.res_code === '200') {
                        var data = response;
                        document.getElementById('modal-meeting-id').value = data.meeting.meeting_no;
                        document.getElementById('modal-meeting-name').value = data.meeting.meeting_name;
                        document.getElementById('modal-meeting-max').value = data.meeting.meeting_max;
                        document.getElementById('modal-meeting-available-start').value = data.meeting.meeting_available_start;
                        document.getElementById('modal-meeting-available-end').value = data.meeting.meeting_available_end;
                        document.getElementById('modal-meeting-ori-image').value = data.meeting.meeting_ori_image;
                        document.getElementById('modal-meeting-new-image').value = data.meeting.meeting_new_image;
                        document.getElementById('modal-meeting-comment').value = data.meeting.meeting_comment;

                        modal.style.display = 'block';
                    } else {
                        Swal.fire('오류', response.res_msg, 'error');
                    }
                },
                error: function () {
                    Swal.fire('서버 오류', '서버와의 통신 중 오류가 발생했습니다.', 'error');
                }
            });
        });
    });

    // 등록 모달 열기
    addButton.onclick = function() {
        addModal.style.display = 'block';
    };
 
    // 모달 열기
    addButton.onclick = function() {
        addModal.style.display = 'flex';
    };

    // 모달 닫기 
	Array.from(span).forEach(function (el) {
	    el.onclick = function() {
	        Swal.fire({ 
	            text: '작성한 내용이 모두 초기화됩니다.',
	            icon: 'warning',
	            showCancelButton: true,
	            confirmButtonColor: '#B1C2DD',
	            cancelButtonColor: '#C0C0C0',
	            confirmButtonText: '확인',
	            cancelButtonText: '취소',
	        }).then((result) => {
	            if (result.isConfirmed) {
	                addModal.style.display = 'none';
	                resetModal(); // 초기화 함수 호출
	            }
	        });
	    };
	});

 
    // 모달 내용 리셋
    function resetModal() {
        createForm.reset();
        imagePreview.src = '';
        imagePreview.style.display = 'none';
    }
     
    startInput.addEventListener('change', function () {
	    var startTime = startInput.value; 
	    if (startTime) { 
	        endInput.disabled = false; 
	    } else { 
	        endInput.disabled = true;
	    }
	});

    
    // 회의실 등록
    createForm.addEventListener('submit', function (event) {
        event.preventDefault();  
        
        var meetingName = document.getElementById('meetingName').value;
        var meetingMax = document.getElementById('meetingMax').value;
        var meetingAvailableStart = document.getElementById('meetingAvailableStart').value;
        var meetingAvailableEnd = document.getElementById('meetingAvailableEnd').value;
        var meetingComment = document.getElementById('meetingComment').value;
        var meetingImage = document.getElementById('meetingImage').files[0];

        // 유효성 검사
        if (!meetingName) { 
            Swal.fire({ 
                text: '회의실 이름을 입력해 주세요.',
                icon: 'warning'
            });
            return;
        }
        if (!meetingMax) { 
            Swal.fire({ 
                text: '수용 인원을 입력해 주세요.',
                icon: 'warning'
            });
            return;
        }
        if (!meetingAvailableStart) { 
            Swal.fire({ 
                text: '이용 가능 시작 시간을 입력해 주세요.',
                icon: 'warning'
            });
            return;
        }
        if (!meetingAvailableEnd) { 
            Swal.fire({ 
                text: '이용 가능 마감 시간을 입력해 주세요.',
                icon: 'warning'
            });
            return; 
        } 
        if (meetingAvailableEnd <= meetingAvailableStart) { 
            Swal.fire({ 
                text: '이용 종료 시간은 이용 시작 시간 이후로 설정해주세요.',
                icon: 'warning'
            });
            return; 
        }
        if (!meetingComment) { 
            Swal.fire({ 
                text: '설명을 입력해 주세요.',
                icon: 'warning'
            });
            return; 
        }
        if (!meetingImage) { 
            Swal.fire({ 
                text: '이미지를 선택해 주세요.',
                icon: 'warning'
            });
            return; 
        }
 
        var formData = new FormData(createForm);
        formData.append('meetingImage', meetingImage);
 
        $.ajax({
            type: "POST",
            url: "/meetingroomList/add",
            data: formData,
            contentType: false,
            processData: false,
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            success: function (response) {
                if (response.res_code === "200") {
                    Swal.fire('등록 성공', response.res_msg, 'success').then(() => {
                        location.reload();
                    });
                } else {
                    Swal.fire('등록 실패', response.res_msg, 'error');
                }
            },
            error: function () {
                Swal.fire('서버 오류', '서버에서 오류가 발생했습니다.', 'error');
            }
        });
    }); 
  
});
