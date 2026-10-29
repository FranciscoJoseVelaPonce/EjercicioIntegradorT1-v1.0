package es.tracker.servicio;

import es.tracker.modelo.Estado;
import es.tracker.modelo.Oferta;
import es.tracker.repositorio.RepositorioOfertas;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Aqui esta la logica del programa: filtrar, añadir, ver, borrar e importar ofertas.
 * No sabe nada de la consola ni del archivo, eso lo hacen el Cli y el repositorio.
 */
public class OfertaService {

    private final RepositorioOfertas repositorio;
    private final Clock reloj;

    /**
     * Crea el servicio.
     *
     * @param repositorio de donde se leen y donde se guardan las ofertas
     * @param reloj       para saber la fecha de hoy (en los tests se pone una fija)
     */
    public OfertaService(RepositorioOfertas repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    /**
     * Devuelve las ofertas que cumplen los filtros. Si un filtro es null no se usa.
     * La empresa se busca por trozo de texto y sin mirar mayusculas.
     *
     * @param estado  estado que tienen que tener, o null
     * @param empresa texto que tiene que aparecer en la empresa, o null
     * @return las ofertas que cumplen los dos filtros
     * @throws IOException si falla la lectura
     */
    public List<Oferta> listar(String estado, String empresa) throws IOException {
        Estado estadoBuscado = estado == null ? null : Estado.desdeTexto(estado);
        String empresaBuscada = empresa == null ? null : empresa.toLowerCase(Locale.ROOT);
        return repositorio.leerTodas().stream()
                .filter(oferta -> estadoBuscado == null || oferta.estado() == estadoBuscado)
                .filter(oferta -> empresaBuscada == null
                        || oferta.empresa().toLowerCase(Locale.ROOT).contains(empresaBuscada))
                .toList();
    }

    /**
     * Añade una oferta nueva al final con el siguiente id libre.
     * Si no se pasa fecha se pone la de hoy. Los ; de los textos se cambian por comas
     * porque si no se romperia el archivo.
     *
     * @param titulo  puesto
     * @param empresa nombre de la empresa
     * @param estado  estado en texto
     * @param fecha   fecha en dd/MM/yyyy, o null para hoy
     * @param enlace  url de la oferta, puede ser null
     * @param notas   notas, pueden ser null
     * @return la oferta que se ha guardado
     * @throws IOException si falla la lectura o la escritura
     */
    public Oferta anadir(String titulo, String empresa, String estado, String fecha,
                         String enlace, String notas) throws IOException {
        List<Oferta> ofertas = repositorio.leerTodas();
        Oferta nueva = new Oferta(
                siguienteId(ofertas),
                limpiar(titulo),
                limpiar(empresa),
                Estado.desdeTexto(estado),
                fecha == null ? LocalDate.now(reloj) : convertirFecha(fecha),
                limpiar(enlace),
                limpiar(notas));
        guardarConNuevas(ofertas, List.of(nueva));
        return nueva;
    }

    /**
     * Busca una oferta por su id.
     *
     * @param id el id de la oferta
     * @return la oferta encontrada
     * @throws IOException            si falla la lectura
     * @throws NoSuchElementException si no hay ninguna con ese id
     */
    public Oferta ver(int id) throws IOException {
        return buscar(repositorio.leerTodas(), id);
    }

    /**
     * Borra la oferta con ese id y guarda el resto.
     *
     * @param id el id de la oferta a borrar
     * @return la oferta que se ha borrado
     * @throws IOException            si falla la lectura o la escritura
     * @throws NoSuchElementException si no hay ninguna con ese id
     */
    public Oferta borrar(int id) throws IOException {
        List<Oferta> ofertas = repositorio.leerTodas();
        Oferta borrada = buscar(ofertas, id);
        repositorio.guardarTodas(ofertas.stream().filter(oferta -> oferta.id() != id).toList());
        return borrada;
    }

    /**
     * Mete las ofertas de otro CSV al final, con ids nuevos para que no se repitan.
     * Las lineas que estan mal se saltan y se avisa de cuales son.
     *
     * @param ruta el CSV que se quiere importar
     * @return cuantas se han importado y que lineas estaban mal
     * @throws IOException si el archivo no existe o falla la escritura
     */
    public ResultadoImportacion importar(Path ruta) throws IOException {
        RepositorioOfertas.Lectura lectura = repositorio.leerArchivo(ruta);
        List<Oferta> ofertas = repositorio.leerTodas();
        int id = siguienteId(ofertas);
        List<Oferta> nuevas = new ArrayList<>();
        for (Oferta oferta : lectura.ofertas()) {
            nuevas.add(oferta.conId(id++));
        }
        guardarConNuevas(ofertas, nuevas);
        return new ResultadoImportacion(nuevas.size(), lectura.lineasNoValidas());
    }

    private void guardarConNuevas(List<Oferta> ofertas, List<Oferta> nuevas) throws IOException {
        List<Oferta> todas = new ArrayList<>(ofertas);
        todas.addAll(nuevas);
        repositorio.guardarTodas(todas);
    }

    private static Oferta buscar(List<Oferta> ofertas, int id) {
        return ofertas.stream()
                .filter(oferta -> oferta.id() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe ninguna candidatura con el ID " + id + "."));
    }

    private static int siguienteId(List<Oferta> ofertas) {
        return ofertas.stream().mapToInt(Oferta::id).max().orElse(0) + 1;
    }

    private static String limpiar(String texto) {
        return texto == null ? null : texto.strip().replace(';', ',');
    }

    private static LocalDate convertirFecha(String texto) {
        try {
            return LocalDate.parse(texto.strip(), Oferta.FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha no válida: \"" + texto + "\". Usa el formato dd/MM/yyyy, por ejemplo 15/09/2026.");
        }
    }

    /**
     * Lo que devuelve la importacion.
     *
     * @param importadas     numero de ofertas que se han añadido
     * @param lineasNoValidas lineas del CSV que no se han podido leer
     */
    public record ResultadoImportacion(int importadas, List<Integer> lineasNoValidas) {
    }
}
