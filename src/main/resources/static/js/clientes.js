// Razón social y RFC solo se habilitan para clientes corporativos (RF-15).
(function () {
  var tipo = document.getElementById('tipoCliente');
  var campos = [document.getElementById('razonSocial'), document.getElementById('rfc')];
  function actualizar() {
    var corporativo = tipo.value === 'CORPORATIVO';
    campos.forEach(function (c) { c.disabled = !corporativo; });
  }
  tipo.addEventListener('change', actualizar);
  actualizar();
})();
