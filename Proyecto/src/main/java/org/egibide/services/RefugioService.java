package org.egibide.services;

import org.egibide.dao.RefugioDao;
import org.egibide.models.Refugio;

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

    // Validar los datos del refugio
    private void validar(Refugio refugio) {

        if (refugio == null) {
            throw new IllegalArgumentException("El refugio no puede ser nulo");
        }

        if (refugio.getNombre() == null || refugio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (refugio.getCif() == null || refugio.getCif().isBlank()) {
            throw new IllegalArgumentException("El CIF es obligatorio");
        }
    }
}
