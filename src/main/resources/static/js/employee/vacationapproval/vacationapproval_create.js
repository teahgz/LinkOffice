
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
});



ClassicEditor.create(document.querySelector('#editor'), editorConfig)
    .then(editor => {
		
        editor.ui.view.editable.element.style.height = '500px';

            editor.setData(`
            <h1><span style="font-family:나눔고딕, NanumGothic, sans-serif;"><strong>휴가 신청서</strong> </span></h1>
            <p>&nbsp;</p>
            <figure class="table" style="float:left;width:35.29%;">
            <table class="ck-table-resized">
            <colgroup><col style="width:34.74%;"><col style="width:65.26%;"></colgroup>
            <tbody>
            <tr><th>부서&nbsp;</th><td>&nbsp;</td></tr>
            <tr><th>성명</th><td>&nbsp;</td></tr>
            </tbody>
            </table>
            </figure>
            <figure class="table" style="float:left;width:35.29%;">
            <table class="ck-table-resized">
            <colgroup><col style="width:34.74%;"><col style="width:65.26%;"></colgroup>
            <tbody>
            <tr><th>직위</th><td style="text-align:right;">&nbsp;</td></tr>
            <tr><th>휴가구분</th><td>&nbsp;</td></tr>
            </tbody>
            </table>
            </figure>
            <p>&nbsp;</p><p>&nbsp;</p>
            <p>&nbsp;</p>
            <figure class="table" style="float:left;width:70.62%;">
            <table class="ck-table-resized">
            <colgroup><col style="width:17.35%;"><col style="width:82.65%;"></colgroup>
            <tbody>
            <tr><th>휴가 기간</th><td>&nbsp;</td></tr>
            </tbody>
            </table>
            </figure>
            <figure class="table" style="float:left;width:70.62%;">
            <table class="ck-table-resized">
            <colgroup><col style="width:17.35%;"> <col style="width:82.65%;"></colgroup>
            <tbody>
            <tr><th>사유</th><td>&nbsp;</td></tr>
            </tbody>
            </table>
            </figure>
            <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;상가 본인은 위와 같이 휴가 신청서를 제출 합니다.</p><p>&nbsp;</p><p>&nbsp;</p>`);

        document.querySelector('.submit_button').addEventListener('click', (e) => {
            e.preventDefault();
           
		 	const editorData = editor.getData();
		    const vacationapprovalTitle = document.querySelector('#vacationapproval_title').value;
		    const vacationtype = document.querySelector('select[name="vacationtype"]').value;
		    const csrfToken = document.querySelector('#csrf_token').value;
		    const memberNo = document.querySelector('#member_no').value;
		    const startDate = document.querySelector('#vacationapproval_start_date').value;
		    const endDate = document.querySelector('#vacationapproval_end_date').value;
		    const dateCount = document.querySelector('#vacationapproval_date_count').value;
		
		
			console.log(editorData);
			console.log(vacationapprovalTitle);
			console.log(vacationtype);
			console.log(memberNo);
			console.log(startDate);
			console.log(endDate);
			console.log(dateCount);
		    let vali_check = false;
		    let vali_text = "";
		
		    if (vacationapprovalTitle.trim() === "") {
		        vali_text += '휴가 제목을 입력해주세요.';
		        document.querySelector('#vacationapproval_title').focus();
		    } else if (vacationtype === "") {
		        vali_text += '휴가 종류를 선택해주세요.';
		        document.querySelector('select[name="vacationtype"]').focus();
		    } else if (startDate === "") {
		        vali_text += '휴가 기간을 선택해주세요.';
				document.querySelector('#vacationapproval_end_date').focus();
		    } else if (endDate === "") {
		        vali_text += '휴가 기간을 선택해주세요.';
				document.querySelector('#vacationapproval_start_date').focus();
		    }else if (dateCount === "") {
		        vali_text += '휴가 일수를 입력해주세요.';
		        document.querySelector('#vacationapproval_date_count').focus();
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
                 const requestData = {
			            vacation_approval_title: vacationapprovalTitle,
			            vacation_type_no: vacationtype,
			            vacation_approval_start_date: startDate,
			            vacation_approval_end_date: endDate,
			            vacation_approval_total_days: dateCount,
			            vacation_approval_content: editorData,
			            member_no: memberNo
			        };

                fetch('/employee/vacationapproval/create', {
                    method: 'post',
                    body: JSON.stringify(requestData),
                    headers: {
						'Content-Type': 'application/json',
                        'Accept': 'application/json',
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
                            console.log("성공");
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            text: data.res_msg,
                            confirmButtonColor: '#B1C2DD',
                            confirmButtonText: "확인"
                        });
                    }
                });  
            }
        });
    });
