package org.egibide.services;

import org.bson.types.ObjectId;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AdoptanteDao;
import org.egibide.dao.AnimalDao;
import org.egibide.models.Adopcion;
import org.egibide.models.Adoptante;
import org.egibide.models.Animal;

import java.util.Date;
import java.util.List;

public class AdopcionService {

    private final AdopcionDao adopcionDao;
    private final AnimalDao animalDao;
    private final AdoptanteDao adoptanteDao;

    public AdopcionService() {
        adopcionDao = new AdopcionDao();
        animalDao = new AnimalDao();
        adoptanteDao = new AdoptanteDao();
    }

    // Crear una adopción
    public void crear(Adopcion adopcion) {

        validar(adopcion);

        Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());
        Adoptante adoptante = adoptanteDao.buscarPorDni(adopcion.getDniAdoptante());

        if (animal == null) {
            throw new IllegalArgumentException("El animal no existe");
        }

        if (adoptante == null) {
            throw new IllegalArgumentException("El adoptante no existe");
        }

        if ("adoptado".equalsIgnoreCase(animal.getEstado())) {
            throw new IllegalArgumentException("El animal ya está adoptado");
        }

        if ("tratamiento".equalsIgnoreCase(animal.getEstado())) {
            throw new IllegalArgumentException("El animal está en tratamiento");
        }

        adopcion.setFechaSolicitud(new Date());
        adopcion.setFechaResolucion(null);
        adopcion.setEstado("pendiente");
        adopcion.setEliminado(false);

        adopcionDao.insertar(adopcion);

        // Reservar el animal mientras tiene solicitudes pendientes
        animal.setEstado("reservado");
        animalDao.actualizar(animal);
    }

    // Buscar una adopción por ID
    public Adopcion buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID de la adopción es obligatorio");
        }

        return adopcionDao.buscarPorId(id);
    }

    // Listar las adopciones
    public List<Adopcion> listar() {
        return adopcionDao.listar();
    }

    // Buscar adopciones de un animal
    public List<Adopcion> buscarPorAnimal(ObjectId animalId) {

        if (animalId == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        return adopcionDao.buscarPorAnimal(animalId);
    }

    // Buscar adopciones de un adoptante
    public List<Adopcion> buscarPorAdoptante(String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        return adopcionDao.buscarPorAdoptante(dni);
    }

    // TODO : Tener cuidado con lo de actualizar el estado
    // Actualizar una adopción
    public boolean actualizar(Adopcion adopcion) {

        validar(adopcion);

        if (adopcion.getId() == null) {
            throw new IllegalArgumentException("El ID de la adopción es obligatorio");
        }

        return adopcionDao.actualizar(adopcion);
    }

    // Aprobar una adopción
    public boolean aprobar(ObjectId id) {

        Adopcion adopcion = buscarPorId(id);

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no existe");
        }

        if (!"pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException("La adopción no está pendiente");
        }

        Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());

        if (animal == null) {
            throw new IllegalArgumentException("El animal no existe");
        }

        if ("adoptado".equalsIgnoreCase(animal.getEstado())) {
            throw new IllegalArgumentException("El animal ya está adoptado");
        }

        if (!"reservado".equalsIgnoreCase(animal.getEstado())) {
            throw new IllegalArgumentException("El animal no está reservado");
        }

        // Comprobar que no exista otra adopción aprobada
        for (Adopcion otra : adopcionDao.buscarPorAnimal(adopcion.getAnimalId())) {

            if (!otra.getId().equals(adopcion.getId())
                    && "aprobada".equalsIgnoreCase(otra.getEstado())) {

                throw new IllegalArgumentException(
                        "El animal ya tiene otra adopción aprobada"
                );
            }
        }

        String estadoAnterior = animal.getEstado();

        // Actualizar primero el animal
        animal.setEstado("adoptado");

        if (!animalDao.actualizar(animal)) {
            throw new IllegalStateException("No se ha podido actualizar el animal");
        }

        // Actualizar la adopción
        adopcion.setEstado("aprobada");
        adopcion.setFechaResolucion(new Date());

        if (!adopcionDao.actualizar(adopcion)) {

            // Intentar recuperar el estado anterior del animal
            animal.setEstado(estadoAnterior);
            animalDao.actualizar(animal);

            throw new IllegalStateException(
                    "No se ha podido aprobar la adopción"
            );
        }

        return true;
    }

    // Rechazar una adopción
    public boolean rechazar(ObjectId id) {

        Adopcion adopcion = buscarPorId(id);

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no existe");
        }

        if (!"pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException("La adopción no está pendiente");
        }

        adopcion.setEstado("rechazada");
        adopcion.setFechaResolucion(new Date());

        if (!adopcionDao.actualizar(adopcion)) {
            return false;
        }

        // Comprobar si quedan otras solicitudes pendientes
        List<Adopcion> adopciones = adopcionDao.buscarPorAnimal(
                adopcion.getAnimalId()
        );

        boolean quedanPendientes = adopciones.stream()
                .anyMatch(a -> "pendiente".equalsIgnoreCase(a.getEstado()));

        if (!quedanPendientes) {

            Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());

            if (animal != null
                    && "reservado".equalsIgnoreCase(animal.getEstado())) {

                animal.setEstado("disponible");

                if (!animalDao.actualizar(animal)) {
                    throw new IllegalStateException(
                            "La adopción se ha rechazado, pero no se ha podido liberar el animal"
                    );
                }
            }
        }

        return true;
    }

    // Dar de baja una adopción
    public boolean darDeBaja(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID de la adopción es obligatorio");
        }

        return adopcionDao.darDeBaja(id);
    }

    // Validar los datos de la adopción
    private void validar(Adopcion adopcion) {

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no puede ser nula");
        }

        if (adopcion.getAnimalId() == null) {
            throw new IllegalArgumentException("El animal es obligatorio");
        }

        if (adopcion.getDniAdoptante() == null
                || adopcion.getDniAdoptante().isBlank()) {

            throw new IllegalArgumentException("El adoptante es obligatorio");
        }
    }
}
