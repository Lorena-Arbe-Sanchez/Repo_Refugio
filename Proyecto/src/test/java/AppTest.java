import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AdoptanteDao;
import org.egibide.dao.AnimalDao;
import org.egibide.dao.RefugioDao;
import org.egibide.models.Adopcion;
import org.egibide.models.Adoptante;
import org.egibide.models.Animal;
import org.egibide.models.Refugio;
import org.egibide.services.AdopcionService;
import org.egibide.services.AdoptanteService;
import org.egibide.services.AnimalService;
import org.egibide.services.RefugioService;
import org.egibide.utils.DatabaseConnection;
import org.egibide.utils.Validaciones;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private static final String PREFIJO = "TEST_";

    private static final List<String> TEST_DNIS = List.of(
            "87654321X",
            "12345678Z",
            "23456789D",
            "34567890V",
            "45678901G",
            "56789012B",
            "56789013N",
            "56789014J"
    );

    private AnimalDao animalDao;
    private AdoptanteDao adoptanteDao;
    private AdopcionDao adopcionDao;
    private RefugioDao refugioDao;

    private Refugio refugioAnterior;

    private MongoDatabase database;

    @BeforeEach
    public void setUp() {

        DatabaseConnection conexion = DatabaseConnection.getInstance();

        database = conexion.getDatabase();

        // Inicializar los DAO
        animalDao = new AnimalDao();
        adoptanteDao = new AdoptanteDao();
        adopcionDao = new AdopcionDao();
        refugioDao = new RefugioDao();

        refugioAnterior = refugioDao.buscar();
    }

    @AfterEach
    public void tearDown() {

        MongoCollection<Document> animales = database.getCollection("animales");
        MongoCollection<Document> adopciones = database.getCollection("adopciones");
        MongoCollection<Document> adoptantes = database.getCollection("adoptantes");

        // Recuperar los IDs de los animales de prueba
        List<ObjectId> idsAnimales = animales.find(
                        regex("nombre", "^" + PREFIJO)
                ).map(documento -> documento.getObjectId("_id"))
                .into(new ArrayList<>());

        // Eliminar primero las adopciones relacionadas con las pruebas
        adopciones.deleteMany(or(
                in("dniAdoptante", TEST_DNIS),
                in("animalId", idsAnimales)
        ));

        // Eliminar los animales de prueba
        animales.deleteMany(regex("nombre", "^" + PREFIJO));

        // Eliminar los adoptantes de prueba
        adoptantes.deleteMany(in("_id", TEST_DNIS));

        // Restaurar los datos anteriores del refugio
        if (refugioAnterior != null) {
            refugioDao.guardar(refugioAnterior);
        } else {
            database.getCollection("refugio").deleteOne(eq("_id", "1"));
        }
    }

    // ------------------------------------------
    // PRUEBAS DE CONEXIÓN Y COLECCIONES
    // ------------------------------------------

    /**
     * Comprueba la conexión con MongoDB
     */
    @Test
    public void testConexion() {

        DatabaseConnection conexion = DatabaseConnection.getInstance();

        assertNotNull(conexion);
        assertNotNull(conexion.getDatabase());
        assertEquals("bd_refugio", conexion.getDatabase().getName());
    }

    /**
     * Comprueba que existen las colecciones necesarias
     */
    @Test
    public void testColecciones() {

        List<String> colecciones = database.listCollectionNames()
                .into(new ArrayList<>());

        assertTrue(colecciones.contains("animales"));
        assertTrue(colecciones.contains("adopciones"));
        assertTrue(colecciones.contains("adoptantes"));
        assertTrue(colecciones.contains("refugio"));
    }

    /**
     * Comprueba que se puede consultar la base de datos
     */
    @Test
    public void testConsultaBaseDatos() {

        assertDoesNotThrow(() ->
                database.listCollectionNames().first()
        );
    }

    // ------------------------------------------
    // PRUEBAS DE ANIMALES
    // ------------------------------------------

    @Test
    public void testCRUDAnimalDao() {

        Animal animal = crearAnimal("TEST_DAO_ANIMAL");

        animalDao.insertar(animal);

        assertNotNull(animal.getId());

        Animal encontrado = animalDao.buscarPorId(animal.getId());

        assertNotNull(encontrado);
        assertEquals(animal.getNombre(), encontrado.getNombre());

        encontrado.setDescripcion("Descripción modificada");

        assertTrue(animalDao.actualizar(encontrado));

        assertEquals(
                "Descripción modificada",
                animalDao.buscarPorId(animal.getId()).getDescripcion()
        );

        assertTrue(animalDao.listar().stream()
                .anyMatch(a -> a.getId().equals(animal.getId())));

        assertTrue(animalDao.darDeBaja(animal.getId()));

        assertNull(animalDao.buscarPorId(animal.getId()));

        assertFalse(animalDao.darDeBaja(animal.getId()));
    }

    @Test
    public void testAnimalService() {

        AnimalService servicio = new AnimalService();

        Animal animal = crearAnimal("TEST_SERVICE_ANIMAL");

        animal.setEstado(null);

        servicio.crear(animal);

        assertNotNull(animal.getId());
        assertEquals("disponible", animal.getEstado());

        Animal encontrado = servicio.buscarPorId(animal.getId());

        assertNotNull(encontrado);

        encontrado.setDescripcion("Animal actualizado");
        encontrado.setEstado("adoptado");

        assertTrue(servicio.actualizar(encontrado));

        Animal actualizado = servicio.buscarPorId(animal.getId());

        assertEquals("Animal actualizado", actualizado.getDescripcion());

        // La actualización normal no debe modificar el estado
        assertEquals("disponible", actualizado.getEstado());

        assertThrows(IllegalArgumentException.class, () ->
                servicio.crear(crearAnimalConSexo("TEST_SEXO_INVALIDO", "otro"))
        );
    }

    @Test
    public void testBajaAnimalConSolicitudesPendientes() {

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();

        Animal animal = crearAnimal("TEST_BAJA_ANIMAL");
        animalService.crear(animal);

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(0));
        adoptanteService.crear(adoptante);

        Adopcion adopcion = crearAdopcion(animal, adoptante);
        adopcionService.crear(adopcion);

        assertTrue(animalService.darDeBaja(animal.getId()));

        assertNull(animalService.buscarPorId(animal.getId()));

        Adopcion solicitud = adopcionService.buscarPorId(adopcion.getId());

        assertNotNull(solicitud);
        assertEquals("rechazada", solicitud.getEstado());
        assertNotNull(solicitud.getFechaResolucion());
    }

    // ------------------------------------------
    // PRUEBAS DE ADOPTANTES
    // ------------------------------------------

    @Test
    public void testCRUDAdoptanteDao() {

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(0));

        adoptanteDao.insertar(adoptante);

        Adoptante encontrado = adoptanteDao.buscarPorDni(adoptante.getDni());

        assertNotNull(encontrado);
        assertEquals(adoptante.getNombre(), encontrado.getNombre());

        encontrado.setNombre("Nombre modificado");

        assertTrue(adoptanteDao.actualizar(encontrado));

        assertEquals(
                "Nombre modificado",
                adoptanteDao.buscarPorDni(adoptante.getDni()).getNombre()
        );

        assertTrue(adoptanteDao.listar().stream()
                .anyMatch(a -> a.getDni().equals(adoptante.getDni())));

        assertTrue(adoptanteDao.darDeBaja(adoptante.getDni()));

        assertFalse(adoptanteDao.listar().stream()
                .anyMatch(a -> a.getDni().equals(adoptante.getDni())));
    }

    @Test
    public void testAdoptanteService() {

        AdoptanteService servicio = new AdoptanteService();

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(1));

        servicio.crear(adoptante);

        assertNotNull(adoptante.getFechaRegistro());

        Adoptante encontrado = servicio.buscarPorDni(adoptante.getDni());

        assertNotNull(encontrado);

        encontrado.setNombre("Nombre actualizado");

        assertTrue(servicio.actualizar(adoptante.getDni(), encontrado));

        assertEquals(
                "Nombre actualizado",
                servicio.buscarPorDni(adoptante.getDni()).getNombre()
        );

        // No se debe poder modificar el DNI
        Adoptante modificado = crearAdoptante(TEST_DNIS.get(2));

        assertThrows(IllegalArgumentException.class, () ->
                servicio.actualizar(adoptante.getDni(), modificado)
        );

        assertThrows(IllegalArgumentException.class, () ->
                servicio.crear(crearAdoptanteConDniInvalido())
        );

        assertTrue(servicio.darDeBaja(adoptante.getDni()));

        assertNull(servicio.buscarPorDni(adoptante.getDni()));
    }

    @Test
    public void testBajaAdoptanteConSolicitudPendiente() {

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();

        Animal animal = crearAnimal("TEST_ADOPTANTE_PENDIENTE");
        animalService.crear(animal);

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(3));
        adoptanteService.crear(adoptante);

        Adopcion adopcion = crearAdopcion(animal, adoptante);
        adopcionService.crear(adopcion);

        assertThrows(IllegalArgumentException.class, () ->
                adoptanteService.darDeBaja(adoptante.getDni())
        );
    }

    // ------------------------------------------
    // PRUEBAS DE ADOPCIONES
    // ------------------------------------------

    @Test
    public void testCRUDAdopcionDaoYBusquedas() {

        Animal animal = crearAnimal("TEST_ADOPCION_DAO");
        animalDao.insertar(animal);

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(4));
        adoptanteDao.insertar(adoptante);

        Adopcion adopcion = crearAdopcion(animal, adoptante);

        adopcionDao.insertar(adopcion);

        assertNotNull(adopcion.getId());

        assertNotNull(adopcionDao.buscarPorId(adopcion.getId()));

        assertEquals(1, adopcionDao.buscarPorAnimal(animal.getId()).size());

        assertEquals(1, adopcionDao.buscarPorAdoptante(adoptante.getDni()).size());

        adopcion.setObservaciones("Observaciones modificadas");

        assertTrue(adopcionDao.actualizar(adopcion));

        assertEquals(
                "Observaciones modificadas",
                adopcionDao.buscarPorId(adopcion.getId()).getObservaciones()
        );

        assertTrue(adopcionDao.darDeBaja(adopcion.getId()));

        assertNull(adopcionDao.buscarPorId(adopcion.getId()));
    }

    @Test
    public void testCrearAdopcionReservaAnimal() {

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();

        Animal animal = crearAnimal("TEST_RESERVA");
        animalService.crear(animal);

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(5));
        adoptanteService.crear(adoptante);

        Adopcion adopcion = crearAdopcion(animal, adoptante);

        adopcionService.crear(adopcion);

        assertNotNull(adopcion.getId());
        assertEquals("pendiente", adopcion.getEstado());
        assertNotNull(adopcion.getFechaSolicitud());
        assertNull(adopcion.getFechaResolucion());

        assertEquals(
                "reservado",
                animalService.buscarPorId(animal.getId()).getEstado()
        );

        // No permitir solicitudes pendientes duplicadas
        Adopcion duplicada = crearAdopcion(animal, adoptante);

        assertThrows(IllegalArgumentException.class, () ->
                adopcionService.crear(duplicada)
        );
    }

    @Test
    public void testAprobarAdopcionYRechazarLasDemas() {

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();

        Animal animal = crearAnimal("TEST_APROBAR");
        animalService.crear(animal);

        Adoptante adoptante1 = crearAdoptante(TEST_DNIS.get(0));
        Adoptante adoptante2 = crearAdoptante(TEST_DNIS.get(1));

        adoptanteService.crear(adoptante1);
        adoptanteService.crear(adoptante2);

        Adopcion adopcion1 = crearAdopcion(animal, adoptante1);
        Adopcion adopcion2 = crearAdopcion(animal, adoptante2);

        adopcionService.crear(adopcion1);
        adopcionService.crear(adopcion2);

        assertTrue(adopcionService.aprobar(adopcion1.getId()));

        assertEquals(
                "aprobada",
                adopcionService.buscarPorId(adopcion1.getId()).getEstado()
        );

        Adopcion segunda = adopcionService.buscarPorId(adopcion2.getId());

        assertEquals("rechazada", segunda.getEstado());
        assertNotNull(segunda.getFechaResolucion());

        assertEquals(
                "adoptado",
                animalService.buscarPorId(animal.getId()).getEstado()
        );

        // No se puede aprobar una solicitud ya resuelta
        assertThrows(IllegalArgumentException.class, () ->
                adopcionService.aprobar(adopcion2.getId())
        );
    }

    @Test
    public void testRechazarUltimaSolicitudLiberaAnimal() {

        AnimalService animalService = new AnimalService();
        AdoptanteService adoptanteService = new AdoptanteService();
        AdopcionService adopcionService = new AdopcionService();

        Animal animal = crearAnimal("TEST_RECHAZAR");
        animalService.crear(animal);

        Adoptante adoptante = crearAdoptante(TEST_DNIS.get(2));
        adoptanteService.crear(adoptante);

        Adopcion adopcion = crearAdopcion(animal, adoptante);
        adopcionService.crear(adopcion);

        assertTrue(adopcionService.rechazar(adopcion.getId()));

        Adopcion rechazada = adopcionService.buscarPorId(adopcion.getId());

        assertEquals("rechazada", rechazada.getEstado());
        assertNotNull(rechazada.getFechaResolucion());

        assertEquals(
                "disponible",
                animalService.buscarPorId(animal.getId()).getEstado()
        );
    }

    @Test
    public void testIntegridadReferencial() {

        AdopcionService servicio = new AdopcionService();

        Adopcion sinAnimal = new Adopcion();
        sinAnimal.setDniAdoptante(TEST_DNIS.get(0));

        assertThrows(IllegalArgumentException.class, () ->
                servicio.crear(sinAnimal)
        );

        Adopcion animalInexistente = new Adopcion();
        animalInexistente.setAnimalId(new ObjectId());
        animalInexistente.setDniAdoptante(TEST_DNIS.get(0));

        assertThrows(IllegalArgumentException.class, () ->
                servicio.crear(animalInexistente)
        );
    }

    // ------------------------------------------
    // PRUEBAS DEL REFUGIO
    // ------------------------------------------

    @Test
    public void testCRUDRefugio() {

        RefugioService servicio = new RefugioService();

        Refugio refugio = crearRefugio();

        servicio.guardar(refugio);

        Refugio encontrado = servicio.buscar();

        assertNotNull(encontrado);
        assertEquals("TEST_Refugio", encontrado.getNombre());
        assertEquals("A58818501", encontrado.getCif());

        encontrado.setNombre("TEST_Refugio_Modificado");

        servicio.guardar(encontrado);

        assertEquals(
                "TEST_Refugio_Modificado",
                servicio.buscar().getNombre()
        );

        assertThrows(IllegalArgumentException.class, () ->
                servicio.guardar(null)
        );
    }

    // ------------------------------------------
    // PRUEBAS DE VALIDACIONES
    // ------------------------------------------

    @Test
    public void testValidaciones() {

        assertEquals(
                "12345678Z",
                Validaciones.validarDni("12345678z")
        );

        assertThrows(IllegalArgumentException.class, () ->
                Validaciones.validarDni("12345678A")
        );

        assertEquals(
                "612345678",
                Validaciones.validarTelefono("612345678")
        );

        assertThrows(IllegalArgumentException.class, () ->
                Validaciones.validarTelefono("123456789")
        );

        assertEquals(
                "test@email.com",
                Validaciones.validarEmail("TEST@EMAIL.COM")
        );

        assertThrows(IllegalArgumentException.class, () ->
                Validaciones.validarEmail("correo-invalido")
        );

        assertEquals(
                "A58818501",
                Validaciones.validarCif("a58818501")
        );

        assertThrows(IllegalArgumentException.class, () ->
                Validaciones.validarCif("A58818502")
        );
    }

    // ------------------------------------------
    // MÉTODOS AUXILIARES
    // ------------------------------------------

    private Animal crearAnimal(String nombre) {

        Animal animal = new Animal();

        animal.setNombre(nombre);
        animal.setEspecie("Perro");
        animal.setRaza("Mestizo");
        animal.setEdad(2);
        animal.setSexo("macho");
        animal.setDescripcion("Animal de prueba");
        animal.setFechaIngreso(new Date());
        animal.setEstado("disponible");
        animal.setHistorialMedico(new ArrayList<>());
        animal.setEliminado(false);

        return animal;
    }

    private Animal crearAnimalConSexo(String nombre, String sexo) {

        Animal animal = crearAnimal(nombre);
        animal.setSexo(sexo);

        return animal;
    }

    private Adoptante crearAdoptante(String dni) {

        Adoptante adoptante = new Adoptante();

        adoptante.setDni(dni);
        adoptante.setNombre("TEST_Nombre");
        adoptante.setApellido1("TEST_Apellido");
        adoptante.setApellido2("Prueba");
        adoptante.setTelefono("612345678");
        adoptante.setDireccion("TEST_Direccion");
        adoptante.setEmail("test_" + dni.toLowerCase() + "@email.com");
        adoptante.setFechaRegistro(new Date());
        adoptante.setPreferenciasAdopcion(
                new Document("especie", "Perro")
        );
        adoptante.setEliminado(false);

        return adoptante;
    }

    private Adoptante crearAdoptanteConDniInvalido() {

        Adoptante adoptante = crearAdoptante("12345678Z");
        adoptante.setDni("12345678A");

        return adoptante;
    }

    private Adopcion crearAdopcion(Animal animal, Adoptante adoptante) {

        Adopcion adopcion = new Adopcion();

        adopcion.setAnimalId(animal.getId());
        adopcion.setDniAdoptante(adoptante.getDni());
        adopcion.setFechaSolicitud(new Date());
        adopcion.setEstado("pendiente");
        adopcion.setObservaciones("Solicitud de prueba");
        adopcion.setSeguimiento(new ArrayList<>());
        adopcion.setEliminado(false);

        return adopcion;
    }

    private Refugio crearRefugio() {

        Refugio refugio = new Refugio();

        refugio.setNombre("TEST_Refugio");
        refugio.setRazonSocial("TEST_Razon Social");
        refugio.setFechaApertura(new Date());
        refugio.setDireccion("TEST_Direccion");
        refugio.setCif("A58818501");
        refugio.setTelefono("612345678");
        refugio.setCorreo("test_refugio@email.com");

        return refugio;
    }
}

// TODO : Añadir pruebas de exportaciones y metadatos