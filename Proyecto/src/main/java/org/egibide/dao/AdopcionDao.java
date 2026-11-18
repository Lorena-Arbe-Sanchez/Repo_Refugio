package org.egibide.dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.egibide.models.Adopcion;
import org.egibide.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AdopcionDao {

    private final MongoCollection<Document> coleccion;

    public AdopcionDao() {
        coleccion = DatabaseConnection.getInstance()
                .getCollection("adopciones");
    }

    // Insertar una adopción
    public void insertar(Adopcion adopcion) {

        if (adopcion.getId() == null) {
            adopcion.setId(new ObjectId());
        }

        Document documento = convertirDocumento(adopcion);
        coleccion.insertOne(documento);
    }

    // Buscar una adopción por ID
    public Adopcion buscarPorId(ObjectId id) {

        Document documento = coleccion.find(eq("_id", id)).first();

        if (documento == null) {
            return null;
        }

        return convertirAdopcion(documento);
    }

    // Listar adopciones no eliminadas
    public List<Adopcion> listar() {

        List<Adopcion> adopciones = new ArrayList<>();

        for (Document documento : coleccion.find(eq("eliminado", false))) {
            adopciones.add(convertirAdopcion(documento));
        }

        return adopciones;
    }

    // Buscar adopciones de un animal
    public List<Adopcion> buscarPorAnimal(ObjectId animalId) {

        List<Adopcion> adopciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                eq("animalId", animalId)
        )) {
            adopciones.add(convertirAdopcion(documento));
        }

        return adopciones;
    }

    // Buscar adopciones de un adoptante
    public List<Adopcion> buscarPorAdoptante(String dni) {

        List<Adopcion> adopciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                eq("dniAdoptante", dni)
        )) {
            adopciones.add(convertirAdopcion(documento));
        }

        return adopciones;
    }

    // Actualizar una adopción
    public boolean actualizar(Adopcion adopcion) {

        if (adopcion.getId() == null) {
            return false;
        }

        Document documento = convertirDocumento(adopcion);

        return coleccion.replaceOne(
                eq("_id", adopcion.getId()), documento
        ).getMatchedCount() > 0;
    }

    // Dar de baja una adopción
    public boolean darDeBaja(ObjectId id) {

        return coleccion.updateOne(
                eq("_id", id),
                new Document("$set", new Document("eliminado", true))
        ).getModifiedCount() > 0;
    }

    // Convertir Adopcion en Document
    private Document convertirDocumento(Adopcion adopcion) {

        return new Document("_id", adopcion.getId())
                .append("animalId", adopcion.getAnimalId())
                .append("dniAdoptante", adopcion.getDniAdoptante())
                .append("fechaSolicitud", adopcion.getFechaSolicitud())
                .append("fechaResolucion", adopcion.getFechaResolucion())
                .append("estado", adopcion.getEstado())
                .append("observaciones", adopcion.getObservaciones())
                .append("seguimiento", adopcion.getSeguimiento())
                .append("eliminado", adopcion.getEliminado());
    }

    // Convertir Document en Adopcion
    private Adopcion convertirAdopcion(Document documento) {

        Adopcion adopcion = new Adopcion();

        adopcion.setId(documento.getObjectId("_id"));
        adopcion.setAnimalId(documento.getObjectId("animalId"));
        adopcion.setDniAdoptante(documento.getString("dniAdoptante"));
        adopcion.setFechaSolicitud(documento.getDate("fechaSolicitud"));
        adopcion.setFechaResolucion(documento.getDate("fechaResolucion"));
        adopcion.setEstado(documento.getString("estado"));
        adopcion.setObservaciones(documento.getString("observaciones"));
        adopcion.setSeguimiento(
                documento.getList("seguimiento", Document.class)
        );
        adopcion.setEliminado(documento.getBoolean("eliminado", false));

        return adopcion;
    }
}