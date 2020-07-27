package com.groupfive.satapp.models.inventariable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditInventariable {
    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("ubicacion")
    @Expose
    public String ubicacion;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
}
