package com.groupfive.satapp.models.tickets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTicketBody {
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
}
