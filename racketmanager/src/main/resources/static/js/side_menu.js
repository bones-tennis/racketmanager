function openMenu(){
  document.getElementById('sideMenu')?.classList.add('open');
  document.getElementById('overlay')?.classList.add('show');
}
function closeMenu(){
  document.getElementById('sideMenu')?.classList.remove('open');
  document.getElementById('overlay')?.classList.remove('show');
}
