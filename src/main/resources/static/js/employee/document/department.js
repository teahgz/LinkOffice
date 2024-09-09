$(function () {
    // jsTree 인스턴스 생성
    $('#jstree').jstree();

    // 상위 폴더 체크박스 선택하면 하위폴더 체크박스도 선택됨
    $('#jstree').on('change', 'input[type="checkbox"]', function () {
        var isChecked = $(this).prop('checked');
        $(this).closest('li').find('ul input[type="checkbox"]').prop('checked', isChecked);
    });

    // 폴더 클릭 시 선택됨
    $('button').on('click', function () {
        $('#jstree').jstree('select_node', 'root1_childNode1');
    });
    
    // 폴더 추가 버튼 클릭 시 이벤트
    $('#add-folder-button').on('click', function() {
        alert('폴더 추가 버튼');
    });
});