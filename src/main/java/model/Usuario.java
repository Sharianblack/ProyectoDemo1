package model;

public class Usuario {

    // CAMPOS QUE COINCIDEN CON LA TABLA USUARIOS
    private int id; // Se mantendrá 'id' en Java aunque en BD sea 'id_usuario'
    private String nombre;
    private String correo; // Coincide con la columna 'Correo' (se usa como username/email)
    private String passwordHash; // Coincide con la columna 'PasswordHash'
    private String rol;
    private String telefono;
    private String direccion;


    // ----------------------
    // 1. CONSTRUCTORES
    // ----------------------

    // Constructor vacío
    public Usuario() {
    }

    // Constructor para Login (username y password)
    public Usuario(String correo, String passwordHash) {
        this.correo = correo;
        this.passwordHash = passwordHash;
    }

    // Constructor completo (para Registro)
    public Usuario(String nombre, String correo, String passwordHash, String rol, String telefono, String direccion) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Constructor para mapeo desde la BD (incluye id)
    public Usuario(int id, String nombre, String correo, String rol, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
    }


    // ----------------------
    // 2. GETTERS Y SETTERS
    // ----------------------

    // ID (BD: id_usuario)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Nombre (BD: Nombre)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Correo (BD: Correo)
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // Password (BD: PasswordHash)
    // Se usa 'passwordHash' en Java para reflejar que es el hash y no el texto plano.
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Rol (BD: Rol)
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // Teléfono (BD: Telefono)
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Dirección (BD: Direccion)
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // ----------------------
    // 3. TOSTRING
    // ----------------------

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}