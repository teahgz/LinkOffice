document.querySelectorAll('#nav_wrap > ul > li > a').forEach(anchor => {
    anchor.addEventListener('click', function(event) {

        const dropdown = this.nextElementSibling;
        const parentLi = this.parentElement;
        const isParentActive = parentLi.classList.contains('active');

        closeDropdowns();

        if (!isParentActive) {
            parentLi.classList.add('active');

            if (dropdown) {
                dropdown.style.display = 'block';
            }
        } else {
            parentLi.classList.remove('active');
            if (dropdown) {
                dropdown.style.display = 'none';
            }
        }

        const childAnchors = dropdown?.querySelectorAll('li > a') || [];
        childAnchors.forEach(childAnchor => {
            childAnchor.removeEventListener('click', handleChildClick);
            childAnchor.addEventListener('click', handleChildClick);
        });

        function handleChildClick(event) {
            event.stopPropagation();
            const childDropdown = this.nextElementSibling;
            const childLi = this.parentElement;
            const isChildDropdownOpen = childDropdown && childDropdown.style.display === 'block';

            if (childDropdown) {
                childDropdown.style.display = isChildDropdownOpen ? 'none' : 'block';
            }

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
