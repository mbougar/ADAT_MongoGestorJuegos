package repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.github.cdimascio.dotenv.dotenv
import model.Juego
import org.bson.Document
import java.util.Date

class RepositorioJuegos {

    fun getJuegos(): List<Juego> {

        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"
        val juegos = mutableListOf<Juego>()

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")
            val resultsFlow = coll.find()

            resultsFlow.forEach { document ->
                val titulo = document.getString("titulo")
                val genero = document.getString("genero")
                val precio = document.getDouble("precio") ?: 0.0
                val fechaLanzamiento = document.get("fecha_lanzamiento") as? Date ?: Date()

                juegos.add(
                    Juego(
                    titulo = titulo,
                    genero = genero,
                    precio = precio,
                    fechaLanzamiento = fechaLanzamiento
                    )
                )
            }

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }

        return juegos
    }

    fun getGeneros(): List<String> {
        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"
        val generos = mutableListOf<String>()

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")
            val resultsFlow = coll.find()

            resultsFlow.forEach { document ->
                val genero = document.getString("genero")

                if (genero !in generos) {
                    generos.add(genero)
                }
            }

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }

        return generos
    }

    fun getJuego(titulo: String): Juego? {

        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")
            val filter = Filters.eq("titulo", titulo)

            val result = coll.find(filter).first()

            result?.let {
                return Juego(
                    titulo = it["titulo"].toString(),
                    genero = it["genero"].toString(),
                    precio = it["precio"].toString().toDouble(),
                    fechaLanzamiento = it["fecha_lanzamiento"] as Date
                )
            }


        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }

        return null
    }

    fun insertJuego(titulo: String, genero: String, precio: Double, fechaLanzamiento: Date) {
        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"

        try {
            val database = mongoClient.getDatabase(databaseName)

            val coll = database.getCollection("juegos")

            val juego = getJuego(titulo)

            if (juego == null) {
                val nuevoDocumento: Document = Document()
                    .append("titulo", titulo)
                    .append("genero", genero)
                    .append("precio", precio)
                    .append("fecha_lanzamiento", fechaLanzamiento)

                coll.insertOne(nuevoDocumento)
            }

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }
    }

    fun updateJuego(titulo: String, nuevoTitulo: String, nuevoGenero: String, nuevoPrecio: Double, nuevaFechaLanzamiento: Date) {

        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")

            val filter = Filters.eq("titulo", titulo)

            val update = Updates.combine(
                Updates.set("titulo", nuevoTitulo),
                Updates.set("genero", nuevoGenero),
                Updates.set("precio", nuevoPrecio),
                Updates.set("fecha_lanzamiento", nuevaFechaLanzamiento)
            )

            val result = coll.updateOne(filter, update)

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }
    }

    fun deleteJuegoPorTitulo(titulo: String) {

        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")
            val filter = Filters.eq("titulo", titulo)

            coll.deleteOne(filter)

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }

    }

    fun deleteJuegosPorGenero(genero: String) {

        val dotenv = dotenv()
        val connectString = dotenv["URL_MONGODB"]

        val mongoClient: MongoClient = MongoClients.create(connectString)
        val databaseName = "mbougar"

        try {
            val database = mongoClient.getDatabase(databaseName)
            val coll = database.getCollection("juegos")
            val filter = Filters.eq("genero", genero)

            coll.deleteMany(filter)

        } catch (e: Exception) {
            println("Error al conectar a MongoDB: ${e.message}")
        } finally {
            mongoClient.close()
        }

    }
}