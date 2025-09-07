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
    let checkboxes = null;
    if (tableId) {
        checkboxes = document.querySelectorAll(`#${tableId} input[type='checkbox']`);
    } else {
        checkboxes = document.querySelectorAll("table input[type='checkbox']");
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
    document.querySelectorAll('.alert.message').forEach(alert => {
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

function renderDynamicDropdown( {
containerId,
        apiUrl,
        params = {},
        buildOptionText = item => item.label,
        pageSize = 10,
        placeholder = '',
        value = null,
        allowNull = false,
        required = false
        }) {
    let page = 1;
    let isLoading = false;
    let allLoaded = false;

    const container = document.getElementById(containerId);
    container.style.flex = 1;

    // Hidden input để submit form
    const tempInput = container.querySelector('input');
    tempInput.type = 'hidden';
    if (required)
        tempInput.setAttribute("required", "required");

    // Button hiển thị giá trị
    const tempBtn = document.createElement('button');
    tempBtn.classList.add('form-select');
    tempBtn.type = 'button';
    tempBtn.style.height = '37.6px';
    tempBtn.style.textAlign = 'left';
    if (required) {
        tempBtn.classList.add('required');
    }

    // Nếu có giá trị ban đầu thì hiển thị
    if (value) {
        tempBtn.innerText = buildOptionText(value);
        tempInput.value = value.id;
    } else {
        tempBtn.innerText = placeholder || (allowNull ? "-- Không chọn --" : "Chọn...");
        tempInput.value = "";
    }

    // Search input
    const searchContainer = document.createElement('div');
    searchContainer.style.padding = '10px';
    searchContainer.style.position = 'sticky';
    searchContainer.style.top = '0';
    searchContainer.style.background = 'white';
    const searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.classList.add('form-control');

    // Dropdown container
    const dropdown = document.createElement('div');
    dropdown.classList.add('dropdown-menu');
    dropdown.style.maxHeight = '300px';
    dropdown.style.overflowX = 'hidden';
    dropdown.style.overflowY = 'auto';
    dropdown.style.paddingTop = '0';

    container.prepend(tempBtn);
    container.appendChild(dropdown);
    dropdown.appendChild(searchContainer);
    searchContainer.appendChild(searchInput);

    if (allowNull) {
        const nullOption = document.createElement('div');
        nullOption.classList.add('dropdown-item', 'text-muted');
        nullOption.textContent = "-- Không chọn --";
        nullOption.style.cursor = "pointer";
        nullOption.onclick = () => {
            tempInput.value = "";
            tempBtn.innerText = placeholder || "-- Không chọn --";
            dropdown.style.display = 'none';
        };
        dropdown.appendChild(nullOption);
    }

    function loadData(reset = false, forceReset = false) {
        if ((isLoading || allLoaded) && !forceReset)
            return;

        if (reset) {
            page = 1;
            allLoaded = false;
            dropdown.innerHTML = '';
            dropdown.appendChild(searchContainer);

            if (allowNull) { // thêm lại option null nếu reset
                const nullOption = document.createElement('div');
                nullOption.classList.add('dropdown-item', 'text-muted');
                nullOption.textContent = "-- Không chọn --";
                nullOption.style.cursor = "pointer";
                nullOption.onclick = () => {
                    tempInput.value = "";
                    tempBtn.innerText = placeholder || "-- Không chọn --";
                    dropdown.style.display = 'none';
                };
                dropdown.appendChild(nullOption);
            }
        }

        isLoading = true;

        const queryString = new URLSearchParams({
            page: page,
            size: pageSize,
            kw: searchInput.value,
            ...params
        }).toString();

        fetch(`${apiUrl}?${queryString}`)
                .then(res => res.json())
                .then(data => {
                    if (!data.results || data.results.length === 0) {
                        if (reset) {
                            dropdown.replaceChildren(searchContainer);
                            dropdown.insertAdjacentHTML('beforeend', "<div class='dropdown-item text-muted'>Không tìm thấy</div>");
                        }
                        allLoaded = true;
                        return;
                    }

                    data.results.forEach(item => {
                        const div = document.createElement('div');
                        div.classList.add('dropdown-item');
                        div.style.cursor = 'pointer';
                        div.textContent = buildOptionText(item);
                        div.dataset.id = item.id;
                        div.onclick = () => {
                            tempInput.value = item.id;
                            tempBtn.innerText = buildOptionText(item);
                            dropdown.style.display = 'none';
                        };
                        dropdown.appendChild(div);
                    });

                    if (!data.pagination || !data.pagination.more) {
                        allLoaded = true;
                    } else {
                        page++;
                    }
                })
                .finally(() => {
                    isLoading = false;
                    searchInput.focus();
                });
    }

    tempBtn.addEventListener('click', function () {
        dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
        if (dropdown.style.display === 'block') {
            loadData(true);
            dropdown.style.width = `${tempBtn.offsetWidth}px`;
            searchInput.style.width = `calc(${tempBtn.offsetWidth}px - 30px)`;
            searchInput.focus();
        }
    });

    document.addEventListener('click', function (e) {
        if (!container.contains(e.target)) {
            dropdown.style.display = 'none';
        }
    });

    searchInput.addEventListener('input', function () {
        loadData(true, true);
    });

    dropdown.addEventListener('scroll', function () {
        if (this.scrollTop + this.clientHeight >= this.scrollHeight - 5) {
            loadData();
        }
    });
}

