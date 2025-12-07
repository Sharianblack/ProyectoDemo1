package model;

import java.sql.Date;

public class Mascota {
    private int idMascota;
    private int idUsuarioPropietario; // 🔥 CAMBIO: Ahora apunta directo a usuarios
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private Date fechaNacimiento;

    // Campos auxiliares
    private String nombrePropietario;
    private String correoPropietario;
    private int edad; // Calculada

    public Mascota() {}

    public Mascota(int idUsuarioPropietario, String nombre, String especie, String raza, String sexo, Date fechaNacimiento) {
        this.idUsuarioPropietario = idUsuarioPropietario;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
    }

    // GETTERS Y SETTERS
    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdUsuarioPropietario() {
        return idUsuarioPropietario;
    }

    public void setIdUsuarioPropietario(int idUsuarioPropietario) {
        this.idUsuarioPropietario = idUsuarioPropietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getCorreoPropietario() {
        return correoPropietario;
    }

    public void setCorreoPropietario(String correoPropietario) {
        this.correoPropietario = correoPropietario;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    // MÃTODOS AUXILIARES
    public String getSexoCompleto() {
        if (sexo == null) return "No especificado";
        return sexo.equalsIgnoreCase("M") ? "Macho" : "Hembra";
    }

    public String getIconoEspecie() {
        if (especie == null) return "🐾";
        String especieLower = especie.toLowerCase();
        if (especieLower.contains("perro")) return "🐕";
        if (especieLower.contains("gato")) return "🐈";
        if (especieLower.contains("ave") || especieLower.contains("pájaro")) return "🐦";
        if (especieLower.contains("conejo")) return "🐰";
        if (especieLower.contains("hamster")) return "🐹";
        return "🐾";
    }

    @Override
    public String toString() {
        return "Mascota{" +
                "idMascota=" + idMascota +
                ", nombre='" + nombre + '\'' +
                ", especie='" + especie + '\'' +
                ", propietario='" + nombrePropietario + '\'' +
                '}';
    }
}