package org.egibide.services;

import org.egibide.dao.AdoptanteDao;
import org.egibide.models.Adoptante;
import org.egibide.utils.Validaciones;

import java.util.Date;
import java.util.List;

public class AdoptanteService {

    private final AdoptanteDao adoptanteDao;

    public AdoptanteService() {
        adoptanteDao = new AdoptanteDao();
    }

    // Crear un adoptante
    public void crear(Adoptante adoptante) {

        validar(adoptante);

        if (adoptanteDao.buscarPorDni(adoptante.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe un adoptante con ese DNI");
        }

        adoptante.setFechaRegistro(new Date());
        adoptante.setEliminado(false);

        adoptanteDao.insertar(adoptante);
    }

    // Buscar un adoptante por DNI
    public Adoptante buscarPorDni(String dni) {

        validarDni(dni);

        return adoptanteDao.buscarPorDni(dni);
    }

    // Listar los adoptantes
    public List<Adoptante> listar() {
        return adoptanteDao.listar();
    }

    // Actualizar un adoptante
    public boolean actualizar(Adoptante adoptante) {

        validar(adoptante);

        return adoptanteDao.actualizar(adoptante);
    }

    // Dar de baja un adoptante
    public boolean darDeBaja(String dni) {

        validarDni(dni);

        return adoptanteDao.darDeBaja(dni);
    }

    // Validar los datos del adoptante
    private void validar(Adoptante adoptante) {

        if (adoptante == null) {
            throw new IllegalArgumentException("El adoptante no puede ser nulo");
        }

        // Normalizar y validar DNI
        adoptante.setDni(Validaciones.validarDni(adoptante.getDni()));

        Validaciones.validarTexto(adoptante.getNombre(), "El nombre");
        Validaciones.validarTexto(adoptante.getApellido1(), "El primer apellido");
        Validaciones.validarTexto(adoptante.getTelefono(), "El teléfono");
        Validaciones.validarTelefono(adoptante.getTelefono());
        Validaciones.validarTexto(adoptante.getDireccion(), "La dirección");
        Validaciones.validarTexto(adoptante.getEmail(), "El email");
        Validaciones.validarEmail(adoptante.getEmail());

        Validaciones.validarLongitud(adoptante.getNombre(), "El nombre", 100);
        Validaciones.validarLongitud(adoptante.getApellido1(), "El primer apellido", 100);
        Validaciones.validarLongitud(adoptante.getApellido2(), "El segundo apellido", 100);
    }

    // Validar el DNI
    private void validarDni(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
    }
}
