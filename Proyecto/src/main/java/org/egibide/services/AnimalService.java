package org.egibide.services;

import org.bson.types.ObjectId;
import org.egibide.dao.AnimalDao;
import org.egibide.models.Animal;

import java.util.List;
import java.util.Set;

public class AnimalService {

    private final AnimalDao animalDao;

    public AnimalService() {
        animalDao = new AnimalDao();
    }

    // Crear un animal
    public void crear(Animal animal) {

        validar(animal);

        animal.setEliminado(false);

        if (animal.getEstado() == null || animal.getEstado().isBlank()) {
            animal.setEstado("disponible");
        }

        validarEstado(animal.getEstado());

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

    // Actualizar un animal
    public boolean actualizar(Animal animal) {

        validar(animal);

        if (animal.getId() == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        validarEstado(animal.getEstado());

        return animalDao.actualizar(animal);
    }

    // Dar de baja un animal
    public boolean darDeBaja(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID del animal es obligatorio");
        }

        return animalDao.darDeBaja(id);
    }

    // Validar los datos del animal
    private void validar(Animal animal) {

        if (animal == null) {
            throw new IllegalArgumentException("El animal no puede ser nulo");
        }

        if (animal.getNombre() == null || animal.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (animal.getEspecie() == null || animal.getEspecie().isBlank()) {
            throw new IllegalArgumentException("La especie es obligatoria");
        }

        if (animal.getEdad() != null && animal.getEdad() < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
    }

    // Validar el estado del animal
    private void validarEstado(String estado) {

        Set<String> estados = Set.of(
                "disponible",
                "reservado",
                "adoptado",
                "tratamiento"
        );

        if (estado == null || !estados.contains(estado.toLowerCase())) {
            throw new IllegalArgumentException("El estado del animal no es válido");
        }
    }
}
