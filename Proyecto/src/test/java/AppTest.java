import com.mongodb.client.MongoDatabase;
import org.egibide.dao.AdopcionDao;
import org.egibide.dao.AdoptanteDao;
import org.egibide.dao.AnimalDao;
import org.egibide.dao.RefugioDao;
import org.egibide.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private static final String PREFIJO = "TEST_";

    private AnimalDao animalDao;
    private AdoptanteDao adoptanteDao;
    private AdopcionDao adopcionDao;
    private RefugioDao refugioDao;

    @BeforeEach
    public void setUp() {
        // Inicializar los DAO
    }

    @AfterEach
    public void tearDown() {
        // Limpiar los registros de prueba
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

        DatabaseConnection conexion = DatabaseConnection.getInstance();

        MongoDatabase database = conexion.getDatabase();

        assertTrue(database.listCollectionNames().into(new java.util.ArrayList<>()).contains("animales"));
    }

    /**
     * Comprueba que se puede consultar la base de datos
     */
    @Test
    public void testConsultaBaseDatos() {

        DatabaseConnection conexion = DatabaseConnection.getInstance();

        assertDoesNotThrow(() -> conexion.getDatabase().listCollectionNames().first());
    }

    // ------------------------------------------
    // PRUEBAS DE ANIMALES
    // ------------------------------------------

    // testCRUDAnimal()
    // testBajaAnimal()
    // testBusquedasAnimal()

    // ------------------------------------------
    // PRUEBAS DE ADOPTANTES
    // ------------------------------------------

    // testCRUDAdoptante()
    // testBajaAdoptante()
    // testBusquedasAdoptante()

    // ------------------------------------------
    // PRUEBAS DE ADOPCIONES
    // ------------------------------------------

    // testCRUDAdopcion()
    // testBuscarAdopcionesPorAnimal()
    // testBuscarAdopcionesPorAdoptante()
    // testAprobarAdopcion()
    // testRechazarAdopcion()
    // testIntegridadReferencial()

    // ------------------------------------------
    // PRUEBAS DEL REFUGIO
    // ------------------------------------------

    // testCRUDRefugio()

    // ------------------------------------------
    // PRUEBAS DE EXPORTACIONES
    // ------------------------------------------

    // testExportacionesXML()
    // testExportacionesJSON()

    // ------------------------------------------
    // PRUEBAS DE METADATOS
    // ------------------------------------------

    // testMetadatos()
}