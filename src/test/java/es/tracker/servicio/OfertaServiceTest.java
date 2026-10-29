package es.tracker.servicio;

import es.tracker.modelo.Estado;
import es.tracker.modelo.Oferta;
import es.tracker.repositorio.RepositorioOfertas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfertaServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 28);

    private RepositorioEnMemoria repositorio;
    private OfertaService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        Clock reloj = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("Europe/Madrid"));
        servicio = new OfertaService(repositorio, reloj);
    }

    private static Oferta oferta(int id, String empresa, Estado estado) {
        return new Oferta(id, "Puesto " + id, empresa, estado, HOY, "", "");
    }

    @Test
    void listarFiltraPorEstadoYEmpresaSinMirarMayusculas() throws Exception {
        repositorio.ofertas = List.of(
                oferta(1, "Indra", Estado.PENDIENTE),
                oferta(2, "Indra", Estado.ENTREVISTA),
                oferta(3, "Accenture", Estado.PENDIENTE));

        List<Oferta> resultado = servicio.listar("PENDIENTE", "indr");

        assertEquals(List.of(1), resultado.stream().map(Oferta::id).toList());
    }

    @Test
    void listarSinFiltrosDevuelveTodas() throws Exception {
        repositorio.ofertas = List.of(oferta(1, "Indra", Estado.ACEPTADO), oferta(2, "NTT Data", Estado.RECHAZADO));

        assertEquals(2, servicio.listar(null, null).size());
    }

    @Test
    void listarConEstadoQueNoExisteDaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.listar("contratado", null));
    }

    @Test
    void anadirPoneElSiguienteIdLaFechaDeHoyYQuitaLosPuntoYComa() throws Exception {
        repositorio.ofertas = List.of(oferta(7, "Indra", Estado.PENDIENTE));

        Oferta nueva = servicio.anadir("Dev Java", "Indra; Madrid", "pendiente", null, null, null);

        assertEquals(8, nueva.id());
        assertEquals("Indra, Madrid", nueva.empresa());
        assertEquals(HOY, nueva.fechaPostulacion());
        assertEquals("", nueva.enlace());
        assertEquals(2, repositorio.ofertas.size());
    }

    @Test
    void anadirConArchivoVacioEmpiezaEnUno() throws Exception {
        Oferta nueva = servicio.anadir("Prácticas DAM", "Telefónica", "entrevista", "01/10/2026",
                "https://empleo.ejemplo/1", "Me llamaron por teléfono");

        assertEquals(1, nueva.id());
        assertEquals(LocalDate.of(2026, 10, 1), nueva.fechaPostulacion());
        assertEquals(Estado.ENTREVISTA, nueva.estado());
    }

    @Test
    void anadirNoGuardaSiLosDatosEstanMal() {
        assertThrows(IllegalArgumentException.class, () -> servicio.anadir(" ", "Indra", "pendiente", null, null, null));
        assertThrows(IllegalArgumentException.class, () -> servicio.anadir("Dev", " ", "pendiente", null, null, null));
        assertThrows(IllegalArgumentException.class, () -> servicio.anadir("Dev", "Indra", "contratado", null, null, null));
        assertThrows(IllegalArgumentException.class, () -> servicio.anadir("Dev", "Indra", "pendiente", "31/02/2026", null, null));
        assertThrows(IllegalArgumentException.class, () -> servicio.anadir("Dev", "Indra", "pendiente", "2026-10-01", null, null));
        assertFalse(repositorio.guardado);
    }

    @Test
    void verDevuelveLaOfertaOFallaSiNoExiste() throws Exception {
        repositorio.ofertas = List.of(oferta(13, "Indra", Estado.PENDIENTE));

        assertEquals("Puesto 13", servicio.ver(13).titulo());
        assertThrows(NoSuchElementException.class, () -> servicio.ver(99));
    }

    @Test
    void borrarQuitaSoloEsaOferta() throws Exception {
        repositorio.ofertas = List.of(oferta(1, "A", Estado.PENDIENTE), oferta(2, "B", Estado.PENDIENTE));

        servicio.borrar(1);

        assertEquals(List.of(2), repositorio.ofertas.stream().map(Oferta::id).toList());
    }

    @Test
    void borrarUnIdQueNoExisteNoGuardaNada() {
        repositorio.ofertas = List.of(oferta(1, "A", Estado.PENDIENTE));

        assertThrows(NoSuchElementException.class, () -> servicio.borrar(5));
        assertFalse(repositorio.guardado);
    }

    @Test
    void importarAnadeAlFinalConIdsNuevosYDiceLasLineasMalas() throws Exception {
        repositorio.ofertas = List.of(oferta(10, "A", Estado.PENDIENTE));
        repositorio.lecturaDeOtroArchivo = new RepositorioOfertas.Lectura(
                List.of(oferta(1, "X", Estado.ACEPTADO), oferta(2, "Y", Estado.RECHAZADO)), List.of(4));

        OfertaService.ResultadoImportacion resultado = servicio.importar(Path.of("otras.csv"));

        assertEquals(2, resultado.importadas());
        assertEquals(List.of(4), resultado.lineasNoValidas());
        assertEquals(List.of(10, 11, 12), repositorio.ofertas.stream().map(Oferta::id).toList());
    }

    private static class RepositorioEnMemoria implements RepositorioOfertas {

        private List<Oferta> ofertas = new ArrayList<>();
        private Lectura lecturaDeOtroArchivo;
        private boolean guardado;

        @Override
        public List<Oferta> leerTodas() {
            return new ArrayList<>(ofertas);
        }

        @Override
        public void guardarTodas(List<Oferta> nuevas) {
            ofertas = new ArrayList<>(nuevas);
            guardado = true;
        }

        @Override
        public Lectura leerArchivo(Path ruta) {
            return lecturaDeOtroArchivo;
        }
    }
}
