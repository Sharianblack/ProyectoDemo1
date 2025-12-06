package model;

import java.sql.Timestamp;

/**
 * Modelo de Cita
 * Representa una cita veterinaria con toda su información relacionada
 */
public class Cita {

    // Campos de la tabla citas
    private int idCita;
    private int idMascota;
    private int idVeterinario;
    private int idSucursal;
    private Timestamp fechaCita;
    private String estado; // Programada, Completada, Cancelada, En Proceso
    private String observaciones;

    // Información relacionada (para mostrar en la vista)
    private String nombreMascota;
    private String especieMascota;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String nombreVeterinario;
    private String nombreSucursal;

    // ========================================================================
    // CONSTRUCTORES
    // ========================================================================

    public Cita() {
    }

    // Constructor básico para crear cita
    public Cita(int idMascota, int idVeterinario, int idSucursal, Timestamp fechaCita, String estado) {
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.idSucursal = idSucursal;
        this.fechaCita = fechaCita;
        this.estado = estado;
    }

    // Constructor completo con información relacionada
    public Cita(int idCita, int idMascota, int idVeterinario, int idSucursal,
                Timestamp fechaCita, String estado, String observaciones,
                String nombreMascota, String especieMascota, String nombreCliente,
                String correoCliente, String telefonoCliente) {
        this.idCita = idCita;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.idSucursal = idSucursal;
        this.fechaCita = fechaCita;
        this.estado = estado;
        this.observaciones = observaciones;
        this.nombreMascota = nombreMascota;
        this.especieMascota = especieMascota;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;
    }

    // ========================================================================
    // GETTERS Y SETTERS
    // ========================================================================

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Timestamp getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(Timestamp fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getEspecieMascota() {
        return especieMascota;
    }

    public void setEspecieMascota(String especieMascota) {
        this.especieMascota = especieMascota;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Obtiene la fecha formateada en español
     */
    public String getFechaFormateada() {
        if (fechaCita == null) return "";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(fechaCita);
    }

    /**
     * Obtiene solo la fecha sin hora
     */
    public String getFechaSola() {
        if (fechaCita == null) return "";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fechaCita);
    }

    /**
     * Obtiene solo la hora
     */
    public String getHora() {
        if (fechaCita == null) return "";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        return sdf.format(fechaCita);
    }

    /**
     * Obtiene el badge HTML con el color según el estado
     */
    public String getEstadoBadge() {
        if (estado == null) return "";

        String estadoLower = estado.toLowerCase();
        String color = "";
        String emoji = "";

        switch (estadoLower) {
            case "programada":
                color = "#0056b3";
                emoji = "📅";
                break;
            case "en proceso":
                color = "#ffc107";
                emoji = "⏳";
                break;
            case "completada":
                color = "#28a745";
                emoji = "✓";
                break;
            case "cancelada":
                color = "#dc3545";
                emoji = "✗";
                break;
            default:
                color = "#6c757d";
                emoji = "•";
                break;
        }

        return "<span style='background-color: " + color + "; color: white; padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.85rem; font-weight: 600;'>" +
                emoji + " " + estado + "</span>";
    }

    /**
     * Verifica si la cita es hoy
     */
    public boolean esHoy() {
        if (fechaCita == null) return false;

        java.util.Calendar calCita = java.util.Calendar.getInstance();
        calCita.setTime(fechaCita);

        java.util.Calendar calHoy = java.util.Calendar.getInstance();

        return calCita.get(java.util.Calendar.YEAR) == calHoy.get(java.util.Calendar.YEAR) &&
                calCita.get(java.util.Calendar.DAY_OF_YEAR) == calHoy.get(java.util.Calendar.DAY_OF_YEAR);
    }

    /**
     * Verifica si la cita ya pasó
     */
    public boolean yaPaso() {
        if (fechaCita == null) return false;
        return fechaCita.before(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita=" + idCita +
                ", nombreMascota='" + nombreMascota + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", fechaCita=" + fechaCita +
                ", estado='" + estado + '\'' +
                '}';
    }
}