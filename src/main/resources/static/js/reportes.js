// Pestañas de P-16: solo alternan los paneles ya presentes en la página (sin consultar datos).
(function () {
  var pestanas = document.querySelectorAll('.pestana');
  var paneles = document.querySelectorAll('[data-panel]');
  function mostrar(nombre) {
    pestanas.forEach(function (p) {
      var activa = p.getAttribute('data-pestana') === nombre;
      p.classList.toggle('activa', activa);
      p.setAttribute('aria-selected', activa ? 'true' : 'false');
    });
    paneles.forEach(function (x) { x.hidden = x.getAttribute('data-panel') !== nombre; });
  }
  pestanas.forEach(function (p) {
    p.addEventListener('click', function () { mostrar(p.getAttribute('data-pestana')); });
  });
})();
