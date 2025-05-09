import javax.swing.*;

public class Main {  
    public static void main(String[] args) {
        
        /*String USER="neo4j";
         String PASSWORD="proyectoNEO4J";
         String URL = "bolt://localhost:7474";
        ConexionDB conexion = new ConexionDB(URL, USER, PASSWORD);
        String mensaje = conexion.prueba();
        System.out.println(mensaje);
    conexion.Close();
    */
        JFrame frame = new JFrame();
        JButton boton = new JButton("boton");

        // Estableciendo posición y tamaño del botón
        boton.setBounds(130, 100, 100, 40);  

        // Añadiendo el botón al marco
        frame.add(boton);  

        // Configuración del JFrame
        frame.setSize(400, 500);  
        frame.setLayout(null);  
        frame.setVisible(true);  
    }
}
