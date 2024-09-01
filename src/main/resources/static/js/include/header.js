document.getElementById('userImage').addEventListener('click', function(event) {
   var dropdownMenu = document.getElementById('dropdownMenu');
   dropdownMenu.classList.toggle('show');
   event.stopPropagation();
});

document.addEventListener('click', function(event) {
   var dropdownMenu = document.getElementById('dropdownMenu');
    if (!event.target.closest('.user_image')) {
       dropdownMenu.classList.remove('show');
    }
});