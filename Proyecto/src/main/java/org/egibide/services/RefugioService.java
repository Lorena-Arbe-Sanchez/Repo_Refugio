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
            throw new IllegalArgumentException("⚠️ El refugio no puede ser nulo.");
        }

        Validaciones.validarTexto(refugio.getNombre(), "El nombre");
        Validaciones.validarLongitud(refugio.getNombre(), "El nombre", 100);

        Validaciones.validarTexto(refugio.getRazonSocial(), "La razón social");
        Validaciones.validarLongitud(refugio.getRazonSocial(), "La razón social", 150);

        Validaciones.validarTexto(refugio.getDireccion(), "La dirección");
        Validaciones.validarLongitud(refugio.getDireccion(), "La dirección", 200);

        refugio.setCif(Validaciones.validarCif(refugio.getCif()));

        refugio.setTelefono(
                Validaciones.validarTelefono(refugio.getTelefono())
        );

        refugio.setCorreo(
                Validaciones.validarEmail(refugio.getCorreo())
        );

        Validaciones.validarFecha(
                refugio.getFechaApertura(),
                "La fecha de apertura"
        );
    }
}