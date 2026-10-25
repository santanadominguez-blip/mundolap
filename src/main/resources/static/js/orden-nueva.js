// Muestra el costo estimado del equipo elegido. El servidor lo vuelve a calcular al generar la orden.
(function () {
  var equipo = document.getElementById('idEquipo');
  var costo = document.getElementById('costo');
  function actualizar() {
    var opcion = equipo.options[equipo.selectedIndex];
    if (opcion) costo.value = opcion.getAttribute('data-costo') || '';
  }
  equipo.addEventListener('change', actualizar);
  actualizar();
})();
