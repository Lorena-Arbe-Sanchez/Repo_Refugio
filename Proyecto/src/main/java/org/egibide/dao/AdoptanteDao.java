package org.egibide.dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.egibide.models.Adoptante;
import org.egibide.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AdoptanteDao {

    private final MongoCollection<Document> coleccion;

    public AdoptanteDao() {
        coleccion = DatabaseConnection.getInstance()
                .getCollection("adoptantes");
    }

    // Insertar un adoptante
    public void insertar(Adoptante adoptante) {

        Document documento = convertirDocumento(adoptante);
        coleccion.insertOne(documento);
    }

    // Buscar un adoptante por DNI
    public Adoptante buscarPorDni(String dni) {

        Document documento = coleccion.find(eq("_id", dni)).first();

        if (documento == null) {
            return null;
        }

        return convertirAdoptante(documento);
    }

    // Listar adoptantes no eliminados
    public List<Adoptante> listar() {

        List<Adoptante> adoptantes = new ArrayList<>();

        for (Document documento : coleccion.find(eq("eliminado", false))) {
            adoptantes.add(convertirAdoptante(documento));
        }

        return adoptantes;
    }

    // Actualizar un adoptante
    public boolean actualizar(Adoptante adoptante) {

        Document documento = convertirDocumento(adoptante);

        return coleccion.replaceOne(
                eq("_id", adoptante.getDni()), documento
        ).getMatchedCount() > 0;
    }

    // Dar de baja un adoptante
    public boolean darDeBaja(String dni) {

        return coleccion.updateOne(
                eq("_id", dni),
                new Document("$set", new Document("eliminado", true))
        ).getModifiedCount() > 0;
    }

    // Convertir Adoptante en Document
    private Document convertirDocumento(Adoptante adoptante) {

        return new Document("_id", adoptante.getDni())
                .append("nombre", adoptante.getNombre())
                .append("apellido1", adoptante.getApellido1())
                .append("apellido2", adoptante.getApellido2())
                .append("telefono", adoptante.getTelefono())
                .append("direccion", adoptante.getDireccion())
                .append("email", adoptante.getEmail())
                .append("fechaRegistro", adoptante.getFechaRegistro())
                .append("preferenciasAdopcion", adoptante.getPreferenciasAdopcion())
                .append("eliminado", adoptante.getEliminado());
    }

    // Convertir Document en Adoptante
    private Adoptante convertirAdoptante(Document documento) {

        Adoptante adoptante = new Adoptante();

        adoptante.setDni(documento.getString("_id"));
        adoptante.setNombre(documento.getString("nombre"));
        adoptante.setApellido1(documento.getString("apellido1"));
        adoptante.setApellido2(documento.getString("apellido2"));
        adoptante.setTelefono(documento.getString("telefono"));
        adoptante.setDireccion(documento.getString("direccion"));
        adoptante.setEmail(documento.getString("email"));
        adoptante.setFechaRegistro(documento.getDate("fechaRegistro"));
        adoptante.setPreferenciasAdopcion(
                documento.get("preferenciasAdopcion", Document.class)
        );
        adoptante.setEliminado(documento.getBoolean("eliminado", false));

        return adoptante;
    }
}