package org.egibide.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.egibide.models.Refugio;
import org.egibide.utils.DatabaseConnection;

import static com.mongodb.client.model.Filters.eq;

public class RefugioDao {

    private final MongoCollection<Document> coleccion;

    public RefugioDao() {
        coleccion = DatabaseConnection.getInstance()
                .getCollection("refugio");
    }

    // Buscar el refugio
    public Refugio buscar() {

        Document documento = coleccion.find(eq("_id", "1")).first();

        if (documento == null) {
            return null;
        }

        return convertirRefugio(documento);
    }

    // Guardar o actualizar el refugio
    public void guardar(Refugio refugio) {

        Document documento = convertirDocumento(refugio);

        coleccion.replaceOne(
                eq("_id", "1"),
                documento,
                new ReplaceOptions().upsert(true)
        );
    }

    // Convertir Refugio en Document
    private Document convertirDocumento(Refugio refugio) {

        return new Document("_id", "1")
                .append("nombre", refugio.getNombre())
                .append("razonSocial", refugio.getRazonSocial())
                .append("fechaApertura", refugio.getFechaApertura())
                .append("direccion", refugio.getDireccion())
                .append("cif", refugio.getCif())
                .append("telefono", refugio.getTelefono())
                .append("correo", refugio.getCorreo());
    }

    // Convertir Document en Refugio
    private Refugio convertirRefugio(Document documento) {

        Refugio refugio = new Refugio();

        refugio.setId(documento.getString("_id"));
        refugio.setNombre(documento.getString("nombre"));
        refugio.setRazonSocial(documento.getString("razonSocial"));
        refugio.setFechaApertura(documento.getDate("fechaApertura"));
        refugio.setDireccion(documento.getString("direccion"));
        refugio.setCif(documento.getString("cif"));
        refugio.setTelefono(documento.getString("telefono"));
        refugio.setCorreo(documento.getString("correo"));

        return refugio;
    }
}