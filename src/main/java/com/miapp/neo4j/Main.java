import javax.swing.*;

public class Main {  
    public static void main(String[] args) {
        
        String USER="neo4j";
         String PASSWORD="proyectoNEO4J";
         String URL = "bolt://localhost:7474";
        ConexionDB conexion = new ConexionDB(URL, USER, PASSWORD);
        String mensaje = conexion.prueba();
        System.out.println(mensaje);
    conexion.Close();


    }
}
