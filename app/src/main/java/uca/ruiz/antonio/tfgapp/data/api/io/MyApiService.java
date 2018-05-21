package uca.ruiz.antonio.tfgapp.data.api.io;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;



public interface MyApiService {

    @GET("pacientes")
    Call<ArrayList<Paciente>> getPacientes();

    @GET("pacientes/{id}")
    Call<Paciente> getPacienteById(@Path("id") Long id);

    /* PROCESOS */

    @GET("procesos")
    Call<ArrayList<Proceso>> getProcesos();

    @GET("procesos/{id}")
    Call<Proceso> getProcesoById(@Path("id") Long id);

    @GET("procesos/{id}/curas")
    Call<ArrayList<Cura>> getCurasByProcesoId(@Path("id") Long id);

    @Headers("Content-Type: application/json")
    @POST("procesos")
    Call<Proceso> crearProceso(@Body Proceso p);

    @Headers("Content-Type: application/json")
    @PUT("procesos/{id}")
    Call<Proceso> editarProceso(@Path("id") Long id, @Body Proceso p);

    /* CURAS */

    @Headers("Content-Type: application/json")
    @POST("curas")
    Call<Cura> crearCura(@Body Cura c);

    @Headers("Content-Type: application/json")
    @PUT("curas/{id}")
    Call<Cura> editarCura(@Path("id") Long id, @Body Cura c);

}
