// Adds a hamburger toggle + slide-in behavior for the dashboard sidebar on small screens.
document.addEventListener('DOMContentLoaded', function () {
    var sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;

    var toggle = document.createElement('button');
    toggle.type = 'button';
    toggle.className = 'sidebar-toggle';
    toggle.setAttribute('aria-label', 'Toggle navigation menu');
    toggle.innerHTML = '<i class="fa-solid fa-bars"></i>';

    var overlay = document.createElement('div');
    overlay.className = 'sidebar-overlay';

    document.body.appendChild(toggle);
    document.body.appendChild(overlay);

    function closeSidebar() {
        sidebar.classList.remove('open');
        overlay.classList.remove('active');
    }

    function openSidebar() {
        sidebar.classList.add('open');
        overlay.classList.add('active');
    }

    toggle.addEventListener('click', function () {
        if (sidebar.classList.contains('open')) {
            closeSidebar();
        } else {
            openSidebar();
        }
    });

    overlay.addEventListener('click', closeSidebar);

    sidebar.querySelectorAll('a').forEach(function (link) {
        link.addEventListener('click', closeSidebar);
    });
});
