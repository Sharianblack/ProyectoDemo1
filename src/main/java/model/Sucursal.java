package model;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Modelo para representar una Sucursal de la veterinaria
 */
public class Sucursal {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String ciudad;
    private Time horarioApertura;
    private Time horarioCierre;
    private boolean activo;
    private Timestamp fechaRegistro;

    // Constructor vacío
    public Sucursal() {
    }

    // Constructor con parámetros principales
    public Sucursal(String nombre, String direccion, String telefono, String correo, String ciudad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.ciudad = ciudad;
        this.activo = true;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Time getHorarioApertura() {
        return horarioApertura;
    }

    public void setHorarioApertura(Time horarioApertura) {
        this.horarioApertura = horarioApertura;
    }

    public Time getHorarioCierre() {
        return horarioCierre;
    }

    public void setHorarioCierre(Time horarioCierre) {
        this.horarioCierre = horarioCierre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Sucursal{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", activo=" + activo +
                '}';
    }
}
