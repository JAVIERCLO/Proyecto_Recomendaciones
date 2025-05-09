import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;
//Clase de conexión de Java con Neo4J
public class ConexionDB{

        String USER="neo4j";
        String PASSWORD="proyectoNEO4J";
        String URL = "bolt://localhost:7687";

    private Driver driver;

    public ConexionDB(String URL, String USER, String PASSWORD){
       this.driver = GraphDatabase.driver(URL, AuthTokens.basic(USER, PASSWORD));
    }
    //Prueba de conexión
    public String prueba(){
        try {

            driver.verifyConnectivity();
            return "Prueba de conexión de java con Neo4j";
        } 
        catch (Exception e){
            return "Error de conexion" +e.getMessage();
        }
    }
    public void Close(){
        driver.close();
    }
}