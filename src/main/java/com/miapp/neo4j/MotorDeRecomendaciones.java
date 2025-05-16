package com.miapp.neo4j;

import java.util.List;

//Interfaz que funciona como motor de recomendaciones para esta y otras apps
public interface MotorDeRecomendaciones {
    
//Métodos para usuario
void crearUsuario(Usuario usuario);
boolean iniciarSesion(String nombre, String password);
Usuario obtenerNombreUsuario(String nombre);
boolean eliminarUsuario(String nombre);
boolean actualizarContrasena(String nombre, String nuevaPassword);
boolean cambiarPresupuesto(String nombre, double nuevoPresupuesto);

//Métodos para carro
void crearCarro(Carro carro);
Carro obtenerModelo(String modelo);

//Relaciones entre usuario y carro
void agregarCarroFavorito(String nombre, String modelo);
void eliminarRecomendacion(String nombre);
void recomendar(String nombre, String modelo);
boolean eliminarCarroFavorito(String nombre, String modelo);
List<Carro>obtenerCarrosFavoritos(String nombre);
List<Carro>obtenerCarrosRecomendados(String nombre);

}
