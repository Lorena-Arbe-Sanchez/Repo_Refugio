package org.egibide.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportarDatos {

    // Exportar animales a XML
    public static void exportarAnimalesXML() {
        exportarXML("animales", "Animales.xml");
    }

    // Exportar animales a JSON
    public static void exportarAnimalesJSON() {
        exportarJSON("animales", "Animales.json");
    }

    // Exportar adoptantes a XML
    public static void exportarAdoptantesXML() {
        exportarXML("adoptantes", "Adoptantes.xml");
    }

    // Exportar adoptantes a JSON
    public static void exportarAdoptantesJSON() {
        exportarJSON("adoptantes", "Adoptantes.json");
    }

    // Exportar adopciones a XML
    public static void exportarAdopcionesXML() {
        exportarXML("adopciones", "Adopciones.xml");
    }

    // Exportar adopciones a JSON
    public static void exportarAdopcionesJSON() {
        exportarJSON("adopciones", "Adopciones.json");
    }

    // Exportar refugio a XML
    public static void exportarRefugioXML() {
        exportarXML("refugio", "Refugio.xml");
    }

    // Exportar refugio a JSON
    public static void exportarRefugioJSON() {
        exportarJSON("refugio", "Refugio.json");
    }

    // Exportar todas las colecciones a XML
    public static void exportarTodoXML() {
        exportarAnimalesXML();
        exportarAdoptantesXML();
        exportarAdopcionesXML();
        exportarRefugioXML();
    }

    // Exportar todas las colecciones a JSON
    public static void exportarTodoJSON() {
        exportarAnimalesJSON();
        exportarAdoptantesJSON();
        exportarAdopcionesJSON();
        exportarRefugioJSON();
    }

    // Exportar todas las colecciones a XML y JSON
    public static void exportarTodoXMLyJSON() {
        exportarTodoXML();
        exportarTodoJSON();
    }

    // Obtener los registros de una colección
    private static List<Map<String, Object>> obtenerRegistros(String coleccion) {

        MongoCollection<Document> collection =
                DatabaseConnection.getInstance().getCollection(coleccion);

        List<Map<String, Object>> registros = new ArrayList<>();

        for (Document documento : collection.find()) {
            registros.add(convertirDocumento(documento));
        }

        return registros;
    }

    // Convertir un documento de MongoDB a un mapa
    private static Map<String, Object> convertirDocumento(Document documento) {

        Map<String, Object> registro = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entrada : documento.entrySet()) {
            registro.put(entrada.getKey(), convertirValor(entrada.getValue()));
        }

        return registro;
    }

    // Convertir los valores de MongoDB a tipos compatibles
    private static Object convertirValor(Object valor) {

        if (valor instanceof ObjectId objectId) {
            return objectId.toHexString();
        }

        if (valor instanceof Date fecha) {
            return fecha.toInstant().toString();
        }

        if (valor instanceof Document documento) {
            return convertirDocumento(documento);
        }

        if (valor instanceof Map<?, ?> mapa) {

            Map<String, Object> resultado = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
                resultado.put(
                        entrada.getKey().toString(),
                        convertirValor(entrada.getValue())
                );
            }

            return resultado;
        }

        if (valor instanceof Iterable<?> iterable) {

            List<Object> resultado = new ArrayList<>();

            for (Object elemento : iterable) {
                resultado.add(convertirValor(elemento));
            }

            return resultado;
        }

        return valor;
    }

    // Exportar una colección a XML
    private static void exportarXML(String coleccion, String nombreArchivo) {

        try {
            List<Map<String, Object>> registros = obtenerRegistros(coleccion);

            Path carpeta = Paths.get("datos", "xml");
            Files.createDirectories(carpeta);

            Path archivo = carpeta.resolve(nombreArchivo);

            XMLOutputFactory fabrica = XMLOutputFactory.newInstance();

            try (OutputStream salida = Files.newOutputStream(archivo)) {

                XMLStreamWriter escritor =
                        fabrica.createXMLStreamWriter(salida, "UTF-8");

                escritor.writeStartDocument("UTF-8", "1.0");
                escritor.writeStartElement("registros");

                for (Map<String, Object> registro : registros) {
                    escribirElemento(escritor, "registro", registro);
                }

                escritor.writeEndElement();
                escritor.writeEndDocument();
                escritor.close();
            }

            // TODO : Poner icono tick
            System.out.println("Archivo XML exportado: " + archivo);

        } catch (Exception e) {
            System.out.println("Error al exportar XML: " + e.getMessage());
        }
    }

    // Exportar una colección a JSON
    private static void exportarJSON(String coleccion, String nombreArchivo) {

        try {
            List<Map<String, Object>> registros = obtenerRegistros(coleccion);

            Path carpeta = Paths.get("datos", "json");
            Files.createDirectories(carpeta);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();

            String contenido = gson.toJson(registros);

            Path archivo = carpeta.resolve(nombreArchivo);

            Files.writeString(archivo, contenido);

            // TODO : Poner icono tick
            System.out.println("Archivo JSON exportado: " + archivo);

        } catch (Exception e) {
            System.out.println("Error al exportar JSON: " + e.getMessage());
        }
    }

    // Escribir los datos en formato XML
    private static void escribirElemento(
            XMLStreamWriter escritor, String nombre, Object valor)
            throws Exception {

        escritor.writeStartElement(nombre);

        if (valor instanceof Map<?, ?> mapa) {

            for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
                escribirElemento(
                        escritor,
                        entrada.getKey().toString(),
                        entrada.getValue()
                );
            }

        } else if (valor instanceof Iterable<?> iterable) {

            for (Object elemento : iterable) {
                escribirElemento(escritor, "elemento", elemento);
            }

        } else if (valor != null) {
            escritor.writeCharacters(valor.toString());
        }

        escritor.writeEndElement();
    }
}