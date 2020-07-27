package com.groupfive.satapp.models.annotations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketAnnotation {
    @SerializedName("id")
    @Expose
    public String id;
}
