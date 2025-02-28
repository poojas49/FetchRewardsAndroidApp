package com.example.fetchrewards.network;

import com.example.fetchrewards.Item;
import com.squareup.moshi.Moshi;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import java.util.List;

public class ApiService {
    private static final String BASE_URL = "https://fetch-hiring.s3.amazonaws.com/";
    private static ApiService instance;
    private final ItemApiService service;

    private ApiService() {
        Moshi moshi = new Moshi.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        service = retrofit.create(ItemApiService.class);
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public Call<List<Item>> getItems() {
        return service.getItems();
    }

    public interface ItemApiService {
        @GET("hiring.json")
        Call<List<Item>> getItems();
    }
}
