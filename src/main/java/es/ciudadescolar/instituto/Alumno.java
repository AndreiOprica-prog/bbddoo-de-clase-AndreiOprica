package es.ciudadescolar.instituto;

public class Alumno 
{
    private String nombre;

    private String expediente;

    private Integer edad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExpediente() {
        return expediente;
    }

    public void setExpediente(String expediente) {
        this.expediente = expediente;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    @Override
    public String toString() 
    {
        return "Alumno [nombre=" + nombre + ", expediente=" + expediente + ", edad=" + edad + "]";
    }

    public Alumno()
    {
        
    }
    public Alumno(String nombre, String expediente, Integer edad)
    {
        this.setEdad(edad);
        this.setExpediente(expediente);
        this.setNombre(nombre);
    }

}

