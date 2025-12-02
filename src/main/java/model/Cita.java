package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Cita {
    private int idCita;
    private int idMascota;
    private int idVeterinario;
    private int idSucursal;
    private Date fechaCita;
    private Time horaCita;
    private String estado; // Pendiente, Confirmada, Completada, Cancelada
    private String motivo;
    private String observaciones;
    private Timestamp fechaCreacion;
    private Timestamp fechaModificacion;

    // Campos auxiliares para mostrar en la UI
    private String nombreMascota;
    private String especieMascota;
    private String nombreCliente;
    private String correoCliente;
    private int idCliente;
    private String nombreVeterinario;
    private String nombreSucursal;

    // ========================================================================
    // CONSTRUCTORES
    // ========================================================================
    public Cita() {}

    public Cita(int idMascota, int idVeterinario, int idSucursal, Date fechaCita, Time horaCita, String motivo) {
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.idSucursal = idSucursal;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivo = motivo;
        this.estado = "Pendiente";
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

    public Date getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(Date fechaCita) {
        this.fechaCita = fechaCita;
    }

    public Time getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(Time horaCita) {
        this.horaCita = horaCita;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
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

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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
    public String getEstadoBadge() {
        switch (estado) {
            case "Pendiente":
                return "<span class='badge badge-warning'>⏳ Pendiente</span>";
            case "Confirmada":
                return "<span class='badge badge-info'>✓ Confirmada</span>";
            case "Completada":
                return "<span class='badge badge-success'>✓ Completada</span>";
            case "Cancelada":
                return "<span class='badge badge-danger'>✗ Cancelada</span>";
            default:
                return "<span class='badge'>" + estado + "</span>";
        }
    }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita=" + idCita +
                ", nombreMascota='" + nombreMascota + '\'' +
                ", nombreVeterinario='" + nombreVeterinario + '\'' +
                ", fechaCita=" + fechaCita +
                ", horaCita=" + horaCita +
                ", estado='" + estado + '\'' +
                '}';
    }
}