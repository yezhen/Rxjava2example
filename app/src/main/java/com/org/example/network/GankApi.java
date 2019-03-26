package com.org.example.network;

import com.org.example.model.GankResultBeauty;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface GankApi {
    @GET("data/福利/{number}/{page}")
    Observable<GankResultBeauty> getBeauties(@Path("number") int number, @Path("page") int page);
}
