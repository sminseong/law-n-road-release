document.addEventListener('DOMContentLoaded', () => {
  const rightNav = document.querySelector('.right-nav');
  const cartOffcanvas = document.getElementById('offcanvasRight');

  if (rightNav && cartOffcanvas) {
    cartOffcanvas.addEventListener('show.bs.offcanvas', () => {
      rightNav.classList.add('d-none');  // Bootstrap에서 display: none
    });

    cartOffcanvas.addEventListener('hidden.bs.offcanvas', () => {
      rightNav.classList.remove('d-none');
    });
  }
});
