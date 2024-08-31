
document.querySelectorAll('#nav_wrap > ul > li > a').forEach(anchor => {
    anchor.addEventListener('click', function(event) {
        event.preventDefault(); // 링크 클릭 시 페이지 이동 방지
        
        const dropdown = this.nextElementSibling;
        
        if (dropdown && dropdown.classList.contains('dropdown')) {
            if (dropdown.style.display === 'block') {
                dropdown.style.display = 'none';
            } else {
                document.querySelectorAll('.dropdown').forEach(drop => {
                    drop.style.display = 'none';
                });
                dropdown.style.display = 'block';
            }
        }
    });
});

document.addEventListener('click', function(event) {
    if (!event.target.closest('#nav_wrap')) {
        document.querySelectorAll('.dropdown').forEach(dropdown => {
            dropdown.style.display = 'none';
        });
    }
});
