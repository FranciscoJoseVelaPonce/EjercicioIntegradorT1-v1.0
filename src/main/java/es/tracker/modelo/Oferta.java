package es.tracker.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Una candidatura a una oferta de empleo. Es una linea del archivo de datos.
 * Al crearla se comprueba que los datos obligatorios estan bien, asi nunca
 * hay una oferta a medias.
 *
 * @param id               numero que identifica la oferta
 * @param titulo           puesto al que nos presentamos
 * @param empresa          nombre de la empresa
 * @param estado           como va el proceso
 * @param fechaPostulacion dia en el que mandamos la candidatura
 * @param enlace           url de la oferta, puede estar vacio
 * @param notas            notas personales, pueden estar vacias
 */
public record Oferta(
        int id,
        String titulo,
        String empresa,
        Estado estado,
        LocalDate fechaPostulacion,
        String enlace,
        String notas) {

    /** Formato de las fechas en el archivo y en los comandos (dd/MM/yyyy). */
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Comprueba los datos antes de crear la oferta.
     *
     * @throws IllegalArgumentException si falta el titulo, la empresa, el estado o la fecha
     */
    public Oferta {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (empresa == null || empresa.isBlank()) {
            throw new IllegalArgumentException("La empresa no puede estar vacía.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio. Estados permitidos: " + Estado.textosPermitidos());
        }
        if (fechaPostulacion == null) {
            throw new IllegalArgumentException("La fecha de postulación es obligatoria.");
        }
        enlace = enlace == null ? "" : enlace;
        notas = notas == null ? "" : notas;
    }

    /**
     * Hace una copia de la oferta con otro id. Se usa al importar.
     *
     * @param nuevoId el id que va a tener la copia
     * @return la oferta igual pero con el id nuevo
     */
    public Oferta conId(int nuevoId) {
        return new Oferta(nuevoId, titulo, empresa, estado, fechaPostulacion, enlace, notas);
    }
}
