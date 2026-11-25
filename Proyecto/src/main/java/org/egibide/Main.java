package org.egibide;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.egibide.models.Adopcion;
import org.egibide.models.Adoptante;
import org.egibide.models.Animal;
import org.egibide.models.Refugio;
import org.egibide.services.AdopcionService;
import org.egibide.services.AdoptanteService;
import org.egibide.services.AnimalService;
import org.egibide.services.RefugioService;
import org.egibide.utils.DatabaseConnection;
import org.egibide.utils.Exportaciones;
import org.egibide.utils.Metadatos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Constantes para establecer colores a los mensajes por consola
    private static final String COLOR_AMARILLO = "\u001B[93m";
    private static final String COLOR_AZUL = "\u001B[94m";
    private static final String RESET_COLOR = "\u001B[0m";

    public static void main(String[] args) {

        DatabaseConnection conexion = null;

        try (Scanner sc = new Scanner(System.in)) {

            // Inicializar la conexión con MongoDB
            conexion = DatabaseConnection.getInstance();

            System.out.println(COLOR_AMARILLO + "🔌 Conexión con MongoDB establecida.");
            System.out.println("Base de datos: " +
                    conexion.getDatabase().getName());

            System.out.println("Colecciones:");

            for (String nombre : conexion.getDatabase().listCollectionNames()) {
                System.out.println("- " + nombre);
            }

            System.out.println("\n✅ Aplicación inicializada correctamente." + RESET_COLOR);

            /* todo : poner colores y quitar esto
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

            AnimalService animalService = new AnimalService();
            AdoptanteService adoptanteService = new AdoptanteService();
            AdopcionService adopcionService = new AdopcionService();
            RefugioService refugioService = new RefugioService();

            mostrarDatosRefugio(refugioService);

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
                        menuExportaciones(sc);
                        break;

                    case 6:
                        Metadatos.mostrarMetadatos();
                        Metadatos.exportarMetadatos();
                        break;

                    case 7:
                        // TODO : Hacer lo que tenga "pendiente"
                        // TODO : Poner lo de "↩️ " en las opciones de "Volver..."
                        // TODO : Poner iconos en los "System.out.print" necesarios
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
                System.out.println(COLOR_AMARILLO + "Conexión con MongoDB cerrada." + RESET_COLOR);
            }
        }
    }

    private static void mostrarDatosRefugio(RefugioService refugioService) {

        System.out.println(COLOR_AZUL + "\n\n========== DATOS DEL REFUGIO ==========");

        try {
            Refugio refugio = refugioService.buscar();

            if (refugio == null) {
                System.out.println("No hay datos del refugio registrados.");
                return;
            }

            System.out.println("Nombre: " + refugio.getNombre());
            System.out.println("Razón social: " + refugio.getRazonSocial());
            System.out.println("Fecha de apertura: " +
                    new SimpleDateFormat("dd/MM/yyyy").format(refugio.getFechaApertura()));
            System.out.println("Dirección: " + refugio.getDireccion());
            System.out.println("CIF: " + refugio.getCif());
            System.out.println("Teléfono: " + refugio.getTelefono());
            System.out.println("Correo electrónico: " + refugio.getCorreo());

        } catch (Exception e) {
            System.out.println("Error al consultar los datos del refugio: " + e.getMessage());
        }

        System.out.println("=======================================\n" + RESET_COLOR);
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

    // TODO : Revisar y probar a partir de aquí
    private static void listarAnimales(AnimalService animalService) {

        List<Animal> animales = animalService.listar();

        if (animales.isEmpty()) {
            System.out.println("\nNo hay animales registrados.");
            return;
        }

        System.out.println("\n===== LISTADO DE ANIMALES =====");

        for (Animal animal : animales) {
            mostrarAnimal(animal);
        }
    }

    // TODO : En las funciones de crear (en la de "Crear solicitud de adopción" ya está implementado) hay q mostrar el ID del objeto recién creado para poder buscarlo luego
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

            // En todas las veces q haya q ponerlo, q se guarde en CamelCase
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
            System.out.println("\n❌ Error. No se ha podido registrar el animal: " + e.getMessage());
        }
    }

    // TODO : En las funciones de modificar hacer q saque la info actual + Lo de q si se da al Enter q se queden los datos actuales
    private static void modificarAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== MODIFICAR ANIMAL =====");

            // TODO : Antes de pedir el ID, que saque un listado de los IDs con sus nombres+especies+razas
            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID.");
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
            System.out.println("\n❌ Error. No se ha podido modificar el animal: " + e.getMessage());
        }
    }

    private static void darDeBajaAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== DAR DE BAJA UN ANIMAL =====");

            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID.");
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
                System.out.println("\nOperación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ Error. No se ha podido dar de baja el animal: " + e.getMessage());
        }
    }

    private static void buscarAnimal(Scanner sc, AnimalService animalService) {

        try {

            System.out.println("\n===== BUSCAR ANIMAL =====");

            System.out.print("Introduce el ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Animal animal = animalService.buscarPorId(id);

            if (animal == null) {
                System.out.println("\nNo existe ningún animal con ese ID.");
            } else {
                mostrarAnimal(animal);
            }

        } catch (Exception e) {
            System.out.println("\n❌ Error. No se ha podido buscar el animal: " + e.getMessage());
        }
    }

    private static void mostrarAnimal(Animal animal) {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

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
                    listarAdoptantes(adoptanteService);
                    break;

                case 2:
                    crearAdoptante(sc, adoptanteService);
                    break;

                case 3:
                    modificarAdoptante(sc, adoptanteService);
                    break;

                case 4:
                    darDeBajaAdoptante(sc, adoptanteService);
                    break;

                case 5:
                    buscarAdoptante(sc, adoptanteService);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    private static void listarAdoptantes(AdoptanteService adoptanteService) {

        List<Adoptante> adoptantes = adoptanteService.listar();

        if (adoptantes.isEmpty()) {
            System.out.println("\nNo hay adoptantes registrados.");
            return;
        }

        System.out.println("\n===== LISTADO DE ADOPTANTES =====");

        for (Adoptante adoptante : adoptantes) {
            mostrarAdoptante(adoptante);
        }
    }

    private static void crearAdoptante(Scanner sc, AdoptanteService adoptanteService) {

        try {

            Adoptante adoptante = new Adoptante();

            System.out.println("\n===== REGISTRAR ADOPTANTE =====");

            System.out.print("DNI: ");
            adoptante.setDni(sc.nextLine().trim());

            System.out.print("Nombre: ");
            adoptante.setNombre(sc.nextLine().trim());

            System.out.print("Primer apellido: ");
            adoptante.setApellido1(sc.nextLine().trim());

            System.out.print("Segundo apellido: ");
            adoptante.setApellido2(sc.nextLine().trim());

            System.out.print("Teléfono: ");
            adoptante.setTelefono(sc.nextLine().trim());

            System.out.print("Dirección: ");
            adoptante.setDireccion(sc.nextLine().trim());

            System.out.print("Correo electrónico: ");
            adoptante.setEmail(sc.nextLine().trim());

            System.out.print("Tipo de animal preferido: ");
            String tipoAnimal = sc.nextLine().trim();

            System.out.print("Tamaño preferido: ");
            String tamanoPreferido = sc.nextLine().trim();

            Document preferencias = new Document()
                    .append("tipoAnimal", tipoAnimal)
                    .append("tamanoPreferido", tamanoPreferido);

            adoptante.setPreferenciasAdopcion(preferencias);

            adoptanteService.crear(adoptante);

            System.out.println("\n✅ Adoptante registrado correctamente");

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido registrar el adoptante: " + e.getMessage());
        }
    }

    private static void modificarAdoptante(Scanner sc, AdoptanteService adoptanteService) {

        try {

            System.out.println("\n===== MODIFICAR ADOPTANTE =====");

            System.out.print("Introduce el DNI del adoptante: ");
            String dni = sc.nextLine().trim();

            Adoptante adoptante = adoptanteService.buscarPorDni(dni);

            if (adoptante == null) {
                System.out.println("\nNo existe ningún adoptante con ese DNI.");
                return;
            }

            mostrarAdoptante(adoptante);

            System.out.println("\nIntroduce los nuevos datos:");

            System.out.print("Nombre: ");
            adoptante.setNombre(sc.nextLine().trim());

            System.out.print("Primer apellido: ");
            adoptante.setApellido1(sc.nextLine().trim());

            System.out.print("Segundo apellido: ");
            adoptante.setApellido2(sc.nextLine().trim());

            System.out.print("Teléfono: ");
            adoptante.setTelefono(sc.nextLine().trim());

            System.out.print("Dirección: ");
            adoptante.setDireccion(sc.nextLine().trim());

            System.out.print("Correo electrónico: ");
            adoptante.setEmail(sc.nextLine().trim());

            System.out.print("Tipo de animal preferido: ");
            String tipoAnimal = sc.nextLine().trim();

            System.out.print("Tamaño preferido: ");
            String tamanoPreferido = sc.nextLine().trim();

            Document preferencias = new Document()
                    .append("tipoAnimal", tipoAnimal)
                    .append("tamanoPreferido", tamanoPreferido);

            adoptante.setPreferenciasAdopcion(preferencias);

            if (adoptanteService.actualizar(dni, adoptante)) {
                System.out.println("\n✅ Adoptante modificado correctamente");
            } else {
                System.out.println("\n⚠️ No se ha podido modificar el adoptante");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido modificar el adoptante: " + e.getMessage());
        }
    }

    private static void darDeBajaAdoptante(Scanner sc, AdoptanteService adoptanteService) {

        try {

            System.out.println("\n===== DAR DE BAJA ADOPTANTE =====");

            System.out.print("Introduce el DNI del adoptante: ");
            String dni = sc.nextLine().trim();

            Adoptante adoptante = adoptanteService.buscarPorDni(dni);

            if (adoptante == null) {
                System.out.println("\nNo existe ningún adoptante con ese DNI.");
                return;
            }

            mostrarAdoptante(adoptante);

            System.out.print("\n¿Quieres dar de baja este adoptante? (s/n): ");
            String confirmacion = sc.nextLine().trim();

            if (confirmacion.equalsIgnoreCase("s")) {

                if (adoptanteService.darDeBaja(dni)) {
                    System.out.println("\n✅ Adoptante dado de baja correctamente");
                } else {
                    System.out.println("\n⚠️ No se ha podido dar de baja el adoptante");
                }

            } else {
                System.out.println("\nOperación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido dar de baja el adoptante: " + e.getMessage());
        }
    }

    private static void buscarAdoptante(Scanner sc, AdoptanteService adoptanteService) {

        try {

            System.out.println("\n===== BUSCAR ADOPTANTE =====");

            System.out.print("Introduce el DNI: ");
            String dni = sc.nextLine().trim();

            Adoptante adoptante = adoptanteService.buscarPorDni(dni);

            if (adoptante == null) {
                System.out.println("\nNo existe ningún adoptante con ese DNI.");
            } else {
                mostrarAdoptante(adoptante);
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido buscar el adoptante: " + e.getMessage());
        }
    }

    private static void mostrarAdoptante(Adoptante adoptante) {

        System.out.println("DNI: " + adoptante.getDni());
        System.out.println("Nombre: " + adoptante.getNombre());
        System.out.println("Primer apellido: " + adoptante.getApellido1());
        System.out.println("Segundo apellido: " + adoptante.getApellido2());
        System.out.println("Teléfono: " + adoptante.getTelefono());
        System.out.println("Dirección: " + adoptante.getDireccion());
        System.out.println("Correo: " + adoptante.getEmail());

        if (adoptante.getFechaRegistro() != null) {
            System.out.println("Fecha de registro: " +
                    new SimpleDateFormat("dd/MM/yyyy").format(adoptante.getFechaRegistro()));
        }

        System.out.println("Preferencias: " + adoptante.getPreferenciasAdopcion());
        System.out.println("------------------------------");
    }

    // Menú de adopciones
    private static void menuAdopciones(Scanner sc, AdopcionService adopcionService) {

        int opcion;

        do {

            System.out.println("\n===== GESTIÓN DE ADOPCIONES =====");
            System.out.println("1. Listar adopciones");
            System.out.println("2. Crear solicitud de adopción");
            System.out.println("3. Buscar adopción por ID");
            System.out.println("4. Buscar adopciones por animal");
            System.out.println("5. Buscar adopciones por adoptante");
            System.out.println("6. Aprobar solicitud");
            System.out.println("7. Rechazar solicitud");
            System.out.println("8. Modificar observaciones");
            System.out.println("9. Dar de baja una solicitud rechazada");
            System.out.println("0. Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                    listarAdopciones(adopcionService);
                    break;

                case 2:
                    crearAdopcion(sc, adopcionService);
                    break;

                case 3:
                    buscarAdopcion(sc, adopcionService);
                    break;

                case 4:
                    buscarAdopcionesPorAnimal(sc, adopcionService);
                    break;

                case 5:
                    buscarAdopcionesPorAdoptante(sc, adopcionService);
                    break;

                case 6:
                    aprobarAdopcion(sc, adopcionService);
                    break;

                case 7:
                    rechazarAdopcion(sc, adopcionService);
                    break;

                case 8:
                    modificarObservacionesAdopcion(sc, adopcionService);
                    break;

                case 9:
                    darDeBajaAdopcion(sc, adopcionService);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    private static void listarAdopciones(AdopcionService adopcionService) {

        List<Adopcion> adopciones = adopcionService.listar();

        if (adopciones.isEmpty()) {
            System.out.println("\nNo hay adopciones registradas.");
            return;
        }

        System.out.println("\n===== LISTADO DE ADOPCIONES =====");

        for (Adopcion adopcion : adopciones) {
            mostrarAdopcion(adopcion);
        }
    }

    private static void crearAdopcion(Scanner sc, AdopcionService adopcionService) {

        try {

            Adopcion adopcion = new Adopcion();

            System.out.println("\n===== NUEVA SOLICITUD DE ADOPCIÓN =====");

            System.out.print("ID del animal: ");
            adopcion.setAnimalId(new ObjectId(sc.nextLine().trim()));

            System.out.print("DNI del adoptante: ");
            adopcion.setDniAdoptante(sc.nextLine().trim());

            System.out.print("Observaciones: ");
            adopcion.setObservaciones(sc.nextLine().trim());

            adopcion.setSeguimiento(new ArrayList<>());

            adopcionService.crear(adopcion);

            System.out.println("\n✅ Solicitud registrada correctamente");
            System.out.println("ID de la solicitud: " + adopcion.getId());
            System.out.println("Estado: pendiente");

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido crear la solicitud: " + e.getMessage());
        }
    }

    private static void buscarAdopcion(Scanner sc, AdopcionService adopcionService) {

        try {

            System.out.println("\n===== BUSCAR ADOPCIÓN =====");

            System.out.print("ID de la adopción: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Adopcion adopcion = adopcionService.buscarPorId(id);

            if (adopcion == null) {
                System.out.println("\nNo existe ninguna adopción con ese ID.");
            } else {
                mostrarAdopcion(adopcion);
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido buscar la adopción: " + e.getMessage());
        }
    }

    private static void buscarAdopcionesPorAnimal(
            Scanner sc,
            AdopcionService adopcionService) {

        try {

            System.out.println("\n===== ADOPCIONES DE UN ANIMAL =====");

            System.out.print("ID del animal: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            List<Adopcion> adopciones = adopcionService.buscarPorAnimal(id);

            if (adopciones.isEmpty()) {
                System.out.println("\nEl animal no tiene solicitudes de adopción");
                return;
            }

            for (Adopcion adopcion : adopciones) {
                mostrarAdopcion(adopcion);
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se han podido consultar las adopciones: " + e.getMessage());
        }
    }

    private static void buscarAdopcionesPorAdoptante(
            Scanner sc,
            AdopcionService adopcionService) {

        try {

            System.out.println("\n===== ADOPCIONES DE UN ADOPTANTE =====");

            System.out.print("DNI del adoptante: ");
            String dni = sc.nextLine().trim();

            List<Adopcion> adopciones = adopcionService.buscarPorAdoptante(dni);

            if (adopciones.isEmpty()) {
                System.out.println("\nEl adoptante no tiene solicitudes de adopción");
                return;
            }

            for (Adopcion adopcion : adopciones) {
                mostrarAdopcion(adopcion);
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se han podido consultar las adopciones: " + e.getMessage());
        }
    }

    private static void aprobarAdopcion(Scanner sc, AdopcionService adopcionService) {

        try {

            System.out.println("\n===== APROBAR SOLICITUD =====");

            System.out.print("ID de la adopción: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Adopcion adopcion = adopcionService.buscarPorId(id);

            if (adopcion == null) {
                System.out.println("\nNo existe ninguna adopción con ese ID.");
                return;
            }

            mostrarAdopcion(adopcion);

            System.out.print("\n¿Quieres aprobar esta solicitud? (s/n): ");
            String confirmacion = sc.nextLine().trim();

            if (confirmacion.equalsIgnoreCase("s")) {

                if (adopcionService.aprobar(id)) {
                    System.out.println("\n✅ Solicitud aprobada correctamente");
                    System.out.println("El animal pasa a estar adoptado");
                    System.out.println("Las demás solicitudes pendientes se rechazan automáticamente");
                } else {
                    System.out.println("\n⚠️ No se ha podido aprobar la solicitud");
                }

            } else {
                System.out.println("\nOperación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido aprobar la solicitud: " + e.getMessage());
        }
    }

    private static void rechazarAdopcion(Scanner sc, AdopcionService adopcionService) {

        try {

            System.out.println("\n===== RECHAZAR SOLICITUD =====");

            System.out.print("ID de la adopción: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Adopcion adopcion = adopcionService.buscarPorId(id);

            if (adopcion == null) {
                System.out.println("\nNo existe ninguna adopción con ese ID.");
                return;
            }

            mostrarAdopcion(adopcion);

            System.out.print("\n¿Quieres rechazar esta solicitud? (s/n): ");
            String confirmacion = sc.nextLine().trim();

            if (confirmacion.equalsIgnoreCase("s")) {

                if (adopcionService.rechazar(id)) {
                    System.out.println("\n✅ Solicitud rechazada correctamente");
                    System.out.println("Si no quedan más solicitudes pendientes, el animal vuelve a estar disponible");
                } else {
                    System.out.println("\n⚠️ No se ha podido rechazar la solicitud");
                }

            } else {
                System.out.println("\nOperación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido rechazar la solicitud: " + e.getMessage());
        }
    }

    private static void modificarObservacionesAdopcion(
            Scanner sc,
            AdopcionService adopcionService) {

        try {

            System.out.println("\n===== MODIFICAR OBSERVACIONES =====");

            System.out.print("ID de la adopción: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Adopcion adopcion = adopcionService.buscarPorId(id);

            if (adopcion == null) {
                System.out.println("\nNo existe ninguna adopción con ese ID.");
                return;
            }

            mostrarAdopcion(adopcion);

            System.out.print("\nNuevas observaciones: ");
            adopcion.setObservaciones(sc.nextLine().trim());

            if (adopcionService.actualizar(adopcion)) {
                System.out.println("\n✅ Observaciones modificadas correctamente");
            } else {
                System.out.println("\n⚠️ No se han podido modificar las observaciones");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se han podido modificar las observaciones: " + e.getMessage());
        }
    }

    private static void darDeBajaAdopcion(
            Scanner sc,
            AdopcionService adopcionService) {

        try {

            System.out.println("\n===== DAR DE BAJA UNA ADOPCIÓN =====");

            System.out.print("ID de la adopción: ");
            ObjectId id = new ObjectId(sc.nextLine().trim());

            Adopcion adopcion = adopcionService.buscarPorId(id);

            if (adopcion == null) {
                System.out.println("\nNo existe ninguna adopción con ese ID.");
                return;
            }

            mostrarAdopcion(adopcion);

            System.out.print("\n¿Quieres dar de baja esta solicitud? (s/n): ");
            String confirmacion = sc.nextLine().trim();

            if (confirmacion.equalsIgnoreCase("s")) {

                if (adopcionService.darDeBaja(id)) {
                    System.out.println("\n✅ Solicitud dada de baja correctamente");
                } else {
                    System.out.println("\n⚠️ No se ha podido dar de baja la solicitud");
                }

            } else {
                System.out.println("\nOperación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ No se ha podido dar de baja la solicitud: " + e.getMessage());
        }
    }

    private static void mostrarAdopcion(Adopcion adopcion) {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("ID de adopción: " + adopcion.getId());
        // TODO : A parte de sacar el ID del animal, sería mejor sacra tambn el nombre, especie y raza
        System.out.println("ID del animal: " + adopcion.getAnimalId());
        System.out.println("DNI del adoptante: " + adopcion.getDniAdoptante());

        if (adopcion.getFechaSolicitud() != null) {
            System.out.println("Fecha de solicitud: " +
                    formato.format(adopcion.getFechaSolicitud()));
        }

        if (adopcion.getFechaResolucion() != null) {
            System.out.println("Fecha de resolución: " +
                    formato.format(adopcion.getFechaResolucion()));
        }

        System.out.println("Estado: " + adopcion.getEstado());
        System.out.println("Observaciones: " + adopcion.getObservaciones());
        System.out.println("------------------------------");
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
                    consultarRefugio(refugioService);
                    break;

                case 2:
                    modificarRefugio(sc, refugioService);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("⚠️ Opción no válida");
            }

        } while (opcion != 0);
    }

    private static void consultarRefugio(RefugioService refugioService) {

        try {

            Refugio refugio = refugioService.buscar();

            if (refugio == null) {
                System.out.println("\nNo hay datos registrados del refugio.");
                return;
            }

            mostrarRefugio(refugio);

        } catch (Exception e) {
            System.out.println("\n❌ No se han podido consultar los datos: " + e.getMessage());
        }
    }

    private static void modificarRefugio(Scanner sc, RefugioService refugioService) {

        try {

            System.out.println("\n===== MODIFICAR DATOS DEL REFUGIO =====");

            Refugio refugio = refugioService.buscar();

            if (refugio == null) {
                System.out.println("\nNo hay datos del refugio que modificar.");
                return;
            }

            mostrarRefugio(refugio);

            System.out.println("\nIntroduce los nuevos datos:");

            System.out.print("Nombre: ");
            refugio.setNombre(sc.nextLine().trim());

            System.out.print("Razón social: ");
            refugio.setRazonSocial(sc.nextLine().trim());

            refugio.setFechaApertura(leerFecha(sc));

            System.out.print("Dirección: ");
            refugio.setDireccion(sc.nextLine().trim());

            System.out.print("CIF: ");
            refugio.setCif(sc.nextLine().trim());

            System.out.print("Teléfono: ");
            refugio.setTelefono(sc.nextLine().trim());

            System.out.print("Correo electrónico: ");
            refugio.setCorreo(sc.nextLine().trim());

            refugioService.guardar(refugio);

            System.out.println("\n✅ Datos del refugio modificados correctamente");

        } catch (Exception e) {
            System.out.println("\n❌ No se han podido modificar los datos: " + e.getMessage());
        }
    }

    private static void mostrarRefugio(Refugio refugio) {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n===== DATOS DEL REFUGIO =====");
        System.out.println("ID: " + refugio.getId());
        System.out.println("Nombre: " + refugio.getNombre());
        System.out.println("Razón social: " + refugio.getRazonSocial());

        if (refugio.getFechaApertura() != null) {
            System.out.println("Fecha de apertura: " +
                    formato.format(refugio.getFechaApertura()));
        }

        System.out.println("Dirección: " + refugio.getDireccion());
        System.out.println("CIF: " + refugio.getCif());
        System.out.println("Teléfono: " + refugio.getTelefono());
        System.out.println("Correo: " + refugio.getCorreo());
        System.out.println("==============================");
    }

    // Menú de exportaciones
    private static void menuExportaciones(Scanner sc) {

        int opcion;

        do {

            System.out.println("\n\n===== EXPORTAR DATOS =====");
            System.out.println("1. Exportar animales a XML");
            System.out.println("2. Exportar animales a JSON");
            System.out.println("3. Exportar adoptantes a XML");
            System.out.println("4. Exportar adoptantes a JSON");
            System.out.println("5. Exportar adopciones a XML");
            System.out.println("6. Exportar adopciones a JSON");
            System.out.println("7. Exportar refugio a XML");
            System.out.println("8. Exportar refugio a JSON");
            System.out.println("9. Exportar todo a XML");
            System.out.println("10. Exportar todo a JSON");
            System.out.println("11. Exportar todo a XML y JSON");
            System.out.println("0. ↩️ Volver al menú principal");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                    Exportaciones.exportarAnimalesXML();
                    break;

                case 2:
                    Exportaciones.exportarAnimalesJSON();
                    break;

                case 3:
                    Exportaciones.exportarAdoptantesXML();
                    break;

                case 4:
                    Exportaciones.exportarAdoptantesJSON();
                    break;

                case 5:
                    Exportaciones.exportarAdopcionesXML();
                    break;

                case 6:
                    Exportaciones.exportarAdopcionesJSON();
                    break;

                case 7:
                    Exportaciones.exportarRefugioXML();
                    break;

                case 8:
                    Exportaciones.exportarRefugioJSON();
                    break;

                case 9:
                    Exportaciones.exportarTodoXML();
                    break;

                case 10:
                    Exportaciones.exportarTodoJSON();
                    break;

                case 11:
                    Exportaciones.exportarTodoXMLyJSON();
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

    /* TODO : Poner más, y las q hay ponerles bucle y de todo bien (buscar por "sc.next")
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

    // TODO : Tiene q ser positivo
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