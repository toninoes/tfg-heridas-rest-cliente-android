package uca.ruiz.antonio.tfgapp.data.api.io;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Login;
import uca.ruiz.antonio.tfgapp.data.api.mapping.TokenResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.Administrador;
import uca.ruiz.antonio.tfgapp.data.api.model.Sanitario;
import uca.ruiz.antonio.tfgapp.data.api.model.User;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Diagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Procedimiento;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.data.api.model.Sala;
import uca.ruiz.antonio.tfgapp.data.api.model.Valoracion;
import uca.ruiz.antonio.tfgapp.data.api.model.ValoracionesResults;


public interface MyApiService {


    /* ======================= AUTENTICACION Y REGISTRO CON JWT ======================= */
    @Headers("Content-Type: application/json")
    @POST("auth")
    Call<TokenResponse> login(@Body Login login);

    @Headers("Content-Type: application/json")
    @POST("registro")
    Call<UserResponse> registro(@Body User user, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("registro/{centroId}")
    Call<UserResponse> registro(@Path("centroId") Long centroId, @Body User user,
                                @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("registro/{id}")
    Call<User> editarRegistro(@Path("id") Long id, @Body User user,
                                          @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("registro/{id}/{centroId}")
    Call<User> editarRegistro(@Path("id") Long id, @Path("centroId") Long centroId,
                              @Body User user, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("user")
    Call<UserResponse> getUser(@Header("Authorization") String token);


    /* ======================= PROCESOS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/procesos")
    Call<ArrayList<Proceso>> getProcesos(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/procesos/{id}")
    Call<Proceso> getProceso(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/procesos/paciente/{id}")
    Call<ArrayList<Proceso>> getProcesosByPacienteId(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/procesos/{sanitarioId}")
    Call<Proceso> crearProceso(@Path("sanitarioId") Long id, @Body Proceso p, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/procesos/{id}")
    Call<Proceso> editarProceso(@Path("id") Long id, @Body Proceso p,
                                @Header("Authorization") String token);


    /* ======================= CURAS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/procesos/{id}/curas")
    Call<ArrayList<Cura>> getCurasByProcesoId(@Path("id") Long id,
                                              @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/curas")
    Call<Cura> crearCura(@Body Cura c, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/curas/{id}")
    Call<Cura> editarCura(@Path("id") Long id, @Body Cura c, @Header("Authorization") String token);


    /* ======================= CENTROS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/centros")
    Call<ArrayList<Centro>> getCentros(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/centros/recientes")
    Call<ArrayList<Centro>> getCentrosRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/centros/filtro")
    Call<ArrayList<Centro>> getCentrosByFiltro(@Query("filtro") String filtro,
                                           @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/centros/{id}")
    Call<Centro> getCentro(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/usercentros/user/{id}")
    Call<Centro> getCentroByUserId(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/centros/{id}")
    Call<String> borrarCentro(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/centros")
    Call<Centro> crearCentro(@Body Centro c, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/centros/{id}")
    Call<Centro> editarCentro(@Path("id") Long id, @Body Centro c,
                              @Header("Authorization") String token);


    /* ======================= SALAS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/salas")
    Call<ArrayList<Sala>> getSalas(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/salas/recientes")
    Call<ArrayList<Sala>> getSalasRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/salas/filtro")
    Call<ArrayList<Sala>> getSalasByFiltro(@Query("filtro") String filtro,
                                           @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/salas/{id}")
    Call<Sala> getSala(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/salas/{id}")
    Call<String> borrarSala(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/salas")
    Call<Sala> crearSala(@Body Sala s, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/salas/{id}")
    Call<Sala> editarSala(@Path("id") Long id, @Body Sala s, @Header("Authorization") String token);


    /* ======================= GRUPOS DIAGNOSTICOS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/gruposdiagnosticos")
    Call<ArrayList<Grupodiagnostico>> getGruposdiagnosticos(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/gruposdiagnosticos/filtro")
    Call<ArrayList<Grupodiagnostico>> getGruposdiagnosticosByFiltro(@Query("filtro") String filtro,
                                                                    @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/gruposdiagnosticos/{id}")
    Call<Grupodiagnostico> getGrupodiagnostico(@Path("id") Long id,
                                               @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/gruposdiagnosticos/{id}")
    Call<String> borrarGrupodiagnostico(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/gruposdiagnosticos")
    Call<Grupodiagnostico> crearGrupodiagnostico(@Body Grupodiagnostico gd,
                                                 @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/gruposdiagnosticos/{id}")
    Call<Grupodiagnostico> editarGrupodiagnostico(@Path("id") Long id, @Body Grupodiagnostico gd,
                                                  @Header("Authorization") String token);


    /* ======================= DIAGNOSTICOS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/diagnosticos")
    Call<ArrayList<Diagnostico>> getDiagnosticos(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/diagnosticos/filtro")
    Call<ArrayList<Diagnostico>> getDiagnosticosByFiltro(@Query("filtro") String filtro,
                                                         @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/diagnosticos/{id}")
    Call<Diagnostico> getDiagnostico(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/diagnosticos/{id}")
    Call<String> borrarDiagnostico(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/diagnosticos")
    Call<Diagnostico> crearDiagnostico(@Body Diagnostico d, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/diagnosticos/{id}")
    Call<Diagnostico> editarDiagnostico(@Path("id") Long id, @Body Diagnostico d,
                                        @Header("Authorization") String token);


    /* ======================= PROCEDIMIENTOS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/procedimientos")
    Call<ArrayList<Procedimiento>> getProcedimientos(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/procedimientos/filtro")
    Call<ArrayList<Procedimiento>> getProcedimientosByFiltro(@Query("filtro") String filtro,
                                                         @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/procedimientos/{id}")
    Call<Procedimiento> getProcedimiento(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/procedimientos/{id}")
    Call<String> borrarProcedimiento(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/procedimientos")
    Call<Procedimiento> crearProcedimiento(@Body Procedimiento p,
                                           @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("api/procedimientos/{id}")
    Call<Procedimiento> editarProcedimiento(@Path("id") Long id, @Body Procedimiento p,
                                        @Header("Authorization") String token);


    /* ======================= ADMINISTRADORES ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/administradores")
    Call<ArrayList<Administrador>> getAdministradores(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/administradores/recientes")
    Call<ArrayList<Administrador>> getAdministradoresRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/administradores/filtro")
    Call<ArrayList<Administrador>> getAdministradoresByFiltro(@Query("filtro") String filtro,
                                                             @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/administradores/{id}")
    Call<Administrador> getAdministrador(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/administradores/{id}")
    Call<String> borrarAdministrador(@Path("id") Long id, @Header("Authorization") String token);


    /* ======================= SANITARIOS ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/sanitarios")
    Call<ArrayList<Sanitario>> getSanitarios(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/sanitarios/recientes")
    Call<ArrayList<Sanitario>> getSanitariosRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/sanitarios/filtro")
    Call<ArrayList<Sanitario>> getSanitariosByFiltro(@Query("filtro") String filtro,
                                                     @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/sanitarios/{id}")
    Call<Sanitario> getSanitario(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/sanitarios/email/{email}")
    Call<Sanitario> getSanitarioByEmail(@Path("email") String email, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/sanitarios/{id}")
    Call<String> borrarSanitario(@Path("id") Long id, @Header("Authorization") String token);


    /* ======================= PACIENTES ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/pacientes")
    Call<ArrayList<Paciente>> getPacientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/pacientes/recientes")
    Call<ArrayList<Paciente>> getPacientesRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/pacientes/filtro")
    Call<ArrayList<Paciente>> getPacientesByFiltro(@Query("filtro") String filtro,
                                                     @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/pacientes/{id}")
    Call<Paciente> getPaciente(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("api/pacientes/{id}")
    Call<String> borrarPaciente(@Path("id") Long id, @Header("Authorization") String token);


    /* ======================= VALORACIONES ======================= */
    @Headers("Content-Type: application/json")
    @GET("api/valoraciones")
    Call<ArrayList<Valoracion>> getValoraciones(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/recientes")
    Call<ArrayList<Valoracion>> getValoracionesRecientes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/filtro")
    Call<ArrayList<Valoracion>> getValoracionesByFiltro(@Query("filtro") String filtro,
                                                        @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/{id}")
    Call<Valoracion> getValoracion(@Path("id") Long id, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/sanitario/{id}")
    Call<ArrayList<Valoracion>> getValoracionesBySanitarioId(@Path("id") Long id,
                                                             @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/media")
    Call<ArrayList<ValoracionesResults>> getAVGValoraciones(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("api/valoraciones/media/filtro")
    Call<ArrayList<ValoracionesResults>> getValoracionesResultsByFiltro(@Query("filtro") String filtro,
                                                   @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/valoraciones")
    Call<Valoracion> crearValoracion(@Body Valoracion v, @Header("Authorization") String token);


}
