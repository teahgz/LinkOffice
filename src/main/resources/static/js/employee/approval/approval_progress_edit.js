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
  
       document.getElementById('approval_file').addEventListener('change', function (event) {
        const file = event.target.files[0]; 
        
        if (file) {
            const fileType = file.type;
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

// 결재자 값 가져오기
const appNo = document.getElementById('approval_no').value;

$.ajax({
    url: '/employee/approval/approve/' + appNo,
    type: 'get',
    dataType: 'json',
    success: function(data) {
        console.log(data); 
        console.log(data.approvaldto.flows); 

        const filteredApprovers = data.approvaldto.flows.filter(approver => approver.approval_flow_role === 2);

        console.log(filteredApprovers);

        populateApprovalLine(filteredApprovers);
    }
});

function populateApprovalLine(approvers) {
    const table = document.getElementById('approvalLineTable');
    const positionCells = table.querySelectorAll('tr:nth-child(1) td');
    const nameCells = table.querySelectorAll('tr:nth-child(3) td');

    approvers.forEach((approver, index) => {
        if (index < positionCells.length - 1) {
            positionCells[index + 1].innerHTML = `<span>${approver.member_position}</span>`;
            nameCells[index + 1].innerHTML = `<span>${approver.member_name}</span>`;
        }
    });
}


});

ClassicEditor.create(document.querySelector('#editor'), editorConfig)
     .then(editor => {
        editor.ui.view.editable.element.style.height = '500px';
			
			// 수정 폼
			const form = document.getElementById('appEditFrm');
			form.addEventListener('submit', (e) => {
	  		  e.preventDefault();
	    
		    const approvalTitle = document.querySelector('#approval_title').value;
		    const editorData = editor.getData();
		    const csrfToken = document.querySelector('#csrf_token').value;
		    const appNo = document.querySelector('#approval_no').value;
		    const file = document.querySelector('#approval_file').files[0];
			const approvers = Array.from(document.querySelectorAll('input[id="approverNumbers"]')).map(input => input.value);
			const references = Array.from(document.querySelectorAll('input[id="referenceNumbers"]')).map(input => input.value);
			const reviewers = Array.from(document.querySelectorAll('input[id="reviewerNumbers"]')).map(input => input.value);

			console.log(approvers);
			console.log(references);
			console.log(reviewers);
		
		 	let vali_check = false;
            let vali_text = "";
			
            if (approvalTitle.trim() === "") {  
                vali_text += '결재 제목을 입력해주세요.';
                document.querySelector('#approval_title').focus();
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
			    payload.append('approvalTitle', approvalTitle);
			    payload.append('approvalContent', editorData);
			    if (file) {
           			payload.append('file', file);
        		}
			    payload.append('approvers', approvers);
			    payload.append('references', references);
			    payload.append('reviewers', reviewers);
			
			    fetch('/employee/approval/edit/'+appNo, {
			        method: 'put',
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
			            	location.href = "/employee/approval/approval_progress_detail/"+appNo;
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
			}
	});
	
	
    
});
