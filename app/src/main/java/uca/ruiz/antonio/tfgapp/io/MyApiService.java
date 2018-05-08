package uca.ruiz.antonio.tfgapp.io;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import uca.ruiz.antonio.tfgapp.model.Cura;
import uca.ruiz.antonio.tfgapp.model.Paciente;
import uca.ruiz.antonio.tfgapp.model.Proceso;



public interface MyApiService {

    @GET("pacientes")
    Call<ArrayList<Paciente>> getPacientes();

    @GET("procesos")
    Call<ArrayList<Proceso>> getProcesos();

    @GET("procesos/{id}/curas")
    Call<ArrayList<Cura>> getCurasByProcesoId(@Path("id") Long id);

}
