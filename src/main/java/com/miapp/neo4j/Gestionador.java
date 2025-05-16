package com.miapp.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
//Clase de conexión de Java con Neo4J
public class Gestionador implements MotorDeRecomendaciones{

    private final Driver driver;
    //Manejar conexión con NEO4J
    public Gestionador(String URL, String USER, String PASSWORD){
       this.driver = GraphDatabase.driver(URL, AuthTokens.basic(USER, PASSWORD));
    }

//Implemetación de métodos de la interfaz de motor de recomendaciones 
    //Métodos para usuario 
    @Override
    public void crearUsuario(Usuario usuario) {
        try (Session session = driver.session()) {
            String query = "CREATE (:Usuario {nombre: $nombre, contrasena: $contrasena, presupuesto: $presupuesto})";
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
            Result result = session.run(query, Values.parameters("nombre", nombre, "contrasena", password));
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
            String query = "MATCH (u:Usuario {nombre: $nombre}) SET u.contrasena = $contrasena";
            session.run(query, Values.parameters("nombre", nombre, "contrasena", nuevaPassword));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}