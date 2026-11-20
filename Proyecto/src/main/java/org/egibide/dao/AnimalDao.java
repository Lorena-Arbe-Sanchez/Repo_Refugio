package org.egibide.dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.egibide.models.Animal;
import org.egibide.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

public class AnimalDao {

    private final MongoCollection<Document> coleccion;

    public AnimalDao() {
        coleccion = DatabaseConnection.getInstance()
                .getCollection("animales");
    }

    // Insertar un animal
    public void insertar(Animal animal) {

        if (animal.getId() == null) {
            animal.setId(new ObjectId());
        }

        Document documento = convertirDocumento(animal);
        coleccion.insertOne(documento);
    }

    // Buscar un animal activo por su ID
    public Animal buscarPorId(ObjectId id) {

        Document documento = coleccion.find(and(
                eq("_id", id),
                ne("eliminado", true)
        )).first();

        if (documento == null) {
            return null;
        }

        return convertirAnimal(documento);
    }

    // Listar animales no eliminados
    public List<Animal> listar() {

        List<Animal> animales = new ArrayList<>();

        for (Document documento : coleccion.find(ne("eliminado", true))) {
            animales.add(convertirAnimal(documento));
        }

        return animales;
    }

    // Actualizar un animal activo
    public boolean actualizar(Animal animal) {

        if (animal.getId() == null) {
            return false;
        }

        Document documento = convertirDocumento(animal);

        return coleccion.replaceOne(
                and(
                        eq("_id", animal.getId()),
                        ne("eliminado", true)
                ),
                documento
        ).getMatchedCount() > 0;
    }

    // Dar de baja un animal
    public boolean darDeBaja(ObjectId id) {

        return coleccion.updateOne(
                and(
                        eq("_id", id),
                        ne("eliminado", true)
                ),
                new Document("$set", new Document("eliminado", true))
        ).getModifiedCount() > 0;
    }

    // Convertir Animal en Document
    private Document convertirDocumento(Animal animal) {

        return new Document("_id", animal.getId())
                .append("nombre", animal.getNombre())
                .append("especie", animal.getEspecie())
                .append("raza", animal.getRaza())
                .append("edad", animal.getEdad())
                .append("sexo", animal.getSexo())
                .append("descripcion", animal.getDescripcion())
                .append("fechaIngreso", animal.getFechaIngreso())
                .append("estado", animal.getEstado())
                .append("historialMedico", animal.getHistorialMedico())
                .append("eliminado", animal.getEliminado());
    }

    // Convertir Document en Animal
    private Animal convertirAnimal(Document documento) {

        Animal animal = new Animal();

        animal.setId(documento.getObjectId("_id"));
        animal.setNombre(documento.getString("nombre"));
        animal.setEspecie(documento.getString("especie"));
        animal.setRaza(documento.getString("raza"));
        animal.setEdad(documento.getInteger("edad"));
        animal.setSexo(documento.getString("sexo"));
        animal.setDescripcion(documento.getString("descripcion"));
        animal.setFechaIngreso(documento.getDate("fechaIngreso"));
        animal.setEstado(documento.getString("estado"));
        animal.setHistorialMedico(
                documento.getList("historialMedico", Document.class)
        );
        animal.setEliminado(documento.getBoolean("eliminado", false));

        return animal;
    }
}