package com.miapp.neo4j;

public class Main {  
    public static void main(String[] args) {
        
        String USER="neo4j";
         String PASSWORD="proyectoNEO4J";
         String URL = "bolt://localhost:7474";
        Gestionador conexion = new Gestionador(URL, USER, PASSWORD);



    }
}
