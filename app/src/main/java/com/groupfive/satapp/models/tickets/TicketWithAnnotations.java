package com.groupfive.satapp.models.tickets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketWithAnnotations {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("creado_por")
    @Expose
    public TicketCreadoPor creadoPor;
    @SerializedName("fecha_creacion")
    @Expose
    public String fechaCreacion;
    @SerializedName("estado")
    @Expose
    public String estado;
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("anotaciones")
    @Expose
    public List<TicketAnotaciones> anotaciones = null;
    @SerializedName("asignaciones")
    @Expose
    public List<TicketAsignaciones> asignaciones = null;
    @SerializedName("fotos")
    @Expose
    public List<String> fotos = null;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;
}
