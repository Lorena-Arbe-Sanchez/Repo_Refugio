package org.egibide.utils;

import java.util.Date;
import java.util.Locale;

public class Validaciones {

    // Validar y normalizar el DNI
    public static String validarDni(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        dni = dni.trim().toUpperCase(Locale.ROOT);

        if (!dni.matches("[0-9]{8}[A-Z]")) {
            throw new IllegalArgumentException("El DNI debe tener 8 números y una letra");
        }

        // Comprobar la letra correspondiente al número
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraCorrecta = letras.charAt(numero % 23);

        if (dni.charAt(8) != letraCorrecta) {
            throw new IllegalArgumentException("La letra del DNI no es correcta");
        }

        return dni;
    }

    // Validar y normalizar el teléfono español
    public static String validarTelefono(String telefono) {

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }

        telefono = telefono.trim().replaceAll("[\\s-]", "");

        if (!telefono.matches("(\\+34)?[6789][0-9]{8}")) {
            throw new IllegalArgumentException("El teléfono no tiene un formato válido");
        }

        return telefono;
    }

    // Validar y normalizar el correo electrónico
    public static String validarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (email.length() > 254 ||
                !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                    "El correo electrónico no tiene un formato válido"
            );
        }

        return email;
    }

    // Validar y normalizar el CIF
    public static String validarCif(String cif) {

        if (cif == null || cif.isBlank()) {
            throw new IllegalArgumentException("El CIF es obligatorio");
        }

        cif = cif.trim().toUpperCase(Locale.ROOT);

        if (!cif.matches("[ABCDEFGHJNPQRSUVW][0-9]{7}[0-9A-J]")) {
            throw new IllegalArgumentException("El CIF no tiene un formato válido");
        }

        int suma = 0;

        for (int i = 0; i < 7; i++) {

            int numero = Character.getNumericValue(cif.charAt(i + 1));

            if (i % 2 == 0) {
                int doble = numero * 2;
                suma += doble / 10 + doble % 10;
            } else {
                suma += numero;
            }
        }

        int control = (10 - suma % 10) % 10;
        char controlNumerico = (char) ('0' + control);
        char controlLetra = "JABCDEFGHI".charAt(control);

        char tipo = cif.charAt(0);
        char ultimo = cif.charAt(8);

        boolean debeSerNumero = "ABEH".indexOf(tipo) >= 0;
        boolean debeSerLetra = "KPQS".indexOf(tipo) >= 0;

        boolean correcto;

        if (debeSerNumero) {
            correcto = ultimo == controlNumerico;
        } else if (debeSerLetra) {
            correcto = ultimo == controlLetra;
        } else {
            correcto = ultimo == controlNumerico || ultimo == controlLetra;
        }

        if (!correcto) {
            throw new IllegalArgumentException("El carácter de control del CIF no es correcto");
        }

        return cif;
    }

    // Validar una fecha obligatoria y que no sea futura
    public static void validarFecha(Date fecha, String nombre) {

        if (fecha == null) {
            throw new IllegalArgumentException(nombre + " es obligatoria");
        }

        if (fecha.after(new Date())) {
            throw new IllegalArgumentException(nombre + " no puede ser futura");
        }
    }

    // Validar una cadena obligatoria
    public static void validarTexto(String texto, String nombre) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(nombre + " es obligatorio");
        }
    }

    // Validar una cadena con longitud máxima
    public static void validarLongitud(String texto, String nombre, int maximo) {

        if (texto != null && texto.length() > maximo) {
            throw new IllegalArgumentException(
                    nombre + " no puede superar los " + maximo + " caracteres"
            );
        }
    }
}