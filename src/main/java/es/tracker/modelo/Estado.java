package es.tracker.modelo;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Estados en los que puede estar una candidatura.
 */
public enum Estado {
    PENDIENTE("pendiente"),
    ENTREVISTA("entrevista"),
    RECHAZADO("rechazado"),
    ACEPTADO("aceptado");

    private final String texto;

    Estado(String texto) {
        this.texto = texto;
    }

    /**
     * Devuelve el estado tal y como se escribe en el archivo.
     *
     * @return el texto del estado, por ejemplo "pendiente"
     */
    public String texto() {
        return texto;
    }

    /**
     * Busca el estado a partir del texto que escribe el usuario.
     * Da igual si viene en mayusculas o minusculas.
     *
     * @param texto lo que ha escrito el usuario
     * @return el estado que corresponde
     * @throws IllegalArgumentException si el texto no es ningun estado
     */
    public static Estado desdeTexto(String texto) {
        return Arrays.stream(values())
                .filter(estado -> texto != null && estado.texto.equalsIgnoreCase(texto.strip()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estado no válido: \"" + texto + "\". Estados permitidos: " + textosPermitidos()));
    }

    /**
     * Lista de estados separada por comas, para los mensajes de error y la ayuda.
     *
     * @return por ejemplo "pendiente, entrevista, rechazado, aceptado"
     */
    public static String textosPermitidos() {
        return Arrays.stream(values()).map(Estado::texto).collect(Collectors.joining(", "));
    }
}
