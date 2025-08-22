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
});
