package org.egibide.services;

import org.bson.types.ObjectId;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AnimalDao;
import org.egibide.models.Adopcion;
import org.egibide.models.Animal;
import org.egibide.utils.Validaciones;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AnimalService {

    private final AnimalDao animalDao;
    private final AdopcionDao adopcionDao;

    public AnimalService() {
        animalDao = new AnimalDao();
        adopcionDao = new AdopcionDao();
    }

    // Crear un animal
    public void crear(Animal animal) {

        validar(animal);

        animal.setEliminado(false);

        if (animal.getEstado() == null || animal.getEstado().isBlank()) {
            animal.setEstado("disponible");
        }

        animal.setEstado(validarEstado(animal.getEstado()));

        animalDao.insertar(animal);
    }

    // Buscar un animal por su ID
    public Animal buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        return animalDao.buscarPorId(id);
    }

    // Listar los animales
    public List<Animal> listar() {
        return animalDao.listar();
    }

    // Actualizar un animal sin modificar su estado
    public boolean actualizar(Animal animal) {

        validar(animal);

        if (animal.getId() == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        Animal actual = animalDao.buscarPorId(animal.getId());

        if (actual == null) {
            throw new IllegalArgumentException("El animal no existe o está eliminado");
        }

        animal.setEstado(actual.getEstado());

        return animalDao.actualizar(animal);
    }

    // TODO : Comprobar las adopciones pendientes/aprobadas (aunque es verdad q un animal podria fallecer antes de ser adoptado esté en el estado q esté; entonces se debería de poder marcar como eliminado por aquí, y entonces se le cambiaría "automáticamente" el estado mediante los otros servicios/daos y así)
    // Dar de baja un animal
    public boolean darDeBaja(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        Animal animal = animalDao.buscarPorId(id);

        if (animal == null) {
            throw new IllegalArgumentException("El animal no existe o está eliminado");
        }

        List<Adopcion> solicitudes = adopcionDao.buscarPorAnimal(id);
        Date fechaResolucion = new Date();

        // No permitir dar de baja a un animal que ya ha sido adoptado
        for (Adopcion adopcion : solicitudes) {

            if ("aprobada".equalsIgnoreCase(adopcion.getEstado())) {
                throw new IllegalArgumentException(
                        "No se puede dar de baja un animal con una adopción aprobada"
                );
            }
        }

        // Rechazar las solicitudes pendientes antes de dar de baja al animal
        for (Adopcion adopcion : solicitudes) {

            if ("pendiente".equalsIgnoreCase(adopcion.getEstado())) {

                adopcion.setEstado("rechazada");
                adopcion.setFechaResolucion(fechaResolucion);

                String observaciones = adopcion.getObservaciones();

                if (observaciones == null || observaciones.isBlank()) {
                    observaciones = "Animal dado de baja";
                } else {
                    observaciones += " | Animal dado de baja";
                }

                adopcion.setObservaciones(observaciones);

                if (!adopcionDao.actualizar(adopcion)) {
                    throw new IllegalStateException(
                            "No se han podido cancelar todas las solicitudes del animal"
                    );
                }
            }
        }

        return animalDao.darDeBaja(id);
    }

    // Validar los datos del animal
    private void validar(Animal animal) {

        if (animal == null) {
            throw new IllegalArgumentException("El animal no puede ser nulo");
        }

        Validaciones.validarTexto(animal.getNombre(), "El nombre");
        Validaciones.validarLongitud(animal.getNombre(), "El nombre", 100);

        Validaciones.validarTexto(animal.getEspecie(), "La especie");
        Validaciones.validarLongitud(animal.getEspecie(), "La especie", 50);

        Validaciones.validarLongitud(animal.getRaza(), "La raza", 100);
        Validaciones.validarLongitud(animal.getDescripcion(), "La descripción", 1000);

        if (animal.getEdad() == null || animal.getEdad() < 0) {
            throw new IllegalArgumentException("La edad debe ser igual o superior a cero");
        }

        if (animal.getSexo() == null ||
                !Set.of("macho", "hembra", "desconocido")
                        .contains(animal.getSexo().trim().toLowerCase(Locale.ROOT))) {

            throw new IllegalArgumentException("El sexo no es válido");
        }

        animal.setSexo(animal.getSexo().trim().toLowerCase(Locale.ROOT));

        Validaciones.validarFecha(animal.getFechaIngreso(), "La fecha de ingreso");
    }

    // Validar el estado del animal
    private String validarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        estado = estado.trim().toLowerCase(Locale.ROOT);

        Set<String> estados = Set.of(
                "disponible",
                "reservado",
                "adoptado",
                "tratamiento"
        );

        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("El estado del animal no es válido");
        }

        return estado;
    }
}