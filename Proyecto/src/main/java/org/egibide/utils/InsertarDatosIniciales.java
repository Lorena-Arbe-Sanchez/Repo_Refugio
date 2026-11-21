package org.egibide.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class InsertarDatosIniciales {

    public static void main(String[] args) {

        DatabaseConnection conexion = null;

        try {

            // Conectar con MongoDB
            conexion = DatabaseConnection.getInstance();

            MongoDatabase db = conexion.getDatabase();

            MongoCollection<Document> animales = db.getCollection("animales");
            MongoCollection<Document> adoptantes = db.getCollection("adoptantes");
            MongoCollection<Document> adopciones = db.getCollection("adopciones");
            MongoCollection<Document> refugio = db.getCollection("refugio");

            // Comprobar que las colecciones están vacías
            if (animales.countDocuments() > 0 ||
                    adoptantes.countDocuments() > 0 ||
                    adopciones.countDocuments() > 0 ||
                    refugio.countDocuments() > 0) {

                System.out.println("⚠️ Ya existen datos en las colecciones");
                System.out.println("No se han insertado nuevos documentos");
                return;
            }

            System.out.println("Insertando datos iniciales...\n");

            // 1. Insertar refugio
            Document datosRefugio = new Document("_id", "1")
                    .append("nombre", "Refugio Huellas Felices")
                    .append("razonSocial", "Asociación Protectora Huellas Felices")
                    .append("fechaApertura", fecha(2015, 5, 12))
                    .append("direccion", "Calle Los Animales 15, Vitoria-Gasteiz")
                    .append("cif", "G01020304")
                    .append("telefono", "945123456")
                    .append("correo", "contacto@huellasfelices.com");

            refugio.insertOne(datosRefugio);

            System.out.println("✅ Refugio insertado");

            // 2. Crear identificadores de animales
            ObjectId a1 = new ObjectId();
            ObjectId a2 = new ObjectId();
            ObjectId a3 = new ObjectId();
            ObjectId a4 = new ObjectId();
            ObjectId a5 = new ObjectId();
            ObjectId a6 = new ObjectId();
            ObjectId a7 = new ObjectId();
            ObjectId a8 = new ObjectId();
            ObjectId a9 = new ObjectId();
            ObjectId a10 = new ObjectId();
            ObjectId a11 = new ObjectId();
            ObjectId a12 = new ObjectId();
            ObjectId a13 = new ObjectId();
            ObjectId a14 = new ObjectId();
            ObjectId a15 = new ObjectId();

            // 3. Insertar animales
            List<Document> listaAnimales = List.of(

                    animal(a1, "Luna", "Perro", "Labrador", 4, "Hembra",
                            "Perra cariñosa y sociable", "adoptado",
                            fecha(2025, 1, 10)),

                    animal(a2, "Max", "Perro", "Pastor alemán", 6, "Macho",
                            "Perro tranquilo y obediente", "adoptado",
                            fecha(2025, 2, 15)),

                    animal(a3, "Mia", "Gato", "Europeo", 2, "Hembra",
                            "Gata juguetona y sociable", "adoptado",
                            fecha(2025, 3, 20)),

                    animal(a4, "Rocky", "Perro", "Bóxer", 3, "Macho",
                            "Perro activo y cariñoso", "reservado",
                            fecha(2025, 4, 5)),

                    animal(a5, "Nala", "Gato", "Siamés", 1, "Hembra",
                            "Gata curiosa y juguetona", "reservado",
                            fecha(2025, 5, 12)),

                    animal(a6, "Toby", "Perro", "Beagle", 5, "Macho",
                            "Perro sociable y alegre", "reservado",
                            fecha(2025, 6, 8)),

                    animal(a7, "Coco", "Conejo", "Enano", 2, "Macho",
                            "Conejo tranquilo y dócil", "reservado",
                            fecha(2025, 7, 18)),

                    animal(a8, "Simba", "Gato", "Persa", 4, "Macho",
                            "Gato independiente y tranquilo", "disponible",
                            fecha(2025, 8, 2)),

                    animal(a9, "Kira", "Perro", "Border Collie", 3, "Hembra",
                            "Perra inteligente y activa", "disponible",
                            fecha(2025, 9, 14)),

                    animal(a10, "Daisy", "Conejo", "Belier", 1, "Hembra",
                            "Coneja pequeña y sociable", "disponible",
                            fecha(2025, 10, 21)),

                    animal(a11, "Thor", "Perro", "Mastín", 7, "Macho",
                            "Perro grande y tranquilo", "disponible",
                            fecha(2025, 11, 3)),

                    animal(a12, "Lola", "Gato", "Europeo", 5, "Hembra",
                            "Gata cariñosa", "disponible",
                            fecha(2025, 12, 9)),

                    animal(a13, "Nico", "Perro", "Podenco", 2, "Macho",
                            "Perro en recuperación", "tratamiento",
                            fecha(2026, 1, 17)),

                    animal(a14, "Milo", "Hurón", "Doméstico", 2, "Macho",
                            "Hurón curioso y juguetón", "disponible",
                            fecha(2026, 2, 11)),

                    animal(a15, "Bella", "Perro", "Caniche", 8, "Hembra",
                            "Perra mayor y muy tranquila", "disponible",
                            fecha(2026, 3, 6))
            );

            animales.insertMany(listaAnimales);

            System.out.println("✅ 15 animales insertados");

            // 4. Insertar adoptantes
            List<Document> listaAdoptantes = List.of(

                    adoptante("12345678Z", "Laura", "García", "Pérez",
                            "600123456", "Calle Mayor 10, Vitoria-Gasteiz",
                            "laura@email.com", "Perro", "Mediano"),

                    adoptante("23456789D", "Carlos", "Martínez", "López",
                            "600234567", "Calle Rioja 25, Vitoria-Gasteiz",
                            "carlos@email.com", "Gato", "Pequeño"),

                    adoptante("34567890V", "Marta", "Fernández", "Ruiz",
                            "600345678", "Avenida Gasteiz 30, Vitoria-Gasteiz",
                            "marta@email.com", "Perro", "Grande"),

                    adoptante("45678901G", "Javier", "López", "Sánchez",
                            "600456789", "Calle Francia 12, Vitoria-Gasteiz",
                            "javier@email.com", "Gato", "Cualquiera"),

                    adoptante("56789012B", "Elena", "Sánchez", "Gómez",
                            "600567890", "Calle Paz 8, Vitoria-Gasteiz",
                            "elena@email.com", "Conejo", "Pequeño"),

                    adoptante("67890123B", "David", "Ruiz", "Martín",
                            "600678901", "Calle Florida 19, Vitoria-Gasteiz",
                            "david@email.com", "Perro", "Grande"),

                    adoptante("78901234X", "Andrea", "Gómez", "Díaz",
                            "600789012", "Calle Francia 40, Vitoria-Gasteiz",
                            "andrea@email.com", "Gato", "Pequeño"),

                    adoptante("89012345E", "Pablo", "Díaz", "Moreno",
                            "600890123", "Calle San Prudencio 5, Vitoria-Gasteiz",
                            "pablo@email.com", "Perro", "Mediano"),

                    adoptante("90123456A", "Sara", "Moreno", "Alonso",
                            "600901234", "Calle Portal del Rey 16, Vitoria-Gasteiz",
                            "sara@email.com", "Conejo", "Pequeño"),

                    adoptante("11223344B", "Alberto", "Alonso", "Castro",
                            "600112233", "Calle Dato 22, Vitoria-Gasteiz",
                            "alberto@email.com", "Perro", "Cualquiera")
            );

            adoptantes.insertMany(listaAdoptantes);

            System.out.println("✅ 10 adoptantes insertados");

            // 5. Insertar adopciones
            List<Document> listaAdopciones = List.of(

                    adopcion(a1, "12345678Z", "aprobada",
                            fecha(2025, 2, 1), fecha(2025, 2, 10),
                            "Adopción completada correctamente"),

                    adopcion(a2, "23456789D", "aprobada",
                            fecha(2025, 3, 1), fecha(2025, 3, 15),
                            "Adopción aprobada tras la entrevista"),

                    adopcion(a3, "34567890V", "aprobada",
                            fecha(2025, 4, 1), fecha(2025, 4, 12),
                            "Adopción completada"),

                    adopcion(a4, "45678901G", "pendiente",
                            fecha(2026, 10, 10), null,
                            "Pendiente de entrevista"),

                    adopcion(a5, "56789012B", "pendiente",
                            fecha(2026, 10, 15), null,
                            "Pendiente de valoración"),

                    adopcion(a6, "67890123B", "pendiente",
                            fecha(2026, 10, 20), null,
                            "Pendiente de visita al refugio"),

                    adopcion(a7, "78901234X", "pendiente",
                            fecha(2026, 11, 1), null,
                            "Solicitud pendiente de revisión"),

                    adopcion(a8, "89012345E", "rechazada",
                            fecha(2026, 8, 1), fecha(2026, 8, 10),
                            "No cumple los requisitos de adopción"),

                    adopcion(a9, "90123456A", "rechazada",
                            fecha(2026, 8, 15), fecha(2026, 8, 20),
                            "Solicitud rechazada tras la entrevista"),

                    adopcion(a10, "11223344B", "rechazada",
                            fecha(2026, 9, 1), fecha(2026, 9, 5),
                            "El adoptante ha retirado su solicitud")
            );

            adopciones.insertMany(listaAdopciones);

            System.out.println("✅ 10 adopciones insertadas");

            // Resumen
            System.out.println("\n===== RESUMEN DE INSERCIÓN =====");
            System.out.println("Refugio: " + refugio.countDocuments());
            System.out.println("Animales: " + animales.countDocuments());
            System.out.println("Adoptantes: " + adoptantes.countDocuments());
            System.out.println("Adopciones: " + adopciones.countDocuments());

            System.out.println("\n✅ Datos iniciales insertados correctamente");

        } catch (Exception e) {

            System.out.println("❌ Error al insertar los datos: " + e.getMessage());

        } finally {

            if (conexion != null) {
                conexion.close();
            }
        }
    }

    // Crear un documento de animal
    private static Document animal(ObjectId id, String nombre,
                                   String especie, String raza, int edad,
                                   String sexo, String descripcion,
                                   String estado, Date fechaIngreso) {

        return new Document("_id", id)
                .append("nombre", nombre)
                .append("especie", especie)
                .append("raza", raza)
                .append("edad", edad)
                .append("sexo", sexo)
                .append("descripcion", descripcion)
                .append("fechaIngreso", fechaIngreso)
                .append("estado", estado)
                .append("historialMedico", List.of())
                .append("eliminado", false);
    }

    // Crear un documento de adoptante
    private static Document adoptante(String dni, String nombre,
                                      String apellido1, String apellido2,
                                      String telefono, String direccion,
                                      String email, String tipoAnimal,
                                      String tamanoPreferido) {

        Document preferencias = new Document("tipoAnimal", tipoAnimal)
                .append("tamanoPreferido", tamanoPreferido);

        return new Document("_id", dni)
                .append("nombre", nombre)
                .append("apellido1", apellido1)
                .append("apellido2", apellido2)
                .append("telefono", telefono)
                .append("direccion", direccion)
                .append("email", email)
                .append("fechaRegistro", fecha(2026, 1, 10))
                .append("preferenciasAdopcion", preferencias)
                .append("eliminado", false);
    }

    // Crear un documento de adopción
    private static Document adopcion(ObjectId animalId, String dniAdoptante,
                                     String estado, Date fechaSolicitud,
                                     Date fechaResolucion, String observaciones) {

        return new Document("_id", new ObjectId())
                .append("animalId", animalId)
                .append("dniAdoptante", dniAdoptante)
                .append("fechaSolicitud", fechaSolicitud)
                .append("fechaResolucion", fechaResolucion)
                .append("estado", estado)
                .append("observaciones", observaciones)
                .append("seguimiento", List.of())
                .append("eliminado", false);
    }

    // Convertir una fecha en formato año, mes y día a Date
    private static Date fecha(int anio, int mes, int dia) {

        return Date.from(
                LocalDate.of(anio, mes, dia)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}