document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.getElementById("toggleBtn");
    const sidebar = document.getElementById("sidebar");
    const dropdownLinks = document.querySelectorAll("#sidebar .dropdown-toggle");

    toggleBtn.addEventListener("click", function () {
        sidebar.classList.toggle("collapsed");

        if (sidebar.classList.contains("collapsed")) {
            dropdownLinks.forEach(link => {
                const dropdownItems = document.querySelectorAll("#sidebar .show");
                dropdownItems.forEach(item => item.classList.remove("show"));

                link.removeAttribute("data-bs-toggle");
                link.classList.remove("dropdown-toggle");
            });
        } else {
            dropdownLinks.forEach(link => {
                link.setAttribute("data-bs-toggle", "collapse");
                link.classList.add("dropdown-toggle");
            });
        }
    });

    const checkboxes = document.querySelectorAll("table input[type='checkbox']");
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", toggleDeleteBtnDisable);
    });
});

function toggleSelectAll(tableId = null) {
    const selectAllCheckbox = document.getElementById("selectAll");
    if (tableId) {
        const checkboxes = document.querySelectorAll(`#${tableId} input[type='checkbox']`);
    } else {
        const checkboxes = document.querySelectorAll("table input[type='checkbox']");
    }

    checkboxes.forEach((checkbox) => {
        checkbox.checked = selectAllCheckbox.checked;
    });

    toggleDeleteBtnDisable();
}

function toggleDeleteBtnDisable() {
    const checkboxes = document.querySelectorAll("table input[type='checkbox']");
    const checkedBoxes = document.querySelectorAll("table input[type='checkbox']:checked");
    const selectAllCheckbox = document.getElementById("selectAll");
    const deleteButton = document.getElementById("deleteButton");

    selectAllCheckbox.checked = checkboxes.length === checkedBoxes.length;

    deleteButton.disabled = checkedBoxes.length === 0;
}

function previewImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function (e) {
        const imgElement = document.getElementById("avatarPreview");
        imgElement.src = e.target.result;
    };

    if (file) {
        reader.readAsDataURL(file);
    }
}

function removeAvatar() {
    document.getElementById("avatarPreview").src = "/HotelManagementApp/img/default_avatar.svg";
    document.getElementById("avatarInput").value = "";
}

//Alert message
const time = 5000;

setTimeout(function () {
    document.querySelectorAll('.alert').forEach(alert => {
        if (bootstrap && bootstrap.Alert) {
            let bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        } else {
            alert.remove();
        }
    });
}, time);

function showMessage(type, message) {
    const messageContainer = document.querySelector("#messageContainer");
    const alertDiv = document.createElement("div");
    alertDiv.classList.add("alert", `alert-${type}`, "alert-dismissible", "fade", "show");
    alertDiv.setAttribute("role", "alert");
    alertDiv.innerHTML = `
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : 'info-circle'}"></i>
                <span>${message}</span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
    messageContainer.appendChild(alertDiv);

    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(alert => {
            if (bootstrap && bootstrap.Alert) {
                let bsAlert = bootstrap.Alert.getOrCreateInstance(alertDiv);
                bsAlert.close();
            } else {
                alertDiv.remove();
            }
        });
    }, time);
}

function confirmAndRequest(url, redirectUrl = null, method = 'DELETE') {
    if (confirm("Bạn có chắc chắn muốn thực hiện thao tác này?")) {
        fetch(url, {method: method})
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => {
                            throw new Error(err.message || "Có lỗi xảy ra!");
                        }).catch(() => {
                            throw new Error("Có lỗi xảy ra!");
                        });
                    }
                    return response.json().catch(() => ({}));
                })
                .then(data => {
                    showMessage('success', data.message || "Thao tác thành công!");
                    if (redirectUrl) {
                        setTimeout(() => window.location.href = redirectUrl, 1000);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showMessage('danger', error.message || "Đã xảy ra lỗi khi gửi yêu cầu!");
                });
}
}

function deleteAll(url, entityName = "mục", redirectUrl = null) {
    const ids = Array.from(document.querySelectorAll("tbody input[type='checkbox']:checked"))
            .map(cb => Number(cb.value));

    if (ids.length === 0) {
        showMessage('warning', `Vui lòng chọn ít nhất một ${entityName} để xóa.`);
        return;
    }

    if (confirm(`Bạn có chắc chắn muốn xóa các ${entityName} đã chọn?`)) {
        fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ids: ids})
        })
                .then(response => response.json())
                .then(data => {
                    if (data && data.message) {
                        showMessage('success', data.message);
                        setTimeout(() => {
                            if (redirectUrl) {
                                window.location.href = redirectUrl;
                            } else {
                                window.location.reload();
                            }
                        }, 1000);
                    } else {
                        showMessage('danger', `Đã xảy ra lỗi khi xóa ${entityName}!`);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showMessage('danger', `Đã xảy ra lỗi khi gửi yêu cầu!`);
                });
}
}
