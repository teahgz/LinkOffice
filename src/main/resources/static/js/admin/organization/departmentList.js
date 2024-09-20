document.addEventListener("DOMContentLoaded", function () {
    var createModal = document.getElementById("createModal");
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
                url: `/department/get?id=${departmentId}`,
                success: function (response) {
                    if (response.res_code === "200") {
                        const department = response.department;
                        document.getElementById("editDepartmentId").value = department.department_no;
                        document.getElementById("editDepartmentName").value = department.department_name;
                        document.getElementById("editDepartmentHigh").value = department.department_high;

                        document.getElementById("editDepartmentHigh").disabled = department.subDepartments && department.subDepartments.length > 0;

                        editModal.style.display = "block";
                    } else {
                        Swal.fire("오류", response.res_msg, "error");
                    }
                },
                error: function () {
                    Swal.fire("오류", response.res_msg, "error");
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
                confirmButtonColor: '#EEB3B3',
                cancelButtonColor: '#C0C0C0',
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
                            if (response.res_code === "200") {
                                Swal.fire({ 
								    text: response.res_msg,
								    icon: 'success', 
								    confirmButtonColor: '#B1C2DD', 
								    confirmButtonText: '확인', 
								}).then(() => {
                                    location.href = "/department"; 
                                });
                            } else {
                                Swal.fire({ 
								    text: response.res_msg,
								    icon: 'error', 
								    confirmButtonColor: '#B1C2DD', 
								    confirmButtonText: '확인', 
								});
                            }
                        },
                        error: function () {
                            Swal.fire('서버 오류', response.res_msg, 'error');
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

            $.ajax({
                type: "POST",
                url: "/department/add",
                contentType: "application/json",
                data: JSON.stringify({
                    departmentName: departmentName,
                    departmentHigh: departmentHigh
                }),
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.res_code === "200") {
                        Swal.fire({ 
						    text: response.res_msg,
						    icon: 'success', 
						    confirmButtonColor: '#B1C2DD', 
						    confirmButtonText: '확인', 
						}).then(() => {
                            location.reload();
                        });
                    } else {
						Swal.fire({ 
						    text: response.res_msg,
						    icon: 'error', 
						    confirmButtonColor: '#B1C2DD', 
						    confirmButtonText: '확인', 
						}); 
                    }
                },
                error: function () {
                    Swal.fire('서버 오류', response.res_msg, 'error');
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
                    if (response.res_code === "200") {
                        Swal.fire({ 
						    text: response.res_msg,
						    icon: 'success', 
						    confirmButtonColor: '#B1C2DD', 
						    confirmButtonText: '확인', 
						}).then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire({ 
						    text: response.res_msg,
						    icon: 'error', 
						    confirmButtonColor: '#B1C2DD', 
						    confirmButtonText: '확인', 
						});
                    }
                },
                error: function () {
                    Swal.fire("서버 오류", response.res_msg, "error");
                }
            });
        };
    }
    
    
});
 
