document.querySelectorAll('#nav_wrap > ul > li > a').forEach(anchor => {
    anchor.addEventListener('click', function(event) {
        event.preventDefault();

        const dropdown = this.nextElementSibling;
        const parentLi = this.parentElement;
        const isParentActive = parentLi.classList.contains('active');

        // 활성화 상태 초기화
        closeDropdowns();

        if (!isParentActive) {
            // 현재 클릭된 메뉴 항목에 활성화 추가
            parentLi.classList.add('active');

            // 현재 클릭된 항목의 드롭다운 표시
            if (dropdown) {
                dropdown.style.display = 'block';
            }
        } else {
            // 클릭한 메뉴가 이미 활성화 되어있다면 드롭다운 닫기
            parentLi.classList.remove('active');
            if (dropdown) {
                dropdown.style.display = 'none';
            }
        }

        // 자식 메뉴 항목 클릭 시 dropdown 표시/숨김
        const childAnchors = dropdown?.querySelectorAll('li > a') || [];
        childAnchors.forEach(childAnchor => {
            // 이벤트 핸들러 중복 추가 방지
            childAnchor.removeEventListener('click', handleChildClick);
            childAnchor.addEventListener('click', handleChildClick);
        });

        function handleChildClick(event) {
            event.stopPropagation();
            const childDropdown = this.nextElementSibling;
            const childLi = this.parentElement;
            const isChildDropdownOpen = childDropdown && childDropdown.style.display === 'block';

            // 자식 메뉴의 활성화 상태 처리
            if (childDropdown) {
                childDropdown.style.display = isChildDropdownOpen ? 'none' : 'block';
            }

            // 자식 메뉴의 li 요소에 active 클래스 추가
            if (!isChildDropdownOpen) {
                childLi.classList.add('active');
            } else {
                childLi.classList.remove('active');
            }
        }
    });
});

document.addEventListener('click', function(event) {
    if (!event.target.closest('#nav_wrap')) {
        closeDropdowns();
    }
});

function closeDropdowns() {
    document.querySelectorAll('.dropdown').forEach(dropdown => {
        dropdown.style.display = 'none';
    });
    document.querySelectorAll('#nav_wrap > ul > li').forEach(li => {
        li.classList.remove('active');
    });
}
