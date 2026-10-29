package es.tracker.repositorio;

import es.tracker.modelo.Estado;
import es.tracker.modelo.Oferta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda las ofertas en un archivo de texto, una por linea y con los campos separados por ;
 */
public class RepositorioOfertasArchivo implements RepositorioOfertas {

    /** Primera linea del archivo con el nombre de las columnas. */
    public static final String CABECERA = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";
    private static final int NUMERO_CAMPOS = 7;

    private final Path archivo;

    /**
     * Crea el repositorio para un archivo concreto.
     *
     * @param archivo el archivo donde se leen y guardan las ofertas
     */
    public RepositorioOfertasArchivo(Path archivo) {
        this.archivo = archivo;
    }

    /**
     * {@inheritDoc}
     * Si el archivo tiene alguna linea mal no se devuelve nada, para no
     * perder datos al guardar despues.
     */
    @Override
    public List<Oferta> leerTodas() throws IOException {
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }
        Lectura lectura = leerArchivo(archivo);
        if (!lectura.lineasNoValidas().isEmpty()) {
            throw new IllegalStateException("El archivo " + archivo + " tiene líneas no válidas: "
                    + lectura.lineasNoValidas() + ". Corrígelas antes de continuar.");
        }
        return lectura.ofertas();
    }

    @Override
    public void guardarTodas(List<Oferta> ofertas) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write(CABECERA);
            escritor.newLine();
            for (Oferta oferta : ofertas) {
                escritor.write(aLinea(oferta));
                escritor.newLine();
            }
        }
    }

    @Override
    public Lectura leerArchivo(Path ruta) throws IOException {
        if (Files.notExists(ruta)) {
            throw new NoSuchFileException(ruta.toString());
        }
        List<Oferta> ofertas = new ArrayList<>();
        List<Integer> lineasNoValidas = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea = lector.readLine();
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    ofertas.add(desdeLinea(linea));
                } catch (RuntimeException e) {
                    lineasNoValidas.add(numeroLinea);
                }
            }
        }
        return new Lectura(ofertas, lineasNoValidas);
    }

    private static Oferta desdeLinea(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length != NUMERO_CAMPOS) {
            throw new IllegalArgumentException("Número de campos incorrecto");
        }
        return new Oferta(
                Integer.parseInt(campos[0].strip()),
                campos[1].strip(),
                campos[2].strip(),
                Estado.desdeTexto(campos[3]),
                LocalDate.parse(campos[4].strip(), Oferta.FORMATO_FECHA),
                campos[5].strip(),
                campos[6].strip());
    }

    private static String aLinea(Oferta oferta) {
        return String.join(";",
                String.valueOf(oferta.id()),
                oferta.titulo(),
                oferta.empresa(),
                oferta.estado().texto(),
                oferta.fechaPostulacion().format(Oferta.FORMATO_FECHA),
                oferta.enlace(),
                oferta.notas());
    }
}
