// editor

import {
    ClassicEditor,
    AccessibilityHelp,
    Autosave,
    Bold,
    Essentials,
    FontBackgroundColor,
    FontColor,
    FontFamily,
    FontSize,
    Heading,
    ImageBlock,
    ImageInline,
    ImageToolbar,
    Italic,
    Link,
    List,
    Paragraph,
    SelectAll,
    Strikethrough,
    Table,
    TableCaption,
    TableCellProperties,
    TableColumnResize,
    TableProperties,
    TableToolbar,
    Underline,
    Undo
} from 'ckeditor5';

const editorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'fontSize',
            'fontFamily',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'link',
            'insertTable',
            '|',
            'bulletedList',
            'numberedList'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Autosave,
        Bold,
        Essentials,
        FontBackgroundColor,
        FontColor,
        FontFamily,
        FontSize,
        Heading,
        ImageBlock,
        ImageInline,
        ImageToolbar,
        Italic,
        Link,
        List,
        Paragraph,
        SelectAll,
        Strikethrough,
        Table,
        TableCaption,
        TableCellProperties,
        TableColumnResize,
        TableProperties,
        TableToolbar,
        Underline,
        Undo
    ],
    fontFamily: {
        options: [
            '돋움, Dotum, sans-serif',
            '굴림, Gulim, sans-serif',
            '바탕, Batang, serif',
            '나눔고딕, NanumGothic, sans-serif'
        ],
        supportAllValues: true
    },
    fontSize: {
        options: [10, 12, 14, '16', 18, 20, 22],
        supportAllValues: true
    },
    heading: {
        options: [
            { model: 'paragraph', title: '굵기', class: 'ck-heading_paragraph' },
            { model: 'heading1', view: 'h1', title: '굵기 1', class: 'ck-heading_heading1' },
            { model: 'heading2', view: 'h2', title: '굵기 2', class: 'ck-heading_heading2' },
            { model: 'heading3', view: 'h3', title: '굵기 3', class: 'ck-heading_heading3' },
            { model: 'heading4', view: 'h4', title: '굵기 4', class: 'ck-heading_heading4' },
            { model: 'heading5', view: 'h5', title: '굵기 5', class: 'ck-heading_heading5' },
            { model: 'heading6', view: 'h6', title: '굵기 6', class: 'ck-heading_heading6' }
        ]
    },
    image: {
        toolbar: ['imageTextAlternative']
    },
    table: {
        contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
    }
};

// 시작 기간 선택 전 disable

document.addEventListener("DOMContentLoaded", function() {
    const startDateInput = document.getElementById("vacationapproval_start_date");
    const endDateInput = document.getElementById("vacationapproval_end_date");
    const dateCountInput = document.getElementById("vacationapproval_date_count");

    endDateInput.disabled = true;

    startDateInput.addEventListener("change", function() {
        if (startDateInput.value) {
            endDateInput.disabled = false; 
            calculateDateDifference(); 
        } else {
            endDateInput.disabled = true; 
            endDateInput.value = '';
            dateCountInput.value = ''; 
        }
    });
    
    endDateInput.addEventListener("change", function() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);

        if (startDate && endDate && endDate < startDate) {
            Swal.fire({
                icon: 'warning',
                text: "종료일은 시작일보다 이전일 수 없습니다.",
                confirmButtonColor: '#B1C2DD',
                confirmButtonText: "확인"
            });
            endDateInput.value = '';
            dateCountInput.value = ''; 
        } else {
            calculateDateDifference(); 
        }
    });

    function calculateDateDifference() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);

        if (startDate && endDate && endDate >= startDate) {
            const timeDifference = endDate - startDate;
            const daysDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24)) + 1;
            dateCountInput.value = daysDifference;
        } else {
            dateCountInput.value = ''; 
        }
    }
    
    
       document.getElementById('vacationapproval_file').addEventListener('change', function (event) {
        const vacationFile = event.target.files[0]; 
        
        if (vacationFile) {
            const fileType = vacationFile.type;
            const validTypes = [
                'image/png', 
                'image/jpeg', 
                'application/pdf', 
                'application/vnd.ms-excel', 
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            ];

            if (!validTypes.includes(fileType)) {
                Swal.fire({
                    icon: 'error',
                    text: 'PNG, JPG, PDF, EXCEL만 등록 가능합니다.',
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: '확인'
                });
                event.target.value = ''; 
            }
        }
    });
    	
    
    
});



