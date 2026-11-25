package org.egibide.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            System.out.println("Error al exportar los metadatos: " + e.getMessage());
        }
    }

    // Obtener todos los metadatos de la base de datos
    public static String obtenerMetadatos() {

        StringBuilder texto = new StringBuilder();

        try {

            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();

            // Añadir información general de la base de datos
            texto.append("METADATOS DE LA BASE DE DATOS\n");
            texto.append("----------------------------------------\n");
            texto.append("Nombre BBDD: ").append(database.getName()).append("\n\n");

            // Recorrer las colecciones de la base de datos
            for (String nombreColeccion : database.listCollectionNames()) {

                MongoCollection<Document> coleccion = database.getCollection(nombreColeccion);

                texto.append("COLECCIÓN: ").append(nombreColeccion).append("\n");
                texto.append("----------------------------------------\n");

                // Mostrar el número de documentos
                texto.append("Número de documentos: ")
                        .append(coleccion.countDocuments())
                        .append("\n");

                // Obtener los índices de la colección
                texto.append("\nÍndices:\n");

                List<Document> indices = coleccion.listIndexes().into(new java.util.ArrayList<>());

                for (Document indice : indices) {
                    texto.append("- ").append(indice.toJson()).append("\n");
                }

                // Obtener un documento de ejemplo
                Document documento = coleccion.find().first();

                texto.append("\nEstructura de un documento de ejemplo:\n");

                if (documento != null) {
                    mostrarEstructura(texto, documento, 0);
                } else {
                    texto.append("La colección está vacía\n");
                }

                texto.append("\n");
            }

        } catch (Exception e) {

            texto.append("Error al obtener los metadatos: ")
                    .append(e.getMessage())
                    .append("\n");
        }

        return texto.toString();
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