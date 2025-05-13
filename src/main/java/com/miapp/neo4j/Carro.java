public class Carro {
    
    private String marca;
    private double precio;
    private String transmision; // automatico o mecanico
    private String tipo; //sedzn, pick up, SUV, hatchback, deportivo, etc.
    private String combustible; //Gasolina, hibrido, eléctrico
    private String modelo; //Por ejemplo: Porsche 911 S
    private int year; //Año del vehículo

    public Carro(String marca, String modelo, int year, double precio, String transmision, String tipo, String combustible){
        if(!transmision.equalsIgnoreCase("automatico") && !transmision.equalsIgnoreCase("mecanico") && !transmision.equalsIgnoreCase("automático") && !transmision.equalsIgnoreCase("mecánico")){
            throw new IllegalArgumentException("Transmisión debe ser automatico o mecanico");
        }

        this.marca = marca;
        this.precio = precio;
        this.transmision = transmision;
        this.tipo = tipo;
        this.combustible = combustible;
        this.modelo = modelo;
        this.year = year;
    }

    public String getMarca() { 
        return marca; 
    }
    public String getModelo() { 
        return modelo; 
    }
    public int getYear() {
         return year; 
    }
    public double getPrecio() {
         return precio; 
    }
    public String getTransmision() { 
        return transmision; 
    }
    public String getTipo() { 
        return tipo; 
    }
    public String getCombustible() {
         return combustible; 
        }

    @Override
    public String toString() {
        return year + " " + marca + " " + modelo + " (" + transmision + ", " + tipo + ", " + combustible + ") - Q" + precio;
    }
}
