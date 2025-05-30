package com.miapp.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
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
                return new Usuario(nombre, password, presupuesto);
            }
            return null;
        }
    }

    //Eliminar usuario
    @Override
    public boolean eliminarUsuario(String nombre) {
        String query = "MATCH (u:Usuario {nombre: $nombre}) DETACH DELETE u";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Métodos para carro
    // Crear carro
    @Override
    public void crearCarro(Carro carro) {
        String query = "CREATE (:Carro {marca: $marca, modelo: $modelo, year: $year, precio: $precio, transmision: $transmision, tipo: $tipo, combustible: $combustible})";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters(
                    "marca", carro.getMarca(),
                    "modelo", carro.getModelo(),
                    "year", carro.getYear(),
                    "precio", carro.getPrecio(),
                    "transmision", carro.getTransmision(),
                    "tipo", carro.getTipo(),
                    "combustible", carro.getCombustible()
            ));
        }
    }
    // Obtener carro por modelo
    public Carro obtenerCarroPorModelo(String modelo) {
        String query = "MATCH (c:Carro {modelo: $modelo}) RETURN c LIMIT 1";
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("modelo", modelo));
            if (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                return new Carro(
                        node.get("marca").asString(),
                        modelo,
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
    public void agregarCarroFavorito(String nombre, String modelo) {
        String query = "MATCH (u:Usuario {nombre: $usuario}), (c:Carro {modelo: $modelo}) MERGE (u)-[:FAVORITO]->(c)";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("usuario", nombre, "modelo", modelo));
        }
    }

    // Obtener carros favoritos de usuario
    @Override
    public List<Carro> obtenerCarrosFavoritos(String nombreUsuario) {
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
    //Eliminar un carro favorito del usuario
    @Override
    public boolean eliminarCarroFavorito(String nombre, String modelo) {
        String query = "MATCH (u:Usuario {nombre: $nombre})-[r:FAVORITO]->(c:Carro {modelo: $modelo}) DELETE r";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre, "modelo", modelo));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //Preferencias
    //Agregar preferencia
    @Override
    public void agregarPreferencia(String nombre, String tipoPreferencia, String valorPreferencia) {
        String tipoNodo;
        String relacion;

        switch (tipoPreferencia.toLowerCase()) {
            case "marca":
                tipoNodo = "Marca";
                relacion = "PREFIERE_MARCA";
                break;
            case "transmision":
                tipoNodo = "Transmision";
                relacion = "PREFIERE_TRANSMISION";
                break;
            case "tipo":
                tipoNodo = "TipoVehiculo";
                relacion = "PREFIERE_TIPO";
                break;
            case "combustible":
                tipoNodo = "Combustible";
                relacion = "PREFIERE_COMBUSTIBLE";
                break;
            case "modelo":
                tipoNodo = "Modelo";
                relacion = "PREFIERE_MODELO";
                break;
            case "year":
                tipoNodo = "Year";
                relacion = "PREFIERE_YEAR";
                break;
            default:
                throw new IllegalArgumentException("Tipo de preferencia no soportado: " + tipoPreferencia);
        }

        String query = String.format("""
            MATCH (u:Usuario {nombre: $nombre})
            MERGE (p:%s {valor: $valor})
            MERGE (u)-[:%s]->(p)
        """, tipoNodo, relacion);

        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre, "valor", valorPreferencia));
        }
    }
    //Eliminar preferencia
    @Override
    public boolean eliminarPreferencia(String nombre, String tipoPreferencia, String valorPreferencia) {
        String tipoNodo;
        String relacion;

        switch (tipoPreferencia.toLowerCase()) {
            case "marca":
                tipoNodo = "Marca";
                relacion = "PREFIERE_MARCA";
                break;
            case "transmision":
                tipoNodo = "Transmision";
                relacion = "PREFIERE_TRANSMISION";
                break;
            case "tipo":
                tipoNodo = "TipoVehiculo";
                relacion = "PREFIERE_TIPO";
                break;
            case "combustible":
                tipoNodo = "Combustible";
                relacion = "PREFIERE_COMBUSTIBLE";
                break;
            case "modelo":
                tipoNodo = "Modelo";
                relacion = "PREFIERE_MODELO";
                break;
            case "year":
                tipoNodo = "Year";
                relacion = "PREFIERE_YEAR";
                break;
            default:
                return false;
        }

        String query = String.format("""
            MATCH (u:Usuario {nombre: $nombre})-[r:%s]->(p:%s {valor: $valor})
            DELETE r
        """, relacion, tipoNodo);

        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre, "valor", valorPreferencia));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //btener preferencias
    @Override
    public List<String> obtenerPreferencias(String nombre, String tipoPreferencia) {
        String tipoNodo;
        String relacion;

        switch (tipoPreferencia.toLowerCase()) {
            case "marca":
                tipoNodo = "Marca";
                relacion = "PREFIERE_MARCA";
                break;
            case "transmision":
                tipoNodo = "Transmision";
                relacion = "PREFIERE_TRANSMISION";
                break;
            case "tipo":
                tipoNodo = "TipoVehiculo";
                relacion = "PREFIERE_TIPO";
                break;
            case "combustible":
                tipoNodo = "Combustible";
                relacion = "PREFIERE_COMBUSTIBLE";
                break;
            case "modelo":
                tipoNodo = "Modelo";
                relacion = "PREFIERE_MODELO";
                break;
            case "year":
                tipoNodo = "Year";
                relacion = "PREFIERE_YEAR";
                break;
            default:
                return new ArrayList<>();
        }

        String query = String.format("""
            MATCH (u:Usuario {nombre: $nombre})-[:%s]->(p:%s)
            RETURN p.valor AS valor
        """, relacion, tipoNodo);

        List<String> preferencias = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            while (result.hasNext()) {
                preferencias.add(result.next().get("valor").asString());
            }
        }
        return preferencias;
    }

    @Override
    public List<Carro> filtrarCarrosPorPreferencias(String nombre) {
        String query = """
            MATCH (u:Usuario {nombre: $nombre})-[:PREFIERE_MARCA]->(m:Marca),
                  (u)-[:PREFIERE_TRANSMISION]->(t:Transmision),
                  (u)-[:PREFIERE_TIPO]->(tp:TipoVehiculo),
                  (u)-[:PREFIERE_COMBUSTIBLE]->(co:Combustible),
                  (u)-[:PREFIERE_MODELO]->(mo:Modelo),
                  (u)-[:PREFIERE_YEAR]->(a:Year),
                  (c:Carro)
            WHERE c.marca = m.valor
              AND c.transmision = t.valor
              AND c.tipo = tp.valor
              AND c.combustible = co.valor
              AND c.modelo = mo.valor
              AND toString(c.year) = a.valor
            RETURN c
        """;

        List<Carro> carros = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                carros.add(new Carro(
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
        return carros;
    }

    @Override
    public List<Carro> filtrarCarrosPorPresupuesto(String nombre) {
        String query = """
            MATCH (u:Usuario {nombre: $nombre}), (c:Carro)
            WHERE c.precio <= u.presupuesto
            RETURN c
        """;

        List<Carro> carros = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                carros.add(new Carro(
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
        return carros;
    }

    @Override
    public List<Carro> generarRecomendaciones(String nombre) {
        String query = """
            MATCH (u:Usuario {nombre: $nombre})-[:FAVORITO]->(f:Carro)
            MATCH (c:Carro)
            WHERE f.tipo = c.tipo
              AND f.transmision = c.transmision
              AND NOT (u)-[:FAVORITO]->(c)
            RETURN DISTINCT c LIMIT 5
        """;

        List<Carro> recomendaciones = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                recomendaciones.add(new Carro(
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
        return recomendaciones;
    }
    @Override
    public void agregarRecomendacion(String nombre, String modelo) {
        String query = "MATCH (u:Usuario {nombre: $nombre}), (c:Carro {modelo: $modelo}) MERGE (u)-[:RECOMENDADO]->(c)";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre, "modelo", modelo));
        }
    }

    @Override
    public List<Carro> obtenerRecomendacionesGuardadas(String nombre) {
        List<Carro> recomendados = new ArrayList<>();
        String query = "MATCH (u:Usuario {nombre: $nombre})-[:RECOMENDADO]->(c:Carro) RETURN c";
        try (Session session = driver.session()) {
            Result result = session.run(query, Values.parameters("nombre", nombre));
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                recomendados.add(new Carro(
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
        return recomendados;
    }
    @Override
    public void limpiarRecomendacionesDeUsuario(String nombre) {
        String query = "MATCH (u:Usuario {nombre: $nombre})-[r:RECOMENDADO]->() DELETE r";
        try (Session session = driver.session()) {
            session.run(query, Values.parameters("nombre", nombre));
        }
    }
}

