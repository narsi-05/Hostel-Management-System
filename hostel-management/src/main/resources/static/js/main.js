// =============================================
//  HOSTEL MANAGEMENT SYSTEM — main.js
// =============================================

document.addEventListener('DOMContentLoaded', function () {

    // ── Auto-hide alerts after 5 seconds ──
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // ── Star rating interactive selector ──
    const ratingInputs = document.querySelectorAll('input[name="rating"]');
    const ratingLabels = document.querySelectorAll('input[name="rating"] + span, .star-label');

    ratingInputs.forEach((input, index) => {
        input.addEventListener('change', function () {
            // Highlight all stars up to selected
            ratingInputs.forEach((inp, i) => {
                const label = inp.closest('label');
                if (label) {
                    label.style.transform = i <= index ? 'scale(1.2)' : 'scale(1)';
                }
            });
        });
    });

    // ── Confirm delete buttons ──
    const deleteForms = document.querySelectorAll('form[data-confirm]');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const msg = this.getAttribute('data-confirm') || 'Are you sure?';
            if (!confirm(msg)) e.preventDefault();
        });
    });

    // ── Navbar: mark active link based on URL ──
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // ── Sidebar: mark active link ──
    document.querySelectorAll('.sidebar-link').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // ── Photo gallery lightbox (simple) ──
    const galleryImages = document.querySelectorAll('.gallery-img');
    galleryImages.forEach(img => {
        img.addEventListener('click', function () {
            const overlay = document.createElement('div');
            overlay.style.cssText = 'position:fixed;inset:0;background:rgba(0,0,0,0.85);z-index:9999;display:flex;align-items:center;justify-content:center;cursor:pointer';
            const bigImg = document.createElement('img');
            bigImg.src = this.src;
            bigImg.style.cssText = 'max-width:90vw;max-height:90vh;border-radius:12px;box-shadow:0 0 40px rgba(0,0,0,0.5)';
            overlay.appendChild(bigImg);
            overlay.addEventListener('click', () => overlay.remove());
            document.body.appendChild(overlay);
        });
    });

    // ── Search form: clear empty params before submit ──
    const searchForms = document.querySelectorAll('form[action*="search"]');
    searchForms.forEach(form => {
        form.addEventListener('submit', function () {
            const inputs = this.querySelectorAll('input, select');
            inputs.forEach(input => {
                if (!input.value) input.disabled = true;
            });
        });
    });

    console.log('HostelFind — Frontend loaded ✓');
});
