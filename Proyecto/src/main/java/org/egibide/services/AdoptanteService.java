package org.egibide.services;

import org.egibide.dao.AdoptanteDao;
import org.egibide.models.Adoptante;

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

        validarDni(adoptante.getDni());

        if (adoptante.getNombre() == null || adoptante.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (adoptante.getApellido1() == null || adoptante.getApellido1().isBlank()) {
            throw new IllegalArgumentException("El primer apellido es obligatorio");
        }
    }

    // Validar el DNI
    private void validarDni(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
    }
}
