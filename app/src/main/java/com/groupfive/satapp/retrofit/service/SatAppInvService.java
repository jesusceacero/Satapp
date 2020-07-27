package com.groupfive.satapp.retrofit.service;

import com.groupfive.satapp.models.inventariable.EditInventariable;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.models.tickets.TicketModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SatAppInvService {

    @GET("/inventariable")
    Call<List<Inventariable>> getInventariables();

    @GET("/inventariable/{id}")
    Call<Inventariable> getInventariable(@Path("id") String id);

    @PUT("/inventariable/{id}")
    Call<Inventariable> putInventariable(@Path("id") String id, @Body EditInventariable editInventariable);

    @Multipart
    @PUT("/inventariable/{id}/img")
    Call<ResponseBody> putInventariableImg(@Path("id") String id,
                                            @Part MultipartBody.Part imagen);

    @GET("/inventariable/ubicaciones")
    Call<List<String>> getLocations();

    @DELETE("/inventariable/{id}")
    Call<Void> deleteInventariable(@Path("id") String id);

    @GET("/inventariable/img/{img_url}")
    Call<ResponseBody> getInventariableImage(@Path("img_url") String img_url);

    @GET("/ticket/inventariable/{id}")
    Call<List<TicketModel>> getTicketsFromInventariable(@Path("id") String id);

    @DELETE("/inventariable/{id}/img")
    Call<Void> deleteInventariableImage(@Path("id") String id);

    @Multipart
    @POST("/inventariable")
    Call<Inventariable> addInventariable(@Part MultipartBody.Part imagen,
                                         @Part("tipo")RequestBody tipo,
                                         @Part("nombre")RequestBody nombre,
                                         @Part("descripcion")RequestBody descripcion,
                                         @Part("ubicacion")RequestBody ubicacion);
}
