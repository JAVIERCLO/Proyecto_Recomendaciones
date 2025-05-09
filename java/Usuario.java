import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nombre;
    private String password;
    private double presupuesto;
    private List<Carro> carrosFavoritos;
    private List<String> preferencias; //las carácterísticas que le gustan al usuario, como transmisión mecánica, SUV, etc.

    public Usuario(String nombre, String password, double presupuesto){
        this.nombre = nombre;
        this.password = password;
        this.presupuesto = presupuesto;
        this.carrosFavoritos = new ArrayList<>();
        this.preferencias = new ArrayList<>();
    }
    
    public String getNombre() { 
        return nombre; 
    }
    public boolean verificarContrasena(String password) {
        return this.password.equals(password);
    }

    public void agregarCarroFavorito(Carro carro) {
        carrosFavoritos.add(carro);
    }

    public void agregarPreferencia(String preferencia) {
        preferencias.add(preferencia.toLowerCase());
    }

    public List<Carro> getCarrosFavoritos() {
        return carrosFavoritos;
    }

    public List<String> getPreferencias() {
        return preferencias;
    }
    public double getPresupuesto(){
        return presupuesto;
    }

    @Override
    public String toString() {
        return "Usuario: " + nombre + "\nPreferencias: " + preferencias + "\nCarros favoritos:\n" + carrosFavoritos;
    }
    
}
