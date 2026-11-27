package org.egibide.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Metadatos {

    // Mostrar los metadatos por terminal
    public static void mostrarMetadatos() {

        System.out.println(obtenerMetadatos());
    }

    // Exportar los metadatos a un fichero TXT
    public static void exportarMetadatos() {

        String nombreArchivo = "datos/metadatos/metadatos_mongodb.txt";

        try {

            // Crear la carpeta si todavía no existe
            Files.createDirectories(Paths.get(nombreArchivo).getParent());

            // Escribir los metadatos en el fichero
            try (BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo))) {
                escritor.write(obtenerMetadatos());
            }

            System.out.println("✅ Metadatos exportados en: " + nombreArchivo + "\n");

        } catch (IOException e) {

            System.out.println("❌ Error al exportar los metadatos: " + e.getMessage());
        }
    }

    // Obtener todos los metadatos de la base de datos
    public static String obtenerMetadatos() {

        StringBuilder texto = new StringBuilder();

        try {

            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();

            // Obtener los nombres de las colecciones
            List<String> colecciones = new ArrayList<>();
            database.listCollectionNames().into(colecciones);

            // Añadir información general de la base de datos
            texto.append("METADATOS DE LA BASE DE DATOS\n");
            texto.append("========================================\n");
            texto.append("Nombre BBDD: ").append(database.getName()).append("\n\n");

            // Mostrar el listado general de colecciones
            texto.append("COLECCIONES DE LA BASE DE DATOS\n");
            texto.append("----------------------------------------\n");

            if (colecciones.isEmpty()) {
                texto.append("No hay colecciones\n");
            }

            for (String nombreColeccion : colecciones) {

                MongoCollection<Document> coleccion = database.getCollection(nombreColeccion);

                List<Document> indices = coleccion.listIndexes().into(new ArrayList<>());

                texto.append("- ").append(nombreColeccion)
                        .append(" | Documentos: ").append(coleccion.countDocuments())
                        .append(" | Índices: ").append(indices.size())
                        .append("\n");
            }

            texto.append("\n");

            // Mostrar los metadatos de cada colección
            for (String nombreColeccion : colecciones) {

                MongoCollection<Document> coleccion = database.getCollection(nombreColeccion);

                texto.append("\nCOLECCIÓN: ").append(nombreColeccion).append("\n");
                texto.append("========================================\n");

                // Mostrar el número de documentos
                texto.append("Número de documentos: ")
                        .append(coleccion.countDocuments())
                        .append("\n\n");

                // Obtener los índices de la colección
                texto.append("ÍNDICES:\n");

                List<Document> indices = coleccion.listIndexes().into(new ArrayList<>());

                if (indices.isEmpty()) {
                    texto.append("No tiene índices\n");
                }

                for (Document indice : indices) {

                    // Obtener el nombre y los campos del índice
                    String nombreIndice = indice.getString("name");
                    Document campos = indice.get("key", Document.class);

                    texto.append("- Nombre: ").append(nombreIndice).append("\n");
                    texto.append("  Campos: ");

                    if (campos != null) {

                        for (String campo : campos.keySet()) {

                            Object valor = campos.get(campo);

                            texto.append(campo)
                                    .append(" (").append(obtenerTipoIndice(valor)).append(") ");
                        }
                    }

                    texto.append("\n");

                    // Mostrar si el índice es único
                    texto.append("  Único: ")
                            .append(indice.getBoolean("unique", false) ? "Sí" : "No")
                            .append("\n");
                }

                // Obtener un documento de ejemplo
                Document documento = coleccion.find().first();

                texto.append("\nESTRUCTURA DE UN DOCUMENTO DE EJEMPLO:\n");

                if (documento != null) {
                    mostrarEstructura(texto, documento, 0);
                } else {
                    texto.append("La colección está vacía\n");
                }

                texto.append("\n");
            }

        } catch (Exception e) {

            texto.append("❌ Error al obtener los metadatos: ")
                    .append(e.getMessage())
                    .append("\n");
        }

        return texto.toString();
    }

    // Obtener el tipo de índice
    private static String obtenerTipoIndice(Object valor) {

        if (valor instanceof Number) {

            int direccion = ((Number) valor).intValue();

            if (direccion == 1) {
                return "B-tree ascendente";
            } else if (direccion == -1) {
                return "B-tree descendente";
            }

        } else if (valor instanceof String) {

            String tipo = (String) valor;

            switch (tipo) {

                case "text":
                    return "Texto";

                case "2dsphere":
                    return "Geoespacial 2dsphere";

                case "2d":
                    return "Geoespacial 2d";

                case "hashed":
                    return "Hash";

                case "wildcard":
                    return "Wildcard";

                default:
                    return tipo;
            }
        }

        return "Desconocido";
    }

    // Mostrar los campos y tipos de datos de un documento
    private static void mostrarEstructura(StringBuilder texto, Document documento, int nivel) {

        String espacios = "  ".repeat(nivel);

        for (String campo : documento.keySet()) {

            Object valor = documento.get(campo);

            texto.append(espacios)
                    .append("- ")
                    .append(campo)
                    .append(" | Tipo: ")
                    .append(obtenerTipo(valor))
                    .append("\n");

            // Mostrar los campos de los documentos anidados
            if (valor instanceof Document) {

                mostrarEstructura(texto, (Document) valor, nivel + 1);

                // Mostrar la estructura de los arrays
            } else if (valor instanceof List<?>) {

                List<?> lista = (List<?>) valor;

                if (!lista.isEmpty()) {

                    Object primerElemento = lista.get(0);

                    if (primerElemento instanceof Document) {
                        mostrarEstructura(texto, (Document) primerElemento, nivel + 1);
                    }
                }
            }
        }
    }

    // Obtener el tipo de dato de un campo
    private static String obtenerTipo(Object valor) {

        if (valor == null) {
            return "Null";
        }

        if (valor instanceof List<?>) {
            return "Array";
        }

        if (valor instanceof Document) {
            return "Documento";
        }

        return valor.getClass().getSimpleName();
    }
}