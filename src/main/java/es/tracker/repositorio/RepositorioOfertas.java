package es.tracker.repositorio;

import es.tracker.modelo.Oferta;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Lo que se puede hacer con los datos de las ofertas, sin decir donde se guardan.
 * En la v1.0 se guardan en un archivo, pero mas adelante podria ser una base de datos.
 */
public interface RepositorioOfertas {

    /**
     * Lee todas las ofertas guardadas.
     *
     * @return la lista de ofertas, vacia si todavia no hay ninguna
     * @throws IOException si no se puede leer el archivo
     */
    List<Oferta> leerTodas() throws IOException;

    /**
     * Guarda la lista entera de ofertas, sustituyendo lo que habia.
     *
     * @param ofertas las ofertas que se tienen que guardar
     * @throws IOException si no se puede escribir el archivo
     */
    void guardarTodas(List<Oferta> ofertas) throws IOException;

    /**
     * Lee las ofertas de otro archivo CSV con el mismo formato. Las lineas
     * que estan mal no se paran la lectura, se apuntan aparte.
     *
     * @param ruta el archivo que se quiere leer
     * @return las ofertas buenas y los numeros de linea que estaban mal
     * @throws IOException si el archivo no existe o no se puede leer
     */
    Lectura leerArchivo(Path ruta) throws IOException;

    /**
     * Resultado de leer un archivo: las ofertas que se han podido leer y las lineas que fallaron.
     *
     * @param ofertas         ofertas leidas bien
     * @param lineasNoValidas numeros de linea que no se han podido leer
     */
    record Lectura(List<Oferta> ofertas, List<Integer> lineasNoValidas) {
    }
}
