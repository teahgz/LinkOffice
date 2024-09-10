document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelectorAll('.table_container input, .table_container textarea, .table_container select').forEach(element => {
        element.disabled = true;
    });
});
