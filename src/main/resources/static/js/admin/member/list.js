document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.member_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const memberNo = this.getAttribute('data_member_no');
			
			window.location.href = '/admin/member/detail/'+memberNo;
		});
	});
});

document.getElementById('excelDownloadBtn').addEventListener('click', function () {
    const searchParams = new URLSearchParams(window.location.search);
    const url = '/admin/member/excelDownload?' + searchParams.toString(); 

    fetch(url, {
        method: 'get',
    })
    .then(response => response.blob()) 
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '사원리스트.xlsx';
        document.body.appendChild(a); 
        a.click();
        a.remove(); 
        window.URL.revokeObjectURL(url); 
    })
    .catch(error => {
        Swal.fire({
            icon: 'error',
            text: '파일 다운로드를 실패하였습니다.',
            confirmButtonColor: '#C0C0C0',
            confirmButtonText: '닫기'
        });
    });
});