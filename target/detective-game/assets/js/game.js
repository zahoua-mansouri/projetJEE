document.querySelectorAll('.feedback-panel').forEach(panel => {
    panel.addEventListener('click', function(e) {
        if (e.target === this) this.style.display = 'none';
    });
});