ClassicEditor.create(document.querySelector('#editor'), editorConfig)
    .then(editor => {
		
        editor.ui.view.editable.element.style.height = '500px';

			const form = document.getElementById('vacAppCreateFrm');
			form.addEventListener('submit', (e) => {
	  		  e.preventDefault();
	    // 수정 폼
/*		    const editorData = editor.getData();
		    const vacationapprovalTitle = document.querySelector('#vacationapproval_title').value;
		    const vacationtype = document.querySelector('select[name="vacationtype"]').value;
		    const csrfToken = document.querySelector('#csrf_token').value;
		    const memberNo = document.querySelector('#member_no').value;
		    const startDate = document.querySelector('#vacationapproval_start_date').value;
		    const endDate = document.querySelector('#vacationapproval_end_date').value;
		    const dateCount = document.querySelector('#vacationapproval_date_count').value;
		    const vacationFile = document.querySelector('#vacationapproval_file').files[0];
			const approvers = Array.from(document.querySelectorAll('input[id="approverNumbers"]')).map(input => input.value);
			const references = Array.from(document.querySelectorAll('input[id="referenceNumbers"]')).map(input => input.value);
			const reviewers = Array.from(document.querySelectorAll('input[id="reviewerNumbers"]')).map(input => input.value);

			console.log(approvers);
			console.log(references);
			console.log(reviewers);
		
		 	let vali_check = false;
            let vali_text = "";
			
            if (vacationapprovalTitle.trim() === "") {  
                vali_text += '결재 제목을 입력해주세요.';
                document.querySelector('#vacationapproval_title').focus();
            } else if(startDate.trim() === ""){
				 vali_text += '휴가 기간을 입력해주세요.';
                document.querySelector('#vacationapproval_start_date').focus();
			} else if(endDate.trim() === ""){
				 vali_text += '휴가 기간을 입력해주세요.';
                document.querySelector('#vacationapproval_end_date').focus();
			} else {
                vali_check = true;
            }
			
			if (vali_check == false) {
                Swal.fire({
                    icon: 'error',
                    text: vali_text,
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: "확인"
                });
            } else {
				const payload = new FormData();
			    payload.append('vacationapprovalContent', editorData);
			    payload.append('vacationapprovalTitle', vacationapprovalTitle);
			    payload.append('vacationtype', vacationtype);
			    payload.append('memberNo', memberNo);
			    payload.append('startDate', startDate);
			    payload.append('endDate', endDate);
			    payload.append('dateCount', dateCount);
			    
			    if (vacationFile) {
           			payload.append('vacationFile', vacationFile);
        		}
			    payload.append('approvers', approvers);
			    payload.append('references', references);
			    payload.append('reviewers', reviewers);
			
			    fetch('/employee/vacationapproval/create', {
			        method: 'post',
			        body: payload,
			        headers: {
			            'X-CSRF-TOKEN': csrfToken
			        }
			    })
			    .then(response => response.json())
			    .then(data => {
			        if (data.res_code == '200') {
			            Swal.fire({
			                icon: 'success',
			                text: data.res_msg,
			                confirmButtonColor: '#B1C2DD',
			                confirmButtonText: "확인"
			            }).then((result) => {
			                location.href = "/employee/vacationapproval/list";
			            });
			        } else {
			            Swal.fire({
			                icon: 'error',
			                text: data.res_msg,
			                confirmButtonColor: '#B1C2DD',
			                confirmButtonText: "확인"
			            });
			        }
			    })
			} */
	});


});
