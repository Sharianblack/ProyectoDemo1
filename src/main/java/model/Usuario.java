package model;

import java.sql.Timestamp;

public class Usuario {

    // CAMPOS QUE COINCIDEN CON LA TABLA USUARIOS
    private int id;
    private String nombre;
    private String correo;
    private String passwordHash;
    private String rol;
    private String telefono;
    private String direccion;
    private boolean activo; // NUEVO CAMPO para activar/desactivar usuarios
    private Timestamp fechaRegistro; // NUEVO CAMPO para fecha de creación

    // ----------------------
    // 1. CONSTRUCTORES
    // ----------------------

    // Constructor vacío
    public Usuario() {
        this.activo = true; // Por defecto, los usuarios están activos
    }

    // Constructor para Login (username y password)
    public Usuario(String correo, String passwordHash) {
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.activo = true;
    }

    // Constructor completo (para Registro)
    public Usuario(String nombre, String correo, String passwordHash, String rol, String telefono, String direccion) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = true;
    }

    // Constructor para mapeo desde la BD (incluye id y activo)
    public Usuario(int id, String nombre, String correo, String rol, String telefono, String direccion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Constructor completo con todos los campos
    public Usuario(int id, String nombre, String correo, String passwordHash, String rol,
                   String telefono, String direccion, boolean activo, Timestamp fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }

    // ----------------------
    // 2. GETTERS Y SETTERS
    // ----------------------

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // NUEVO: Getter y Setter para activo
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // NUEVO: Getter y Setter para fechaRegistro
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // ----------------------
    // 3. MÉTODOS AUXILIARES
    // ----------------------

    /**
     * Devuelve el estado como texto para mostrar en la UI
     */
    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }

    /**
     * Devuelve un badge HTML con el estado
     */
    public String getEstadoBadge() {
        if (activo) {
            return "<span class='badge badge-success'>Activo</span>";
        } else {
            return "<span class='badge badge-danger'>Inactivo</span>";
        }
    }

    // ----------------------
    // 4. TOSTRING
    // ----------------------

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}