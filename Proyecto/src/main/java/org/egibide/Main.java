package org.egibide;

import org.egibide.utils.DatabaseConnection;

import java.util.Scanner;

public class Main {

    // Constantes para establecer colores a los mensajes por consola
    private static final String COLOR_AMARILLO = "\u001B[93m";
    private static final String RESET_COLOR = "\u001B[0m";

    public static void main(String[] args) {

        DatabaseConnection conexion = null;

        try (Scanner sc = new Scanner(System.in)) {

            // Inicializar la conexión con MongoDB
            conexion = DatabaseConnection.getInstance();

            System.out.println(COLOR_AMARILLO + "🔌 Conexión con MongoDB establecida");
            System.out.println("Base de datos: " +
                    conexion.getDatabase().getName());

            System.out.println("Colecciones:");

            for (String nombre : conexion.getDatabase().listCollectionNames()) {
                System.out.println("- " + nombre);
            }

            System.out.println("\n✅ Aplicación inicializada correctamente" + RESET_COLOR);

            /*
            // Colores ANSI básicos
            System.out.println("\u001B[30mNegro\u001B[0m");
            System.out.println("\u001B[31mRojo\u001B[0m");
            System.out.println("\u001B[32mVerde\u001B[0m");
            System.out.println("\u001B[33mAmarillo\u001B[0m");
            System.out.println("\u001B[34mAzul\u001B[0m");
            System.out.println("\u001B[35mMorado\u001B[0m");
            System.out.println("\u001B[36mCian\u001B[0m");
            System.out.println("\u001B[37mBlanco\u001B[0m");

            // Tonalidades claras e intensas
            System.out.println("\u001B[90mGris\u001B[0m");
            System.out.println("\u001B[91mRojo brillante\u001B[0m");
            System.out.println("\u001B[92mVerde brillante\u001B[0m");
            System.out.println("\u001B[93mAmarillo brillante\u001B[0m");
            System.out.println("\u001B[94mAzul brillante\u001B[0m");
            System.out.println("\u001B[95mMorado brillante\u001B[0m");
            System.out.println("\u001B[96mCian brillante\u001B[0m");
            System.out.println("\u001B[97mBlanco brillante\u001B[0m");
            */

            // Menú principal
            int opcion;

            do {

                System.out.println("\n===== MENÚ PRINCIPAL =====");
                System.out.println("1. Gestión de animales");
                System.out.println("2. Gestión de adoptantes");
                System.out.println("3. Gestión de adopciones");
                System.out.println("4. Gestión del refugio");
                System.out.println("5. Exportar datos a XML y JSON");
                System.out.println("6. Metadatos de MongoDB");
                System.out.println("7. Búsquedas y estadísticas");
                System.out.println("8. Ayuda");
                System.out.println("0. Salir");

                opcion = leerOpcion(sc);

                switch (opcion) {

                    case 1:
                        menuAnimales(sc);
                        break;

                    case 2:
                        menuAdoptantes(sc);
                        break;

                    case 3:
                        menuAdopciones(sc);
                        break;

                    case 4:
                        menuRefugio(sc);
                        break;

                    case 5:
                        System.out.println("\nFuncionalidad pendiente de implementar");
                        break;

                    case 6:
                        System.out.println("\nFuncionalidad pendiente de implementar");
                        break;

                    case 7:
                        System.out.println("\nFuncionalidad pendiente de implementar");
                        break;

                    case 8:
                        mostrarAyuda();
                        break;

                    case 0:
                        System.out.println("\n👋 ¡Hasta pronto!");
                        break;

                    default:
                        System.out.println("⚠️ Opción no válida");
                }

            } while (opcion != 0);

        } catch (Exception e) {

            System.out.println("❌ Error en la aplicación: " + e.getMessage());

        } finally {

            // Cerrar la conexión con MongoDB
            if (conexion != null) {
                conexion.close();
                System.out.println(COLOR_AMARILLO + "Conexión con MongoDB cerrada" + RESET_COLOR);
            }
        }
    }

    // Menú de animales
    private static void menuAnimales(Scanner sc) {

        int opcion;

        do {

            System.out.println("\n===== GESTIÓN DE ANIMALES =====");
            System.out.println("1. Listar animales");
            System.out.println("2. Dar de alta un animal");
            System.out.println("3. Modificar un animal");
            System.out.println("4. Dar de baja un animal");
            System.out.println("5. Buscar animales");
            System.out.println("0. Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("Funcionalidad pendiente de implementar");
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    // Menú de adoptantes
    private static void menuAdoptantes(Scanner sc) {

        int opcion;

        do {

            System.out.println("\n===== GESTIÓN DE ADOPTANTES =====");
            System.out.println("1. Listar adoptantes");
            System.out.println("2. Registrar un adoptante");
            System.out.println("3. Modificar un adoptante");
            System.out.println("4. Dar de baja un adoptante");
            System.out.println("5. Buscar adoptantes");
            System.out.println("0. Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("Funcionalidad pendiente de implementar");
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    // Menú de adopciones
    private static void menuAdopciones(Scanner sc) {

        int opcion;

        do {

            System.out.println("\n===== GESTIÓN DE ADOPCIONES =====");
            System.out.println("1. Listar adopciones");
            System.out.println("2. Registrar una solicitud de adopción");
            System.out.println("3. Aprobar una adopción");
            System.out.println("4. Rechazar una adopción");
            System.out.println("5. Buscar adopciones");
            System.out.println("0. Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("Funcionalidad pendiente de implementar");
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    // Menú del refugio
    private static void menuRefugio(Scanner sc) {

        int opcion;

        do {

            System.out.println("\n===== GESTIÓN DEL REFUGIO =====");
            System.out.println("1. Consultar datos del refugio");
            System.out.println("2. Modificar datos del refugio");
            System.out.println("0. Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                case 2:
                    System.out.println("Funcionalidad pendiente de implementar");
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    // Mostrar ayuda
    private static void mostrarAyuda() {

        System.out.println("\n===== AYUDA =====");
        System.out.println("1. Gestión de animales: administrar los animales del refugio");
        System.out.println("2. Gestión de adoptantes: administrar las personas adoptantes");
        System.out.println("3. Gestión de adopciones: gestionar las solicitudes de adopción");
        System.out.println("4. Gestión del refugio: consultar y modificar sus datos");
        System.out.println("5. Exportación: generar archivos XML y JSON");
        System.out.println("6. Metadatos: consultar la estructura de MongoDB");
        System.out.println("7. Búsquedas y estadísticas: consultar información de la aplicación");
        System.out.println("0. Salir de la aplicación");
    }

    // Leer una opción del menú
    private static int leerOpcion(Scanner sc) {

        System.out.print("\nElige una opción: ");

        try {
            return Integer.parseInt(sc.nextLine().trim());

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}