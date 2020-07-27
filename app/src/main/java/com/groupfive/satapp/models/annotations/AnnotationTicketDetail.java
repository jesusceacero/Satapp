package com.groupfive.satapp.models.annotations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationTicketDetail {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("id_usuario")
    @Expose
    public AnnotationTicketDetailUser idUsuario;
    @SerializedName("fecha")
    @Expose
    public String fecha;
    @SerializedName("cuerpo")
    @Expose
    public String cuerpo;
    @SerializedName("ticket")
    @Expose
    public AnnotationTicketDetailTicket ticket;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;
}
