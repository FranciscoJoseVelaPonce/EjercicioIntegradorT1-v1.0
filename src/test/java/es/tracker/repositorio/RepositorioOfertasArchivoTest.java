package es.tracker.repositorio;

import es.tracker.modelo.Estado;
import es.tracker.modelo.Oferta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioOfertasArchivoTest {

    @TempDir
    Path carpeta;

    @Test
    void guardarYLeerDevuelveLasMismasOfertas() throws Exception {
        RepositorioOfertasArchivo repositorio = new RepositorioOfertasArchivo(carpeta.resolve("candidaturas.csv"));
        List<Oferta> ofertas = List.of(
                new Oferta(1, "Desarrollador Java", "Indra", Estado.PENDIENTE, LocalDate.of(2026, 9, 15), "https://empleo.ejemplo/1", "Contacto por LinkedIn"),
                new Oferta(2, "Prácticas DAM", "Telefónica", Estado.ACEPTADO, LocalDate.of(2026, 10, 1), "", ""));

        repositorio.guardarTodas(ofertas);

        assertEquals(ofertas, repositorio.leerTodas());
        assertEquals(RepositorioOfertasArchivo.CABECERA, Files.readAllLines(carpeta.resolve("candidaturas.csv"), StandardCharsets.UTF_8).getFirst());
    }

    @Test
    void siNoExisteElArchivoDeDatosDevuelveListaVacia() throws Exception {
        assertTrue(new RepositorioOfertasArchivo(carpeta.resolve("no-existe.csv")).leerTodas().isEmpty());
    }

    @Test
    void leerArchivoSaltaLasLineasMalasYDiceCuales() throws Exception {
        Path csv = carpeta.resolve("importar.csv");
        Files.writeString(csv, String.join("\n",
                RepositorioOfertasArchivo.CABECERA,
                "1;Dev Java;Indra;pendiente;15/09/2026;;",
                "2;Sin empresa;;pendiente;15/09/2026;;",
                "3;Estado raro;Indra;contratado;15/09/2026;;",
                "",
                "4;Fecha mala;Indra;pendiente;31/02/2026;;"), StandardCharsets.UTF_8);

        RepositorioOfertas.Lectura lectura = new RepositorioOfertasArchivo(carpeta.resolve("candidaturas.csv")).leerArchivo(csv);

        assertEquals(1, lectura.ofertas().size());
        assertEquals(List.of(3, 4, 6), lectura.lineasNoValidas());
    }

    @Test
    void leerUnArchivoQueNoExisteDaError() {
        RepositorioOfertasArchivo repositorio = new RepositorioOfertasArchivo(carpeta.resolve("candidaturas.csv"));

        assertThrows(NoSuchFileException.class, () -> repositorio.leerArchivo(carpeta.resolve("falta.csv")));
    }

    @Test
    void siElArchivoDeDatosTieneLineasMalasNoSeLee() throws Exception {
        Path datos = carpeta.resolve("candidaturas.csv");
        Files.writeString(datos, RepositorioOfertasArchivo.CABECERA + "\nlinea rota\n", StandardCharsets.UTF_8);

        assertThrows(IllegalStateException.class, () -> new RepositorioOfertasArchivo(datos).leerTodas());
    }
}
