package org.egibide;

import org.bson.types.ObjectId;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AdoptanteDao;
import org.egibide.dao.AnimalDao;
import org.egibide.dao.RefugioDao;
import org.egibide.models.Animal;
import org.egibide.services.AdopcionService;
import org.egibide.services.AdoptanteService;
import org.egibide.services.AnimalService;
import org.egibide.services.RefugioService;
import org.egibide.utils.DatabaseConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Constantes para establecer colores a los mensajes por consola
    private static final String COLOR_AMARILLO = "\u001B[93m";
    private static final String RESET_COLOR = "\u001B[0m";

    public static void main(String[] args) {

        DatabaseConnection conexion = null;

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();
        RefugioService refugioService = new RefugioService();

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
                        menuAnimales(sc, animalService);
                        break;

                    case 2:
                        menuAdoptantes(sc, adoptanteService);
                        break;

                    case 3:
                        menuAdopciones(sc, adopcionService);
                        break;

                    case 4:
                        menuRefugio(sc, refugioService);
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
    private static void menuAnimales(Scanner sc, AnimalService animalService) {

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
                    listarAnimales(animalService);
                    break;

                case 2:
                    crearAnimal(sc, animalService);
                    break;

                case 3:
                    modificarAnimal(sc, animalService);
                    break;

                case 4:
                    darDeBajaAnimal(sc, animalService);
                    break;

                case 5:
                    buscarAnimal(sc, animalService);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    private static void listarAnimales(AnimalService animalService) {

        List<Animal> animales = animalService.listar();

        if (animales.isEmpty()) {
            System.out.println("\nNo hay animales registrados");
            return;
        }

        System.out.println("\n===== LISTADO DE ANIMALES =====");

        for (Animal animal : animales) {
            mostrarAnimal(animal);
        }
    }

    private static void crearAnimal(Scanner sc, AnimalService animalService) {

        try {

            Animal animal = new Animal();

            System.out.println("\n===== ALTA DE ANIMAL =====");

            System.out.print("Nombre: ");
            animal.setNombre(sc.nextLine().trim());

            System.out.print("Especie: ");
            animal.setEspecie(sc.nextLine().trim());

            System.out.print("Raza: ");
            animal.setRaza(sc.nextLine().trim());

            animal.setEdad(leerEdad(sc));

            System.out.print("Sexo (macho/hembra/desconocido): ");
            animal.setSexo(sc.nextLine().trim());

            System.out.print("Descripción: ");
            animal.setDescripcion(sc.nextLine().trim());

            animal.setFechaIngreso(leerFecha(sc));

            // El servicio asigna el estado disponible por defecto
            animal.setHistorialMedico(new java.util.ArrayList<>());

            animalService.crear(animal);

            System.out.println("\n✅ Animal registrado correctamente");
            System.out.println("ID asignado: " + animal.getId());

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido registrar el animal: " + e.getMessage());
        }
    }

    private static void modificarAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== MODIFICAR ANIMAL =====");

            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID");
                return;
            }

            System.out.println("\nDatos actuales:");
            mostrarAnimal(animal);

            System.out.println("\nIntroduce los nuevos datos:");

            System.out.print("Nombre: ");
            animal.setNombre(sc.nextLine().trim());

            System.out.print("Especie: ");
            animal.setEspecie(sc.nextLine().trim());

            System.out.print("Raza: ");
            animal.setRaza(sc.nextLine().trim());

            animal.setEdad(leerEdad(sc));

            System.out.print("Sexo (macho/hembra/desconocido): ");
            animal.setSexo(sc.nextLine().trim());

            System.out.print("Descripción: ");
            animal.setDescripcion(sc.nextLine().trim());

            animal.setFechaIngreso(leerFecha(sc));

            if (animalService.actualizar(animal)) {
                System.out.println("\n✅ Animal modificado correctamente");
            } else {
                System.out.println("\n⚠️ No se ha podido modificar el animal");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido modificar el animal: " + e.getMessage());
        }
    }

    private static void darDeBajaAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== DAR DE BAJA UN ANIMAL =====");

            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID");
                return;
            }

            mostrarAnimal(animal);

            System.out.print("\n¿Quieres dar de baja este animal? (s/n): ");
            String confirmacion = sc.nextLine().trim();

            if (confirmacion.equalsIgnoreCase("s")) {

                if (animalService.darDeBaja(id)) {
                    System.out.println("\n✅ Animal dado de baja correctamente");
                } else {
                    System.out.println("\n⚠️ No se ha podido dar de baja el animal");
                }

            } else {
                System.out.println("\nOperación cancelada");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido dar de baja el animal: " + e.getMessage());
        }
    }

    private static void buscarAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== BUSCAR ANIMAL =====");

            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID");
            } else {
                mostrarAnimal(animal);
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido buscar el animal: " + e.getMessage());
        }
    }

    private static void mostrarAnimal(Animal animal) {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n------------------------------");
        System.out.println("ID: " + animal.getId());
        System.out.println("Nombre: " + animal.getNombre());
        System.out.println("Especie: " + animal.getEspecie());
        System.out.println("Raza: " + animal.getRaza());
        System.out.println("Edad: " + animal.getEdad());
        System.out.println("Sexo: " + animal.getSexo());
        System.out.println("Descripción: " + animal.getDescripcion());

        if (animal.getFechaIngreso() != null) {
            System.out.println("Fecha de ingreso: " +
                    formato.format(animal.getFechaIngreso()));
        }

        System.out.println("Estado: " + animal.getEstado());
        System.out.println("------------------------------");
    }

    // Menú de adoptantes
    private static void menuAdoptantes(Scanner sc, AdoptanteService adoptanteService) {

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
    private static void menuAdopciones(Scanner sc, AdopcionService adopcionService) {

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
    private static void menuRefugio(Scanner sc, RefugioService refugioService) {

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

    /* TODO
    FUNCIONES AUXILIARES
     */

    // Leer una opción del menú
    private static int leerOpcion(Scanner sc) {

        System.out.print("\nElige una opción: ");

        try {
            return Integer.parseInt(sc.nextLine().trim());

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Integer leerEdad(Scanner sc) {

        System.out.print("Edad: ");
        return Integer.parseInt(sc.nextLine().trim());
    }

    private static Date leerFecha(Scanner sc) throws ParseException {

        System.out.print("Fecha de ingreso (dd/MM/yyyy): ");

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);

        return formato.parse(sc.nextLine().trim());
    }
}