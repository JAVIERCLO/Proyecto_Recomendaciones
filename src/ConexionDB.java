import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

//Clase de conexión de Java con Neo4J
public class ConexionDB{

    private final Driver driver;

    public ConexionDB(String URL, String USER, String PASSWORD){
        driver = GraphDatabase.driver(URL);
    }
    //Prueba de conexión
    public String prueba(){
        try (Session session = driver.session()){
            return session.run("RETURN 'Prueba de conexión de java con Neo4j' AS mensaje").single().get("mensaje").asString();
        }
    }
    public void Close(){
        driver.close();
    }
}