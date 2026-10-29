package es.tracker;

import es.tracker.cli.Cli;
import es.tracker.repositorio.RepositorioOfertasArchivo;
import es.tracker.servicio.OfertaService;

import java.nio.file.Path;
import java.time.Clock;

/**
 * Punto de entrada del programa. Solo junta las piezas y arranca.
 */
public class Main {

    /**
     * Ejecuta una operacion y termina. El codigo de salida es 0 si va bien y 1 si hay error.
     *
     * @param args la operacion y sus parametros, por ejemplo -listar --estado pendiente
     */
    public static void main(String[] args) {
        RepositorioOfertasArchivo repositorio = new RepositorioOfertasArchivo(Path.of("candidaturas.csv"));
        OfertaService servicio = new OfertaService(repositorio, Clock.systemDefaultZone());
        System.exit(new Cli(servicio, System.out, System.err).ejecutar(args));
    }
}
