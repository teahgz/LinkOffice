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
    	
    
    
});

ClassicEditor.create(document.querySelector('#editor'), editorConfig)
     .then(editor => {
        editor.ui.view.editable.element.style.height = '500px';
	
			// 양식 불러오기
			document.getElementById('approval_form').addEventListener('change', function() {
			    const selectedFormContent = this.options[this.selectedIndex].getAttribute('data-content');
			
			    if (selectedFormContent) {
			        editor.setData(selectedFormContent);
			    } else {
			        editor.setData('');
			    }
			});
			
			// 미리보기
			document.getElementById('previewButton').addEventListener('click', function() {
		    const editorContent = editor.getData();
		    
		    const approvalLineContent = document.getElementById('approvalLineTable').outerHTML;
		    var windowW = 1000;
		    var windowH = 900;
		    var winHeight = document.body.clientHeight;
		    var winWidth = document.body.clientWidth - 500;
		    var winX = window.screenX || window.screenLeft || 500;
		    var winY = window.screenY || window.screenTop || 0;
		    var popX = winX + (winWidth - windowW) / 2;
		    var popY = winY + (winHeight - windowH) / 2;
		    
		    const previewWindow = window.open('', '미리보기', "width=" + windowW + ", height=" + windowH + ", scrollbars=no, menubar=no, top=" + popY + ", left=" + popX);
		    previewWindow.document.write('<html><head><title>전자결재 미리보기</title>');
		    
		    previewWindow.document.write(`
		        <style>
		            html, body {
		                justify-content: center;
		                display: flex;
		                align-content: center;
		                margin: 0;
		                padding: 0;
		                align-items: center;
		            }
		            .approval_line_section {
		                margin-top: 20px;
		            }
		            .approval_line_table {
		                width: 100%;
		                border-collapse: collapse;
		                margin-bottom: 20px;
		            }
		            .approval_line_table th, .approval_line_table td {
		                border: 1px solid #ccc;
		                padding: 10px;
		                text-align: center;
		                vertical-align: middle;
		            }
		            .approval_line_table td {
		                min-height: 50px; 
		                width: 100px; 
		                vertical-align: top; 
		            }
		            .approval_line_table th {
		                background-color: #f2f2f2;
		                font-weight: bold;
		                width: 100px;
		            }
		            #strong {
		                background-color: #f2f2f2;
		            }
		            .signature_box {
		                display: flex;
		                flex-direction: column;
		                align-items: center;
		                justify-content: center;
		                height: 60px;
		            }
		            .ck-table-resized {
		                width: 100%;
		                border-collapse: collapse;
		                margin-bottom: 20px;
		            }
		            .ck-table-resized th, .ck-table-resized td {
		                border: 1px solid #ccc;
		                padding: 10px;
		                text-align: center;
		                vertical-align: middle;
		            }
		            .ck-table-resized th {
		                background-color: #f2f2f2;
		                font-weight: bold;
		            }
		            .preview_div{
		                padding: 20px;
		                width: 800px;
		            }
		            .download_button {
						float: right;
					    padding: 8px 15px;
					    background-color: #B3C7EE;
					    border: none;
					    border-radius: 5px;
					    cursor: pointer;
					    text-decoration: none;
					    font-size: 14px;
					    color: #000000;
		            }
		        </style>
		    `);
		    
		    previewWindow.document.write(`
		        <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
		        <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.0/jspdf.umd.min.js"></script>
		    `);
		    previewWindow.document.write('</head><body>');
		    previewWindow.document.write('<div class="preview_div">');
		    previewWindow.document.write('<button class="download_button" onclick="downloadPDF()">PDF 다운로드</button>');
		    previewWindow.document.write('<h2>결재선</h2>');
		    previewWindow.document.write(approvalLineContent); 
		    previewWindow.document.write('<hr>');
		    previewWindow.document.write('<div>' + editorContent + '</div>');    
		    previewWindow.document.write('</div>');
		    previewWindow.document.write('</body></html>');
		    previewWindow.document.close();
		
		    previewWindow.downloadPDF = function() {
		        previewWindow.html2canvas(previewWindow.document.querySelector('.preview_div'), { scale: 2 })
		        .then(canvas => {
		            const imgData = canvas.toDataURL('image/png');
		            const { jsPDF } = previewWindow.jspdf;
		            const pdf = new jsPDF({
		                orientation: 'portrait',
		                unit: 'mm',
		                format: 'a4'
		            });
		            const imgWidth = 210;
		            const imgHeight = canvas.height * imgWidth / canvas.width;
		            pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight);
		            pdf.save('결재문서.pdf');
		        }).catch(error => {
		            console.error('PDF 생성 중 오류 발생:', error);
		        });
		    };
		});


			// 등록 폼
			
			const form = document.getElementById('appCreateFrm');
			form.addEventListener('submit', (e) => {
	  		  e.preventDefault();
	    
		    const approvalTitle = document.querySelector('#approval_title').value;
		    const editorData = editor.getData();
		    const csrfToken = document.querySelector('#csrf_token').value;
		    const file = document.querySelector('#approval_file').files[0];
			const approvers = Array.from(document.querySelectorAll('input[id="approverNumbers"]')).map(input => input.value);
			const references = Array.from(document.querySelectorAll('input[id="referenceNumbers"]')).map(input => input.value);
			const reviewers = Array.from(document.querySelectorAll('input[id="reviewerNumbers"]')).map(input => input.value);

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
			
			    fetch('/employee/approval/create', {
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
			            	location.href = "/employee/approval/progress";
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
