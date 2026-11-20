package org.egibide.services;

import org.egibide.dao.RefugioDao;
import org.egibide.models.Refugio;
import org.egibide.utils.Validaciones;

public class RefugioService {

    private final RefugioDao refugioDao;

    public RefugioService() {
        refugioDao = new RefugioDao();
    }

    // Buscar los datos del refugio
    public Refugio buscar() {
        return refugioDao.buscar();
    }

    // Guardar los datos del refugio
    public void guardar(Refugio refugio) {

        validar(refugio);

        refugioDao.guardar(refugio);
    }

    // TODO : Y comprobaciones de longitud? Y en los demás servicios?
    // Validar los datos del refugio
    private void validar(Refugio refugio) {

        if (refugio == null) {
            throw new IllegalArgumentException("El refugio no puede ser nulo");
        }

        if (refugio.getNombre() == null || refugio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        refugio.setCif(Validaciones.validarCif(refugio.getCif()));

        refugio.setTelefono(
                Validaciones.validarTelefono(refugio.getTelefono())
        );

        refugio.setCorreo(
                Validaciones.validarEmail(refugio.getCorreo())
        );

        Validaciones.validarTexto(refugio.getRazonSocial(), "La razón social");
        Validaciones.validarTexto(refugio.getDireccion(), "La dirección");

        Validaciones.validarFecha(
                refugio.getFechaApertura(),
                "La fecha de apertura"
        );
    }
}
