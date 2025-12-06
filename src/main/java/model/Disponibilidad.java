package model;

import java.sql.Time;
import java.sql.Timestamp;

public class Disponibilidad {
    private int idDisponibilidad;
    private int idVeterinario;
    private int idSucursal;
    private String diaSemana;
    private Time horaInicio;
    private Time horaFin;
    private boolean activo;
    private Timestamp fechaCreacion;

    // Campos auxiliares
    private String nombreVeterinario;
    private String nombreSucursal;

    // ========================================================================
    // CONSTRUCTORES
    // ========================================================================
    public Disponibilidad() {}

    public Disponibilidad(int idVeterinario, int idSucursal, String diaSemana, Time horaInicio, Time horaFin) {
        this.idVeterinario = idVeterinario;
        this.idSucursal = idSucursal;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = true;
    }

    // ========================================================================
    // GETTERS Y SETTERS
    // ========================================================================
    public int getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public void setIdDisponibilidad(int idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
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

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    @Override
    public String toString() {
        return "Disponibilidad{" +
                "idDisponibilidad=" + idDisponibilidad +
                ", diaSemana='" + diaSemana + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", activo=" + activo +
                '}';
    }
}