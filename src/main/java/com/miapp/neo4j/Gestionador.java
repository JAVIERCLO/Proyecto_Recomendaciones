package com.miapp.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import java.util.ArrayList;
import java.util.List;
//Clase de conexión de Java con Neo4J
public class Gestionador implements MotorDeRecomendaciones{

    private final Driver driver;
    //Manejar conexión con NEO4J
    public Gestionador(String URL, String USER, String PASSWORD){
       this.driver = GraphDatabase.driver(URL, AuthTokens.basic(USER, PASSWORD));
    }
    //Método para cerrar la conexión
    public void Close(){
        driver.close();
    }

//Implemetación de métodos de la interfaz de motor de recomendaciones 
    //Métodos para usuario 
    @Override
    public void crearUsuario(Usuario usuario) {
        try (Session session = driver.session()) {
            String query = "CREATE (:Usuario {nombre: $nombre, password: $password, presupuesto: $presupuesto})";
            session.run(query, Values.parameters(
                "nombre", usuario.getNombre(),
                "contrasena", usuario.getPassword(),
                "presupuesto", usuario.getPresupuesto()
            ));
        }
    }
    //Iniciar sesión
    @Override
    public boolean iniciarSesion(String nombre, String password) {
        try (Session session = driver.session()) {
            String query = "MATCH (u:Usuario {nombre: $nombre, contrasena: $contrasena}) RETURN u";
            Result result = session.run(query, Values.parameters("nombre", nombre, "password", password));
            return result.hasNext();
        }
    }

    //Cambiar presupuesto
    @Override
    public boolean cambiarPresupuesto(String nombre, double nuevoPresupuesto) {
        try (Session session = driver.session()) {
            String query = "MATCH (u:Usuario {nombre: $nombre}) SET u.presupuesto = $presupuesto";
            session.run(query, Values.parameters("nombre", nombre, "presupuesto", nuevoPresupuesto));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Cambiar contraseña del usuario
    @Override
    public boolean actualizarContrasena(String nombre, String nuevaPassword) {
        try (Session session = driver.session()) {
            String query = "MATCH (u:Usuario {nombre: $nombre}) SET u.password = $password";
            session.run(query, Values.parameters("nombre", nombre, "password", nuevaPassword));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Obtener usuario por nombre
    @Override
    public Usuario obtenerNombreUsuario(String nombre) {
        String query = "MATCH (u:Usuario {nombre: $nombre}) RETURN u LIMIT 1";
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            if (result.hasNext()) {
                Node node = result.next().get("u").asNode();
                String password = node.get("password").asString();
                double presupuesto = node.get("presupuesto").asDouble(0.0);
                return new Usuario(nombre, password, presupuesto, edad);
            }
            return null;
        }
    }

    //Métodos para carro
    // Crear carro
    @Override
    public void crearCarro(Carro carro) {
        String query = "CREATE (:Carro {marca: $marca, modelo: $modelo, anio: $anio, precio: $precio, transmision: $transmision, tipoVehiculo: $tipoVehiculo, combustible: $combustible})";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters(
                    "marca", carro.getMarca(),
                    "modelo", carro.getModelo(),
                    "anio", carro.getYear(),
                    "precio", carro.getPrecio(),
                    "transmision", carro.getTransmision(),
                    "tipoVehiculo", carro.getTipo(),
                    "combustible", carro.getCombustible()
            ));
        }
    }
    // Obtener carro por modelo
    public Carro obtenerCarroPorModelo(String modeloCarro) {
        String query = "MATCH (c:Carro {modelo: $modelo}) RETURN c LIMIT 1";
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("modelo", modeloCarro));
            if (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                return new Carro(
                        node.get("marca").asString(),
                        modeloCarro,
                        node.get("year").asInt(),
                        node.get("precio").asDouble(),
                        node.get("transmision").asString(),
                        node.get("tipo").asString(),
                        node.get("combustible").asString()
                );
            }
            return null;
        }
    }

    // Agregar carro favorito a usuario
    @Override
    public void agregarCarroFavorito(String nombreUsuario, String modeloCarro) {
        String query = "MATCH (u:Usuario {nombre: $usuario}), (c:Carro {modelo: $modelo}) MERGE (u)-[:FAVORITO]->(c)";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("usuario", nombre, "modelo", modelo));
        }
    }

    // Obtener carros favoritos de usuario
    public List<Carro> obtenerCarrosFavoritosDeUsuario(String nombreUsuario) {
        List<Carro> favoritos = new ArrayList<>();
        String query = "MATCH (u:Usuario {nombre: $usuario})-[:FAVORITO]->(c:Carro) RETURN c";
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("usuario", nombreUsuario));
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                favoritos.add(new Carro(
                        node.get("marca").asString(),
                        node.get("modelo").asString(),
                        node.get("year").asInt(),
                        node.get("precio").asDouble(),
                        node.get("transmision").asString(),
                        node.get("tipo").asString(),
                        node.get("combustible").asString()
                ));
            }
        }
        return favoritos;
    }
}