package org.egibide.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Adopcion {

    private ObjectId id;
    private ObjectId animalId;
    private String dniAdoptante;
    private Date fechaSolicitud;
    private Date fechaResolucion;
    private String estado;
    private String observaciones;
    private List<Document> seguimiento;
    private Boolean eliminado = false;

    public Adopcion() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getAnimalId() {
        return animalId;
    }

    public void setAnimalId(ObjectId animalId) {
        this.animalId = animalId;
    }

    public String getDniAdoptante() {
        return dniAdoptante;
    }

    public void setDniAdoptante(String dniAdoptante) {
        this.dniAdoptante = dniAdoptante;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
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

    public List<Document> getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(List<Document> seguimiento) {
        this.seguimiento = seguimiento;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}