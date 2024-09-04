document.addEventListener("DOMContentLoaded", function () {
    var createModal = document.getElementById("myModal");
    var editModal = document.getElementById("editModal");
    var openModalBtn = document.getElementById("openModal");
    var closeButtons = document.getElementsByClassName("close");
    var createForm = document.getElementById("departmentForm");
    var editForm = document.getElementById("editForm");
    var csrfToken = document.querySelector('input[name="_csrf"]').value; 

    if (openModalBtn) {
        openModalBtn.onclick = function () {
            createModal.style.display = "block";
        };
    }

	// 수정
    document.querySelectorAll("#editButton").forEach(function (editButton) {
        editButton.onclick = function () {
            var departmentId = this.getAttribute("data-department-id");

            $.ajax({
                type: "GET",
                url: "/department/get?id=" + departmentId,
                success: function (response) {
                    if (response.success) {
                        var department = response.department;
                        document.getElementById("editDepartmentId").value = department.department_no;
                        document.getElementById("editDepartmentName").value = department.department_name;
                        document.getElementById("editDepartmentHigh").value = department.department_high;

                        if (department.subDepartments && department.subDepartments.length > 0) {
                            document.getElementById("editDepartmentHigh").disabled = true;
                        } else {
                            document.getElementById("editDepartmentHigh").disabled = false;
                        }

                        editModal.style.display = "block";
                    } else {
                        Swal.fire("오류", "부서 정보를 불러오는 데 실패했습니다.", "error");
                    }
                },
                error: function () {
                    Swal.fire("오류", "부서 정보를 불러오는 데 실패했습니다.", "error");
                }
            });
        };
    });

	// 삭제
    document.querySelectorAll("#deleteButton").forEach(function (deleteButton) {
        deleteButton.onclick = function () {
            var departmentId = this.getAttribute("data-department-id");

            Swal.fire({
                title: '부서 삭제',
                text: '부서를 삭제하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '삭제',
                cancelButtonText: '취소'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        type: "POST",
                        url: "/department/delete",
                        data: { id: departmentId },
                        headers: {
                            'X-CSRF-TOKEN': csrfToken
                        },
                        success: function (response) {
                            if (response.success) {
                                Swal.fire('삭제 완료', '부서가 성공적으로 삭제되었습니다.', 'success').then(() => {
                                    location.reload();
                                });
                            } else {
                                Swal.fire('삭제 실패', response.error, 'error');
                            }
                        },
                        error: function () {
                            Swal.fire('서버 오류', '서버와의 통신 중 오류가 발생했습니다.', 'error');
                        }
                    });
                }
            });
        };
    });

    Array.from(closeButtons).forEach(function (closeButton) {
        closeButton.onclick = closeModal;
    });

    window.onclick = function (event) {
        if (event.target == createModal || event.target == editModal) {
            closeModal();
        }
    };

    function closeModal() {
        createModal.style.display = "none";
        editModal.style.display = "none";
        resetForm(createForm);
        resetForm(editForm);
    }

    function resetForm(form) {
        form.reset();
        if (form.id === 'editForm') {
            document.getElementById("editDepartmentHigh").disabled = false; 
        }
    }

    // 등록
    if (createForm) {
        createForm.onsubmit = function (event) {
            event.preventDefault();
            var departmentName = document.getElementById("departmentName").value;
            var departmentHigh = document.getElementById("departmentHigh").value;
            var departmentHighInt = parseInt(departmentHigh, 10);

            $.ajax({
                type: "POST",
                url: "/department/add",
                contentType: "application/json",
                data: JSON.stringify({
                    departmentName: departmentName,
                    departmentHigh: departmentHighInt
                }),
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.success) {
                        Swal.fire('등록 성공', '부서가 성공적으로 등록되었습니다.', 'success').then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire('등록 실패', response.error, 'error');
                    }
                },
                error: function () {
                    Swal.fire('등록 실패', '서버와의 통신 중 오류가 발생했습니다.', 'error');
                }
            });
        };
    }

    // 수정
    if (editForm) {
        editForm.onsubmit = function (event) {
            event.preventDefault();

            var departmentId = document.getElementById("editDepartmentId").value;
            var departmentName = document.getElementById("editDepartmentName").value;
            var departmentHigh = document.getElementById("editDepartmentHigh").value;
            var departmentHighInt = parseInt(departmentHigh, 10);

            $.ajax({
                type: "POST",
                url: "/department/update",
                contentType: "application/json",
                data: JSON.stringify({
                    departmentId: departmentId,
                    departmentName: departmentName,
                    departmentHigh: departmentHighInt
                }),
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.success) {
                        Swal.fire("수정 성공", "부서가 성공적으로 수정되었습니다.", "success").then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire("수정 실패", response.error, "error");
                    }
                },
                error: function () {
                    Swal.fire("서버 오류", "서버와의 통신 중 오류가 발생했습니다.", "error");
                }
            });
        };
    }
});
