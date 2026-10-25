package mx.mundolap.compartido.web;

/** Código que atiende una URL. */
@FunctionalInterface
public interface Manejador {
    void atender(Contexto c) throws Exception;
}
