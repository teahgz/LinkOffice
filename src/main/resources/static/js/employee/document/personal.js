// 조회한 폴더리스트를 담을 배열 
let folderList = [];

$(function () {
    // memberNo 받아오기 
    var memberNo = document.getElementById("mem_no").textContent;

    // 페이지당 리스트 10개, 페이징 요소들 넣을 영역 지정 
    const pageSize = 10; 
    const paginationDiv = document.getElementById('pagination');
    let totalPages = 0;
    let currentPage = 0;

    // 날짜 포맷 함수
    function formatDate(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

    // 폴더 리스트 받아오기
    function getJson() {
        $.ajax({
            type: 'GET',
            url: '/personal/folder',
            data: { memberNo: memberNo },
            dataType: 'json',
            success: function(data) {
				// 폴더리스트 초기화 
                folderList = []; 
				console.log(data);
                $.each(data, function(idx, item) {
                    folderList.push({
						// 폴더 번호, 부모폴더가 있는지, 폴더 이름을 배열에 담음 
                        id: item.document_folder_no,
                        parent: item.document_folder_parent_no ? item.document_folder_parent_no : '#',
                        text: item.document_folder_name
                    });
                });

                // 폴더 리스트가 존재할 때
                if (folderList.length > 0) {
                    $('#tree').show();
                    $('#tree').jstree({
                        'core': {
                            'data': folderList,
                            'animation': 0
                        },
                        'themes': {
                            'name': 'proton',
                            'responsive': true,
                            'dots': false
                        },
                        'types': {
                            'default': {
                                'icon': 'fa fa-folder'
                            }
                        },
                        'plugins': ['wholerow', 'types']
                    });

                    $('#tree').on('changed.jstree', function (e, data) {
						// 현재페이지를 1페이지로 리셋 
                        currentPage = 0; 
                        const selectedFolderId = data.node.id;
                        // 폴더 이름 출력을 위한 함수 
                        updateFolderName(selectedFolderId); 
                        // 폴더 안에 든 파일을 불러올 함수 
                        loadFiles(selectedFolderId);
                    });
					// 폴더가 존재하면 폴더를 선택하세요 메시지 띄우기 
                    $('.document_no_folder').hide();
                    $('.document_select_folder').show();
                } else {
					// 폴더가 없으면 폴더 생성 버튼 띄우기 
                    $('#tree').hide();
                    $('.document_no_folder').show();
                }
            }
        });
    }

    // 선택된 폴더의 이름을 출력 
    function updateFolderName(folderId) {
        // folderList에서 폴더 이름을 찾아서 div에 추가 
        const folderName = folderList.find(f => f.id == folderId);
        if (folderName) {
            const folderNameDiv = document.getElementById('folder_name');
            folderNameDiv.innerHTML = folderName.text; 
        }
    }

    // 선택된 폴더의 파일 목록을 불러오기
    function loadFiles(folderId) {
        $.ajax({
            type: 'GET',
            url: '/personal/file',
            data: {
                memberNo: memberNo,
                folderId: folderId
            },
            dataType: 'json',
            success: function(data) {
                // 정렬 기준 가져오기
                const sortOption = $('#sort_select').val();
                const fileList = data;

                // 정렬
                if (sortOption === 'latest') {
                    fileList.sort((a, b) => new Date(b.document_file_upload_date) - new Date(a.document_file_upload_date));
                } else if (sortOption === 'oldest') {
                    fileList.sort((a, b) => new Date(a.document_file_upload_date) - new Date(b.document_file_upload_date));
                }
                
                // 몇 페이지인지 계산 
                totalPages = Math.ceil(fileList.length / pageSize);
                const fileTableBody = document.getElementById('file_table_body');
                fileTableBody.innerHTML = '';

                // 파일 목록이 존재할 때
                if (fileList.length > 0) {
                    $('.document_select_folder').hide();
                    $('.document_file_list').show();

                    // 한 페이지에 10개씩 추가 
                    const start = currentPage * pageSize;
                    const end = Math.min(start + pageSize, fileList.length);
                    const paginatedFiles = fileList.slice(start, end);

                    paginatedFiles.forEach(file => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${file.document_ori_file_name}</td>
                            <td>${formatDate(file.document_file_upload_date)}</td>
                            <td><input type="button" class="file_show_button" value="파일보기"></td>
                            <td>${file.document_file_size}</td>
                            <td><input type="button" class="file_down_button" value="다운로드"></td>
                            <td><input type="button" class="delete_button" value="삭제"></td>
                        `;
                        fileTableBody.appendChild(row);
                    });

                    // 리스트가 10개가 안 된다면 빈 행 추가로 10개 만들기 
                    const emptyRows = pageSize - paginatedFiles.length;
                    for (let i = 0; i < emptyRows; i++) {
                        const emptyRow = document.createElement('tr');
                        emptyRow.innerHTML = `
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        `;
                        fileTableBody.appendChild(emptyRow);
                    }

                    // 페이징 업데이트 
                    updatePagination();
                } else {
                    $('.document_file_list').show();
                    $('.document_select_folder').hide();
                    fileTableBody.innerHTML = '<tr><td colspan="6">파일 목록이 존재하지 않습니다.</td></tr>';

                    // 페이징 버튼 숨기기
                    paginationDiv.innerHTML = '';

                }
            }
        });
    }

	// 페이징 버튼 업데이트 
    function updatePagination() {
        paginationDiv.innerHTML = '';

        // 총 페이지 수가 1일 때
        if (totalPages <= 1) {
            const pageButton = document.createElement('span');
            pageButton.className = 'pagination_button active';
            pageButton.textContent = '1';
            paginationDiv.appendChild(pageButton);
            return;
        }

        // 처음 페이지 버튼 (<<)
        if (currentPage > 0) {
            const firstButton = document.createElement('span');
            firstButton.className = 'go_first_page_button';
            firstButton.textContent = '<<';
            firstButton.onclick = () => {
                currentPage = 0;
                loadFiles($('#tree').jstree('get_selected')[0]); 
            };
            paginationDiv.appendChild(firstButton);
        }

        // 이전 페이지 버튼 (<)
        if (currentPage > 0) {
            const prevButton = document.createElement('span');
            prevButton.className = 'pagination_button';
            prevButton.textContent = '<';
            prevButton.onclick = () => {
                currentPage--;
                loadFiles($('#tree').jstree('get_selected')[0]); 
            };
            paginationDiv.appendChild(prevButton);
        }

        // 페이지 번호 버튼 (최대 3개 표시)
        let startPage = Math.max(0, currentPage - 1);
        let endPage = Math.min(totalPages - 1, currentPage + 1);

        if (currentPage < 1) {
            endPage = Math.min(totalPages - 1, endPage + 1);
        } else if (currentPage > totalPages - 2) {
            startPage = Math.max(0, startPage - 1);
        }

        for (let page = startPage; page <= endPage; page++) {
            const pageButton = document.createElement('span');
            pageButton.className = `pagination_button ${page === currentPage ? 'active' : ''}`;
            pageButton.textContent = page + 1;
            pageButton.onclick = () => {
                currentPage = page;
                loadFiles($('#tree').jstree('get_selected')[0]);
            };
            paginationDiv.appendChild(pageButton);
        }

        // 다음 페이지 버튼 (>)
        if (currentPage < totalPages - 1) {
            const nextButton = document.createElement('span');
            nextButton.className = 'pagination_button';
            nextButton.textContent = '>';
            nextButton.onclick = () => {
                currentPage++;
                loadFiles($('#tree').jstree('get_selected')[0]); 
            };
            paginationDiv.appendChild(nextButton);
        }

        // 마지막 페이지 버튼 (>>)
        if (currentPage < totalPages - 1) {
            const lastButton = document.createElement('span');
            lastButton.className = 'go_last_page_button';
            lastButton.textContent = '>>';
            lastButton.onclick = () => {
                currentPage = totalPages - 1;
                loadFiles($('#tree').jstree('get_selected')[0]); 
            };
            paginationDiv.appendChild(lastButton);
        }
    }

    // 페이지가 로드될 때 폴더 리스트를 불러옴
    $(document).ready(function() {
        getJson();
        
        // 정렬 선택이 변경될 때 파일 목록을 다시 불러옴
        $('#sort_select').on('change', function() {
            const selectedFolderId = $('#tree').jstree('get_selected')[0];
            loadFiles(selectedFolderId);
        });
    });
});