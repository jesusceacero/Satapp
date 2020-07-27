package com.groupfive.satapp.models.inventariable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventariable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("codigo")
    @Expose
    public String codigo;
    @SerializedName("tipo")
    @Expose
    public String tipo;
    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("ubicacion")
    @Expose
    public String ubicacion;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;
    @SerializedName("imagen")
    @Expose
    public String imagen;
}
