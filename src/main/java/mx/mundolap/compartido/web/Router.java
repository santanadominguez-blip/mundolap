package mx.mundolap.compartido.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Rol;

/** Tabla de rutas: asocia (método + URL) con un manejador y, opcionalmente, los roles permitidos. */
public final class Router {

    public record Ruta(String metodo, String ruta, Set<Rol> roles, Manejador manejador) { }

    private final Map<String, Ruta> rutas = new HashMap<>();
    private final List<ItemMenu> menu = new ArrayList<>();

    /** Ruta pública (sin inicio de sesión). */
    public void get(String ruta, Manejador m) { agregar("GET", ruta, null, m); }

    public void post(String ruta, Manejador m) { agregar("POST", ruta, null, m); }

    /** Ruta que exige sesión iniciada y uno de los roles indicados (RF-01, RF-05). */
    public void get(String ruta, Set<Rol> roles, Manejador m) { agregar("GET", ruta, roles, m); }

    public void post(String ruta, Set<Rol> roles, Manejador m) { agregar("POST", ruta, roles, m); }

    private void agregar(String metodo, String ruta, Set<Rol> roles, Manejador m) {
        rutas.put(metodo + " " + ruta, new Ruta(metodo, ruta, roles, m));
    }

    public Ruta buscar(String metodo, String ruta) { return rutas.get(metodo + " " + ruta); }

    public void agregarMenu(List<ItemMenu> items) { menu.addAll(items); }

    public List<ItemMenu> menuPara(Rol rol) {
        return menu.stream()
                .filter(i -> i.roles().contains(rol))
                .sorted(Comparator.comparingInt(ItemMenu::orden).thenComparing(ItemMenu::etiqueta))
                .toList();
    }
}
