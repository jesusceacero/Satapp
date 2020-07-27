package com.groupfive.satapp.models.tickets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketAsignaciones {
    @SerializedName("fecha_asignacion")
    @Expose
    public String fechaAsignacion;
    @SerializedName("tecnico_id")
    @Expose
    public String tecnicoId;
    @SerializedName("picture")
    @Expose
    public String picture;
}
