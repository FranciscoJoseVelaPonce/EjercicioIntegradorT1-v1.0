package es.tracker.cli;

import es.tracker.modelo.Estado;
import es.tracker.modelo.Oferta;
import es.tracker.servicio.OfertaService;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Lee los argumentos que se le pasan al programa, llama al servicio y
 * escribe por consola el resultado o el error.
 */
public class Cli {

    private static final String AYUDA = """
            Uso:
              -listar [--estado ESTADO] [--empresa TEXTO]
              -añadir --titulo X --empresa X --estado ESTADO [--fecha dd/MM/yyyy] [--enlace URL] [--notas X]
              -ver ID
              -borrar ID
              -importar archivo.csv
            Estados: %s""".formatted(Estado.textosPermitidos());

    private final OfertaService servicio;
    private final PrintStream salida;
    private final PrintStream errores;

    /**
     * Crea el Cli.
     *
     * @param servicio el servicio con la logica
     * @param salida   donde se escriben los resultados (normalmente System.out)
     * @param errores  donde se escriben los errores (normalmente System.err)
     */
    public Cli(OfertaService servicio, PrintStream salida, PrintStream errores) {
        this.servicio = servicio;
        this.salida = salida;
        this.errores = errores;
    }

    /**
     * Ejecuta la operacion que viene en los argumentos. Todos los errores se
     * convierten en un mensaje, el programa no se para con una excepcion.
     *
     * @param args los argumentos del main, el primero es la operacion
     * @return 0 si ha ido bien y 1 si ha habido algun error
     */
    public int ejecutar(String[] args) {
        if (args.length == 0) {
            errores.println("Falta la operación.");
            errores.println(AYUDA);
            return 1;
        }
        try {
            switch (args[0]) {
                case "-listar" -> listar(opciones(args, Set.of("estado", "empresa")));
                case "-añadir", "-anadir" -> anadir(opciones(args, Set.of("titulo", "empresa", "estado", "fecha", "enlace", "notas")));
                case "-ver" -> mostrarDetalle(servicio.ver(id(args)));
                case "-borrar" -> salida.println("Candidatura borrada: " + servicio.borrar(id(args)).titulo());
                case "-importar" -> importar(args);
                default -> throw new IllegalArgumentException("Operación desconocida: " + args[0] + "\n" + AYUDA);
            }
            return 0;
        } catch (IllegalArgumentException | NoSuchElementException | IllegalStateException e) {
            errores.println("Error: " + e.getMessage());
        } catch (NoSuchFileException e) {
            errores.println("Error: no se encuentra el archivo " + e.getFile());
        } catch (IOException e) {
            errores.println("Error al leer o escribir el archivo: " + e.getMessage());
        }
        return 1;
    }

    private void listar(Map<String, String> opciones) throws IOException {
        List<Oferta> ofertas = servicio.listar(opciones.get("estado"), opciones.get("empresa"));
        if (ofertas.isEmpty()) {
            salida.println("No hay candidaturas que mostrar.");
            return;
        }
        salida.printf("%-4s %-30s %-20s %-11s %s%n", "ID", "TÍTULO", "EMPRESA", "ESTADO", "FECHA");
        for (Oferta oferta : ofertas) {
            salida.printf("%-4d %-30s %-20s %-11s %s%n", oferta.id(), oferta.titulo(), oferta.empresa(),
                    oferta.estado().texto(), oferta.fechaPostulacion().format(Oferta.FORMATO_FECHA));
        }
        salida.println(ofertas.size() + " candidatura(s).");
    }

    private void anadir(Map<String, String> opciones) throws IOException {
        Oferta oferta = servicio.anadir(
                obligatoria(opciones, "titulo"),
                obligatoria(opciones, "empresa"),
                obligatoria(opciones, "estado"),
                opciones.get("fecha"),
                opciones.get("enlace"),
                opciones.get("notas"));
        salida.println("Candidatura añadida con ID " + oferta.id() + ": " + oferta.titulo() + " en " + oferta.empresa());
    }

    private void importar(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Indica el archivo a importar, por ejemplo: -importar candidaturas.csv");
        }
        OfertaService.ResultadoImportacion resultado = servicio.importar(Path.of(args[1]));
        salida.println("Candidaturas importadas: " + resultado.importadas());
        if (!resultado.lineasNoValidas().isEmpty()) {
            salida.println("Líneas no válidas que se han saltado: " + resultado.lineasNoValidas());
        }
    }

    private void mostrarDetalle(Oferta oferta) {
        salida.println("ID:      " + oferta.id());
        salida.println("Título:  " + oferta.titulo());
        salida.println("Empresa: " + oferta.empresa());
        salida.println("Estado:  " + oferta.estado().texto());
        salida.println("Fecha:   " + oferta.fechaPostulacion().format(Oferta.FORMATO_FECHA));
        salida.println("Enlace:  " + (oferta.enlace().isEmpty() ? "-" : oferta.enlace()));
        salida.println("Notas:   " + (oferta.notas().isEmpty() ? "-" : oferta.notas()));
    }

    private static int id(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Indica un ID, por ejemplo: " + args[0] + " 13");
        }
        try {
            return Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número entero: " + args[1]);
        }
    }

    private static Map<String, String> opciones(String[] args, Set<String> permitidas) {
        Map<String, String> opciones = new HashMap<>();
        for (int i = 1; i < args.length; i += 2) {
            String nombre = args[i].startsWith("--") ? args[i].substring(2) : null;
            if (nombre == null || !permitidas.contains(nombre)) {
                throw new IllegalArgumentException("Parámetro no válido: " + args[i] + "\n" + AYUDA);
            }
            if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                throw new IllegalArgumentException("Falta el valor de " + args[i]);
            }
            opciones.put(nombre, args[i + 1]);
        }
        return opciones;
    }

    private static String obligatoria(Map<String, String> opciones, String nombre) {
        String valor = opciones.get(nombre);
        if (valor == null) {
            throw new IllegalArgumentException("Falta el parámetro --" + nombre);
        }
        return valor;
    }
}
