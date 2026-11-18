package org.egibide;

import org.egibide.utils.DatabaseConnection;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnection conexion = DatabaseConnection.getInstance();

            System.out.println("Conexión con MongoDB establecida");
            System.out.println("Base de datos: " +
                    conexion.getDatabase().getName());

            System.out.println("Colecciones:");

            for (String nombre : conexion.getDatabase().listCollectionNames()) {
                System.out.println("- " + nombre);
            }

            conexion.close();

        } catch (Exception e) {
            System.out.println("Error al conectar con MongoDB: " + e.getMessage());
        }
    }
}

// RESULTADO:
/*
Conexión con MongoDB establecida
Base de datos: bd_refugio
Colecciones:
- animales
- adopciones
- adoptantes
- refugio
 */