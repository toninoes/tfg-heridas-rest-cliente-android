package uca.ruiz.antonio.tfgapp.data.api.io;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class MyApiAdapter {

    private static MyApiService API_SERVICE = null;
    private static String BASEURL = Pref.getBaseUrl();

    public static MyApiService getApiService() {

        // Creamos un interceptor y le indicamos el log level a usar
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Asociamos el interceptor a las peticiones
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String baseUrl = Pref.getBaseUrl();

        /*
         * Patrón Singleton, sólo creo instancia Retrofit nueva si no hay creada una previamente
         * o si se cambia la ip, puerto o protocolo del servidor en las preferencias.
         */
        if (API_SERVICE == null || !baseUrl.equals(BASEURL) ) {
            BASEURL = baseUrl;

            Log.d("###### RETROFIT ###### ", "Creando instancia NUEVA Retrofit para: " + baseUrl);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build()) // <-- usamos el log level
                    .build();
            API_SERVICE = retrofit.create(MyApiService.class);
        } else {
            Log.d("###### RETROFIT ###### ", "Reutilizando instancia Retrofit: " + baseUrl);
        }

        return API_SERVICE;
    }

}
