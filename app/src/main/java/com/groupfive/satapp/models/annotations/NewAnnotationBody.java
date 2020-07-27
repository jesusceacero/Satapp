package com.groupfive.satapp.models.annotations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAnnotationBody {
    @SerializedName("id_ticket")
    @Expose
    public String id_ticket;
    @SerializedName("cuerpo")
    @Expose
    public String cuerpo;
}
