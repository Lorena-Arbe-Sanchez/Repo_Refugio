package org.egibide.services;

import org.bson.types.ObjectId;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AdoptanteDao;
import org.egibide.dao.AnimalDao;
import org.egibide.models.Adopcion;
import org.egibide.models.Adoptante;
import org.egibide.models.Animal;
import org.egibide.utils.Validaciones;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdopcionService {

    private final AdopcionDao adopcionDao;
    private final AnimalDao animalDao;
    private final AdoptanteDao adoptanteDao;

    public AdopcionService() {
        adopcionDao = new AdopcionDao();
        animalDao = new AnimalDao();
        adoptanteDao = new AdoptanteDao();
    }

    /* TODO
        Hay un detalle que debemos respetar: cuando se aprueba una solicitud, las demás solicitudes pendientes
        de ese animal ya no pueden seguir siendo aprobables. Mi recomendación es rechazarlas automáticamente
        con una observación como «El animal ha sido adoptado por otra solicitud» y registrar la misma fecha
        de resolución.
     */
    // Crear una solicitud de adopción
    public void crear(Adopcion adopcion) {

        validar(adopcion);

        Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());
        Adoptante adoptante = adoptanteDao.buscarPorDni(adopcion.getDniAdoptante());

        if (animal == null) {
            throw new IllegalArgumentException("El animal no existe o está eliminado");
        }

        if (adoptante == null || Boolean.TRUE.equals(adoptante.getEliminado())) {
            throw new IllegalArgumentException("El adoptante no existe o está eliminado");
        }

        String estadoAnimal = animal.getEstado() == null
                ? ""
                : animal.getEstado().trim().toLowerCase(Locale.ROOT);

        if (!estadoAnimal.equals("disponible") &&
                !estadoAnimal.equals("reservado")) {

            throw new IllegalArgumentException(
                    "El animal no está disponible para recibir solicitudes"
            );
        }

        List<Adopcion> solicitudes = adopcionDao.buscarPorAnimal(
                adopcion.getAnimalId()
        );

        for (Adopcion otra : solicitudes) {

            if (otra.getDniAdoptante().equals(adopcion.getDniAdoptante()) &&
                    "pendiente".equalsIgnoreCase(otra.getEstado())) {

                throw new IllegalArgumentException(
                        "El adoptante ya tiene una solicitud pendiente para este animal"
                );
            }

            if ("aprobada".equalsIgnoreCase(otra.getEstado())) {
                throw new IllegalArgumentException(
                        "El animal ya tiene una adopción aprobada"
                );
            }
        }

        adopcion.setFechaSolicitud(new Date());
        adopcion.setFechaResolucion(null);
        adopcion.setEstado("pendiente");
        adopcion.setEliminado(false);

        adopcionDao.insertar(adopcion);

        // Reservar el animal después de registrar la solicitud
        animal.setEstado("reservado");

        try {

            if (!animalDao.actualizar(animal)) {
                throw new IllegalStateException("No se ha podido reservar el animal");
            }

        } catch (RuntimeException e) {

            // Cancelar la solicitud si no se ha podido reservar el animal
            try {
                if (!adopcionDao.darDeBaja(adopcion.getId())) {
                    e.addSuppressed(new IllegalStateException(
                            "Tampoco se ha podido cancelar la solicitud"
                    ));
                }
            } catch (RuntimeException errorBaja) {
                e.addSuppressed(errorBaja);
            }

            throw e;
        }
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

        dni = Validaciones.validarDni(dni);

        return adopcionDao.buscarPorAdoptante(dni);
    }

    // TODO : Tener cuidado con lo de actualizar el estado (hay que hacer varias comprobaciones previamente, no?)
    // Actualizar observaciones y seguimiento de una adopción
    public boolean actualizar(Adopcion adopcion) {

        if (adopcion == null || adopcion.getId() == null) {
            throw new IllegalArgumentException("La adopción y su ID son obligatorios");
        }

        Adopcion actual = adopcionDao.buscarPorId(adopcion.getId());

        if (actual == null) {
            throw new IllegalArgumentException("La adopción no existe o está eliminada");
        }

        Validaciones.validarLongitud(
                adopcion.getObservaciones(),
                "Las observaciones",
                1000
        );

        // Mantener los datos que solo cambian mediante las operaciones correspondientes
        actual.setObservaciones(adopcion.getObservaciones());
        actual.setSeguimiento(adopcion.getSeguimiento());

        return adopcionDao.actualizar(actual);
    }

    // Aprobar una solicitud de adopción
    public boolean aprobar(ObjectId id) {

        Adopcion adopcion = buscarPorId(id);

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no existe o está eliminada");
        }

        if (!"pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException("La adopción no está pendiente");
        }

        Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());

        if (animal == null) {
            throw new IllegalArgumentException("El animal no existe o está eliminado");
        }

        Adoptante adoptante = adoptanteDao.buscarPorDni(adopcion.getDniAdoptante());

        if (adoptante == null || Boolean.TRUE.equals(adoptante.getEliminado())) {
            throw new IllegalArgumentException("El adoptante no existe o está eliminado");
        }

        if (!"reservado".equalsIgnoreCase(animal.getEstado())) {
            throw new IllegalArgumentException("El animal no está reservado");
        }

        List<Adopcion> solicitudes = adopcionDao.buscarPorAnimal(
                adopcion.getAnimalId()
        );

        for (Adopcion otra : solicitudes) {

            if (!otra.getId().equals(adopcion.getId()) &&
                    "aprobada".equalsIgnoreCase(otra.getEstado())) {

                throw new IllegalArgumentException(
                        "El animal ya tiene otra adopción aprobada"
                );
            }
        }

        Date fechaResolucion = new Date();
        String estadoAnterior = animal.getEstado();

        // Cambiar el estado del animal
        animal.setEstado("adoptado");

        if (!animalDao.actualizar(animal)) {
            throw new IllegalStateException("No se ha podido actualizar el animal");
        }

        // Aprobar la solicitud
        adopcion.setEstado("aprobada");
        adopcion.setFechaResolucion(fechaResolucion);

        if (!adopcionDao.actualizar(adopcion)) {

            animal.setEstado(estadoAnterior);

            if (!animalDao.actualizar(animal)) {
                throw new IllegalStateException(
                        "No se ha podido aprobar la adopción ni restaurar el estado del animal"
                );
            }

            throw new IllegalStateException("No se ha podido aprobar la adopción");
        }

        // Rechazar las demás solicitudes pendientes del mismo animal
        for (Adopcion otra : solicitudes) {

            if (!otra.getId().equals(adopcion.getId()) &&
                    "pendiente".equalsIgnoreCase(otra.getEstado())) {

                otra.setEstado("rechazada");
                otra.setFechaResolucion(fechaResolucion);

                String observaciones = otra.getObservaciones();

                if (observaciones == null || observaciones.isBlank()) {
                    observaciones = "El animal ha sido adoptado mediante otra solicitud";
                } else {
                    observaciones += " | El animal ha sido adoptado mediante otra solicitud";
                }

                otra.setObservaciones(observaciones);

                if (!adopcionDao.actualizar(otra)) {
                    throw new IllegalStateException(
                            "La adopción se ha aprobado, pero no se han podido actualizar todas las solicitudes restantes"
                    );
                }
            }
        }

        return true;
    }

    // Rechazar una solicitud de adopción
    public boolean rechazar(ObjectId id) {

        Adopcion adopcion = buscarPorId(id);

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no existe o está eliminada");
        }

        if (!"pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException("La adopción no está pendiente");
        }

        Animal animal = animalDao.buscarPorId(adopcion.getAnimalId());

        if (animal == null) {
            throw new IllegalStateException("El animal asociado no existe o está eliminado");
        }

        adopcion.setEstado("rechazada");
        adopcion.setFechaResolucion(new Date());

        if (!adopcionDao.actualizar(adopcion)) {
            return false;
        }

        // Comprobar si quedan otras solicitudes pendientes
        List<Adopcion> solicitudes = adopcionDao.buscarPorAnimal(
                adopcion.getAnimalId()
        );

        boolean quedanPendientes = solicitudes.stream()
                .anyMatch(a -> "pendiente".equalsIgnoreCase(a.getEstado()));

        if (!quedanPendientes &&
                "reservado".equalsIgnoreCase(animal.getEstado())) {

            animal.setEstado("disponible");

            if (!animalDao.actualizar(animal)) {

                // Intentar recuperar la solicitud pendiente
                adopcion.setEstado("pendiente");
                adopcion.setFechaResolucion(null);

                if (!adopcionDao.actualizar(adopcion)) {
                    throw new IllegalStateException(
                            "No se ha podido liberar el animal ni recuperar la solicitud"
                    );
                }

                throw new IllegalStateException("No se ha podido liberar el animal");
            }
        }

        return true;
    }

    // Dar de baja una solicitud de adopción
    public boolean darDeBaja(ObjectId id) {

        Adopcion adopcion = buscarPorId(id);

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no existe o está eliminada");
        }

        if ("pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException(
                    "Debes rechazar la solicitud antes de darla de baja"
            );
        }

        if ("aprobada".equalsIgnoreCase(adopcion.getEstado())) {
            throw new IllegalArgumentException(
                    "No se puede dar de baja una adopción aprobada"
            );
        }

        return adopcionDao.darDeBaja(id);
    }

    // Validar los datos de una adopción
    private void validar(Adopcion adopcion) {

        if (adopcion == null) {
            throw new IllegalArgumentException("La adopción no puede ser nula");
        }

        if (adopcion.getAnimalId() == null) {
            throw new IllegalArgumentException("El animal es obligatorio");
        }

        adopcion.setDniAdoptante(
                Validaciones.validarDni(adopcion.getDniAdoptante())
        );
    }
}